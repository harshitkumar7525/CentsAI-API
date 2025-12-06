package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.AiResponse;
import in.harshitkumar.centsaiapi.dto.ExpenseDto;
import in.harshitkumar.centsaiapi.dto.UserPrompt;
import in.harshitkumar.centsaiapi.exception.AiMicroserviceNotWorking;
import in.harshitkumar.centsaiapi.exception.UserNotFound;
import in.harshitkumar.centsaiapi.models.Expenses;
import in.harshitkumar.centsaiapi.repository.ExpenseRepository;
import in.harshitkumar.centsaiapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ExpenseRepository expensesRepository;

    @Value("${fastapi.url}")
    private String url;

    public Object extractData(String prompt) {

        log.info("AiService: Sending prompt to FastAPI: {}", prompt);

        try {
            Mono<Object> response = webClient.post()
                    .uri(url)
                    .bodyValue(Map.of("prompt", prompt))
                    .retrieve()
                    .bodyToMono(Object.class);

            return response.block();

        } catch (Exception e) {
            log.error("AiService: Error calling FastAPI microservice", e);
            throw new AiMicroserviceNotWorking("FastAPI call failed: " + e.getMessage());
        }
    }

    public AiResponse objectToAiResponse(Long userId, Object obj) {
        log.info("AiService: Converting object to AiResponse");
        ObjectMapper mapper = new ObjectMapper();

        String json = obj.toString();

        List<ExpenseDto> expenses =
                mapper.readValue(json, new TypeReference<List<ExpenseDto>>() {
                });

        return AiResponse.builder().userId(userId).expenses(expenses).build();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


    public AiResponse extractData(Long userId, UserPrompt userPrompt) {
        log.info("AiController: Extracting data from user prompt");
        return objectToAiResponse(userId, extractData(userPrompt.getPrompt()));
    }

    @Transactional
    public ResponseEntity<AiResponse> saveData(Long userId, UserPrompt userPrompt) {
        log.info("AiService: Saving data for userId {}", userId);

        AiResponse convertedData = extractData(userId, userPrompt);

        List<ExpenseDto> expenseDtos = convertedData.getExpenses().stream()
                .filter(dto -> dto.getAmount() != null && dto.getAmount() > 0)
                .toList();

        if (expenseDtos.isEmpty()) {
            log.info("AiService: No valid expenses to save for userId {}", userId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + userId));

        List<Expenses> expenseEntities = expenseDtos.stream()
                .map(dto -> Expenses.builder()
                        .amount(dto.getAmount())
                        .category(capitalize(dto.getCategory()))
                        .date(dto.getTransactionDate())
                        .user(user)
                        .build())
                .toList();

        expensesRepository.saveAll(expenseEntities);

        log.info("AiService: Saved {} expenses for userId {}", expenseEntities.size(), userId);
        return ResponseEntity.status(201).body(convertedData);
    }


}
