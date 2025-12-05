package in.harshitkumar.centsaiapi.dto;

import in.harshitkumar.centsaiapi.models.Expenses;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTransactions {
    private Long userId;
    private List<ExpenseDto> allExpenses;
}
