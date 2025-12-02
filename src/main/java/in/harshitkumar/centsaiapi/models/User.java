package in.harshitkumar.centsaiapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users",
        indexes = {@Index(columnList = "email")}
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Column(name = "user_name", nullable = false)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Column
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Column(unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Expenses> expenses;
}
