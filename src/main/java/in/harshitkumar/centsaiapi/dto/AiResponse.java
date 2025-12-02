package in.harshitkumar.centsaiapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiResponse {
    private List<ExpenseDto> expenses;
}
