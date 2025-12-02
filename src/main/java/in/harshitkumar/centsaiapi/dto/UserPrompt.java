package in.harshitkumar.centsaiapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPrompt {
    private String prompt;
}
