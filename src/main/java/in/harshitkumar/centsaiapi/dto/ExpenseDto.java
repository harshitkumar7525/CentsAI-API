package in.harshitkumar.centsaiapi.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseDto {
    private double amount;
    private LocalDate transactionDate;
    private String category;
}
