package telran.java58.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.java58.post.dao.PostRepository;
import telran.java58.post.model.Post;

@Service
@RequiredArgsConstructor
public class CustomWebSecurity {
    private final PostRepository repository;

    public boolean isPostAuthor(String login, String postId) {
        Post post = repository.findById(postId).orElse(null);
        return post != null && post.getAuthor().equalsIgnoreCase(login);

    }
}
