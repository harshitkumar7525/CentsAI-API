package in.harshitkumar.centsaiapi.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseDto {
    private Double amount;
    private LocalDate transactionDate;
    private String category;
}
