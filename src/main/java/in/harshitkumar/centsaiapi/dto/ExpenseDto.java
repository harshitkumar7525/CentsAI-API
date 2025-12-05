package in.harshitkumar.centsaiapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private Long id;
    private Double amount;
    private LocalDate transactionDate;
    private String category;
}
