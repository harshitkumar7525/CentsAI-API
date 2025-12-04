package in.harshitkumar.centsaiapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private Double amount;
    private String category;
    private Date date;
}
