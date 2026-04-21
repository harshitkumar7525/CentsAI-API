package in.harshitkumar.centsaiapi.service;

import in.harshitkumar.centsaiapi.dto.ExpenseDto;
import in.harshitkumar.centsaiapi.dto.TransactionRequest;
import in.harshitkumar.centsaiapi.dto.TransactionResponse;
import in.harshitkumar.centsaiapi.dto.UserTransactions;
import in.harshitkumar.centsaiapi.exception.NotAuthorizedError;
import in.harshitkumar.centsaiapi.exception.TransactionNotFound;
import in.harshitkumar.centsaiapi.exception.UserNotFound;
import in.harshitkumar.centsaiapi.models.Expenses;
import in.harshitkumar.centsaiapi.repository.ExpenseRepository;
import in.harshitkumar.centsaiapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ResponseEntity<TransactionResponse> addTransaction(String userId, TransactionRequest request) {
        log.info("TransactionService: Saving data for userId {}", userId);

        if (request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + userId));

        LocalDate expenseDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        Expenses expense = Expenses.builder()
                .userId(userId)
                .date(expenseDate)
                .amount(request.getAmount())
                .category(request.getCategory())
                .build();

        expenseRepository.save(expense);

        log.info("TransactionService: Saved expense with expenseId {} for userId {}", expense.getId(), userId);

        ExpenseDto dto = ExpenseDto.builder()
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .transactionDate(expense.getDate())
                .id(expense.getId())
                .build();

        TransactionResponse response = TransactionResponse.builder()
                .userId(userId)
                .expenses(Collections.singletonList(dto))
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deleteTransaction(String userId, String transactionId) {
        log.info("TransactionService: Deleting transaction {} for userId {}", transactionId, userId);

        Expenses expense = expenseRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("TransactionService: Transaction not found for id {}", transactionId);
                    return new TransactionNotFound("Requested transaction not found: " + transactionId);
                });

        if (!expense.getUserId().equals(userId)) {
            log.error("TransactionService: User {} is not authorized to delete transaction {}", userId, transactionId);
            throw new NotAuthorizedError("You are not authorized to delete this transaction");
        }

        expenseRepository.delete(expense);

        log.info("TransactionService: Deleted transaction {} for userId {}", transactionId, userId);

        return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
    }

    public ResponseEntity<?> updateTransaction(String userId, String transactionId, TransactionRequest request) {
        log.info("TransactionService: Updating transaction {} for userId {}", transactionId, userId);

        Expenses expense = expenseRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("TransactionService: Transaction not found for id {}", transactionId);
                    return new TransactionNotFound("Requested transaction not found: " + transactionId);
                });

        if (!expense.getUserId().equals(userId)) {
            log.error("TransactionService: User {} is not authorized to update transaction {}", userId, transactionId);
            throw new NotAuthorizedError("You are not authorized to update this transaction");
        }

        if (request.getAmount() != null) {
            expense.setAmount(request.getAmount());
        }

        if (request.getCategory() != null) {
            expense.setCategory(request.getCategory());
        }

        if (request.getDate() != null) {
            expense.setDate(request.getDate());
        }

        expenseRepository.save(expense);

        log.info("TransactionService: Updated transaction {} for userId {}", transactionId, userId);

        return ResponseEntity.ok(Map.of("message", "Transaction updated successfully"));
    }

    public UserTransactions retrieveTransactions(String userId) {
        log.info("TransactionService: Retrieving transactions for userId {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("TransactionService: User not found for id {}", userId);
                    return new UserNotFound("User not found with id: " + userId);
                });

        List<Expenses> expenses = expenseRepository.findByUserId(userId);

        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(expense -> ExpenseDto.builder()
                        .transactionDate(expense.getDate())
                        .amount(expense.getAmount())
                        .category(expense.getCategory())
                        .id(expense.getId())
                        .build()
                )
                .toList();

        log.info("TransactionService: Retrieved {} transactions for userId {}", expenses.size(), userId);

        return UserTransactions.builder()
                .userId(userId)
                .allExpenses(expenseDtos)
                .build();
    }
}