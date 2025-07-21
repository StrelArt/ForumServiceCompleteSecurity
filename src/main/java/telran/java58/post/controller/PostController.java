package telran.java58.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.java58.post.dto.NewCommentDto;
import telran.java58.post.dto.NewPostDto;
import telran.java58.post.dto.PostDto;
import telran.java58.post.service.PostService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forum")
public class PostController {

    private final PostService postService;

    @PostMapping("/post/{author}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto addNewPost(@PathVariable String author, @RequestBody @Valid NewPostDto newPostDto) {
        return postService.addNewPost(author, newPostDto);
    }

    @GetMapping("/post/{id}")
    public PostDto findPostById(@PathVariable String id) {
        return postService.findPostById(id);
    }

    @PatchMapping("/post/{id}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable String id) {
        postService.addLike(id);
    }

    @PatchMapping("/post/{id}")
    public PostDto updatePost(@PathVariable String id, @RequestBody NewPostDto newPostDto) {
        return postService.updatePost(id, newPostDto);
    }

    @DeleteMapping("/post/{id}")
    public PostDto deletePost(@PathVariable String id) {
        return postService.deletePost(id);
    }

    @PatchMapping("/post/{id}/comment/{author}")
    public PostDto addComment(@PathVariable String id, @PathVariable String author, @RequestBody @Valid NewCommentDto newCommentDto) {
        return postService.addComment(id, author, newCommentDto);
    }

    @GetMapping("/posts/author/{author}")
    public Iterable<PostDto> findPostsByAuthor(@PathVariable String author) {
        return postService.findPostsByAuthor(author);
    }

    @GetMapping("/posts/tags")
    public Iterable<PostDto> findPostsByTags(@RequestParam("values") List<String> tags) {
        return postService.findPostsByTags(tags);
    }

    @GetMapping("/posts/period")
    public Iterable<PostDto> findPostsByPeriod(@RequestParam("dateFrom") LocalDate from, @RequestParam("dateTo") LocalDate to) {
        return postService.findPostsByPeriod(from, to);
    }
}
