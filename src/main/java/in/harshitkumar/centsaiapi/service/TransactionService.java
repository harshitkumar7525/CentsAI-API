package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.ExpenseDto;
import in.harshitkumar.centsaiapi.dto.TransactionRequest;
import in.harshitkumar.centsaiapi.dto.TransactionResponse;
import in.harshitkumar.centsaiapi.exception.NotAuthorizedError;
import in.harshitkumar.centsaiapi.exception.TransactionNotFound;
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
import java.util.List;

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

    public ResponseEntity<?> deleteTransaction(Long userId, Long transactionId) {
        log.info("TransactionService: Deleting transaction {} for userId {}", transactionId, userId);

        Expenses expense = expenseRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFound("Requested transaction not found: " + transactionId));

        if (!expense.getUser().getId().equals(userId)) {
            throw new NotAuthorizedError("You are not authorized to delete this transaction");
        }

        expenseRepository.delete(expense);
        return ResponseEntity.ok("Transaction deleted successfully");
    }


    public ResponseEntity<?> updateTransaction(Long userId, Long transactionId, TransactionRequest transactionRequest) {
        log.info("TransactionService: Updating transaction {} for userId {}", transactionId, userId);

        Expenses expense = expenseRepository.findById(transactionId).orElseThrow(() -> new TransactionNotFound("Requested transaction not found: " + transactionId));

        if (!expense.getUser().getId().equals(userId)) {
            throw new NotAuthorizedError("You are not authorized to update this transaction");
        }

        expense.setAmount(transactionRequest.getAmount());
        expense.setCategory(transactionRequest.getCategory());
        expense.setDate(transactionRequest.getDate());
        expenseRepository.save(expense);
        return ResponseEntity.ok("Transaction updated successfully");
    }

    public TransactionResponse retrieveTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + userId));

        List<Expenses> expenses = user.getExpenses();

        return TransactionResponse.builder().userId(userId).userExpenses(expenses).build();
    }
}
