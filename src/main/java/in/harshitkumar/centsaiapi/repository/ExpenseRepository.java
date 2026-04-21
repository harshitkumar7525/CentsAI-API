package in.harshitkumar.centsaiapi.repository;

import in.harshitkumar.centsaiapi.models.Expenses;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expenses, String> {
    List<Expenses> findByUserId(String userId);
}