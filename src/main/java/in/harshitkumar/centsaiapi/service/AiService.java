package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.AiResponse;
import in.harshitkumar.centsaiapi.dto.ExpenseDto;
import in.harshitkumar.centsaiapi.dto.UserPrompt;
import in.harshitkumar.centsaiapi.exception.AiMicroserviceNotWorking;
import in.harshitkumar.centsaiapi.exception.UserNotFound;
import in.harshitkumar.centsaiapi.models.Expenses;
import in.harshitkumar.centsaiapi.repository.ExpenseRepository;
import in.harshitkumar.centsaiapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ExpenseRepository expensesRepository;
    private final ObjectMapper mapper;

    @Value("${microservice.uri}")
    private String url;

    public Object extractData(String prompt) {
        log.info("AiService: Sending prompt to AI service");

        try {
            Mono<Object> response = webClient.post()
                    .uri(url)
                    .bodyValue(Map.of("prompt", prompt))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .retryWhen(Retry.backoff(1, Duration.ofMillis(500)))
                    .timeout(Duration.ofSeconds(20));

            return response.block();

        } catch (Exception e) {
            log.error("AiService: Error calling AI microservice", e);
            throw new AiMicroserviceNotWorking("AI service is not responding, please try again");
        }
    }

    public AiResponse objectToAiResponse(String userId, Object obj) {

        List<ExpenseDto> expenses = mapper.convertValue(
                obj,
                new TypeReference<List<ExpenseDto>>() {}
        );

        return AiResponse.builder()
                .userId(userId)
                .expenses(expenses)
                .build();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public AiResponse extractData(String userId, UserPrompt userPrompt) {
        log.info("AiService: Extracting structured data from prompt");
        return objectToAiResponse(userId, extractData(userPrompt.getPrompt()));
    }

    public ResponseEntity<AiResponse> saveData(String userId, UserPrompt userPrompt) {

        log.info("AiService: Saving AI extracted data for userId {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + userId));

        AiResponse convertedData = extractData(userId, userPrompt);

        List<ExpenseDto> expenseDtos = convertedData.getExpenses().stream()
                .filter(dto -> dto.getAmount() != null && dto.getAmount() > 0)
                .toList();

        if (expenseDtos.isEmpty()) {
            log.info("AiService: No valid expenses to save");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Expenses> expenseEntities = expenseDtos.stream()
                .map(dto -> Expenses.builder()
                        .amount(dto.getAmount())
                        .category(capitalize(dto.getCategory()))
                        .date(dto.getTransactionDate())
                        .userId(userId)
                        .build())
                .toList();

        expensesRepository.saveAll(expenseEntities);

        log.info("AiService: Saved {} expenses for userId {}", expenseEntities.size(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertedData);
    }
}