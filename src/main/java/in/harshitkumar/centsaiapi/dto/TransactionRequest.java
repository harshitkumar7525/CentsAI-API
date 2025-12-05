package in.harshitkumar.centsaiapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private Double amount;
    private String category;
    private LocalDate date;
}
