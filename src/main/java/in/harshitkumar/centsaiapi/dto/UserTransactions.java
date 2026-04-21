package in.harshitkumar.centsaiapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTransactions {
    private String userId;
    private List<ExpenseDto> allExpenses;
}
