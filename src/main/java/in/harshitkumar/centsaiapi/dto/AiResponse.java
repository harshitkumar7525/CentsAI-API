package in.harshitkumar.centsaiapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class AiResponse {
    private Long userId;
    private List<ExpenseDto> expenses;
}
