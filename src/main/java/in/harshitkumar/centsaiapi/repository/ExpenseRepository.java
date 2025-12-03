package in.harshitkumar.centsaiapi.repository;

import in.harshitkumar.centsaiapi.models.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses, Long> {
}
