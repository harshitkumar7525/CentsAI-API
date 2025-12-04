package in.harshitkumar.centsaiapi.dto;

import in.harshitkumar.centsaiapi.models.Expenses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long userId;
    private List<ExpenseDto> expenses;
    private List<Expenses> userExpenses;
}
