package telran.java58.accounting.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserEditDto {
    @Size(min = 2, max = 20, message = "firstName must be between 2 and 20 characters")
    private String firstName;
    @Size(min = 2, max = 20, message = "lastName must be between 2 and 20 characters")
    private String lastName;
}
