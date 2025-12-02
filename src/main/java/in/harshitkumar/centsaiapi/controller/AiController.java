package in.harshitkumar.centsaiapi.controller;

import in.harshitkumar.centsaiapi.dto.AiResponse;
import in.harshitkumar.centsaiapi.dto.UserPrompt;
import in.harshitkumar.centsaiapi.service.AiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@AllArgsConstructor
@Slf4j
public class AiController {
    private final AiService aiService;

    @PostMapping("/extract")
    public ResponseEntity<AiResponse> extractData(@RequestBody UserPrompt userPrompt) {
        log.info("AiController: Extracting data from user prompt");
        return aiService.objectToAiResponse(aiService.extractData(userPrompt.getPrompt()));
    }

}
