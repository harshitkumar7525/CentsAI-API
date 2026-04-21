package in.harshitkumar.centsaiapi.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "expenses")
public class Expenses {

    @Id
    private String id;   // Mongo uses String (ObjectId)

    private Double amount;

    private String category;

    private LocalDate date;

    private String userId; // store reference manually
}