package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.ExpenseDto;
import in.harshitkumar.centsaiapi.dto.TransactionRequest;
import in.harshitkumar.centsaiapi.dto.TransactionResponse;
import in.harshitkumar.centsaiapi.exception.UserNotFound;
import in.harshitkumar.centsaiapi.models.Expenses;
import in.harshitkumar.centsaiapi.models.User;
import in.harshitkumar.centsaiapi.repository.ExpenseRepository;
import in.harshitkumar.centsaiapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ResponseEntity<TransactionResponse> addTransaction(Long userId, TransactionRequest request) {
        log.info("TransactionService: Saving data for userId {}", userId);

        if (request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + userId));

        Date expenseDate = (request.getDate() != null) ? request.getDate() : new Date();

        Expenses expense = Expenses.builder()
                .user(user)
                .date(expenseDate)
                .amount(request.getAmount())
                .category(request.getCategory())
                .build();

        expenseRepository.save(expense);
        log.info("TransactionService: Saved expense {} for userId {}", expense, userId);

        LocalDate txDate = expense.getDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        ExpenseDto dto = ExpenseDto.builder()
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .transactionDate(txDate)
                .build();

        TransactionResponse response = TransactionResponse.builder()
                .userId(userId)
                .expenses(Collections.singletonList(dto))
                .build();

        return ResponseEntity.ok(response);
    }
}
