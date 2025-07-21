package telran.java58.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewPostDto {
    @NotBlank(message = "title is required")
    @Size(min = 3, max = 255, message = "title must be between 3 and 255 characters")
    private String title;
    @NotBlank(message = "content is required")
    @Size(min = 10, message = "content must be at least 10 symbols")
    private String content;
    private Set<String> tags = new HashSet<>();
}
