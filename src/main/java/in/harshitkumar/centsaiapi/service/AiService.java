package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.AiResponse;
import in.harshitkumar.centsaiapi.dto.ExpenseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value( "${fastapi.url}")
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
            throw new RuntimeException("FastAPI call failed: " + e.getMessage());
        }
    }

    public ResponseEntity<AiResponse> objectToAiResponse(Object obj) {
        log.info("AiService: Converting object to AiResponse");
        try {
            ObjectMapper mapper = new ObjectMapper();

            String json = obj.toString();

            List<ExpenseDto> expenses =
                    mapper.readValue(json, new TypeReference<List<ExpenseDto>>() {});

            AiResponse response = new AiResponse();
            response.setExpenses(expenses);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("AiService: Error converting object to AiResponse, error {}",ex.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

}
