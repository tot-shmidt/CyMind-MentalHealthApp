package edu.iastate.cs3090.exp_1.controller;

import edu.iastate.cs3090.exp_1.blog.BlogPostManager;
import edu.iastate.cs3090.exp_1.blog.CommentManager;
import edu.iastate.cs3090.exp_1.model.BlogPost;
import edu.iastate.cs3090.exp_1.model.Comment;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * GET    /posts
 * GET    /posts?:id
 * GET    /posts/:id
 * POST   /posts
 * PUT    /posts/:id
 * DELETE /posts/:id
 */
@RestController
public class PostController {
    @GetMapping("/posts")
    public HashMap<Integer, BlogPost> getPosts() {
            return BlogPostManager.getPosts();
    }

    @GetMapping(value = "/posts", params = "id")
    public BlogPost getPosts(@RequestParam() Integer id) throws KeyException {
        return BlogPostManager.getPost(id);
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Integer id) throws KeyException {
        StringBuilder fullPost = new StringBuilder();
        BlogPost post = BlogPostManager.getPost(id);

        fullPost.append(post.getTitle());
        if (post.getDate() != null) {
            fullPost.append(" (").append(post.getDate()).append(")");
        }
        fullPost.append("\n");

        fullPost.append(post.getAuthor().getUsername());
        if (post.getAuthor().getEmail() != null) {
            fullPost.append(" (").append(post.getAuthor().getEmail()).append(")\n");
        }
        fullPost.append("\n");

        fullPost.append(post.getContent()).append("\n");

        ArrayList<Comment> comments = CommentManager.getCommentsByPost(id);
        if (!comments.isEmpty()) {
            fullPost.append("\nComments:\n");
            for (Comment comment : comments) {
                fullPost.append(post.getAuthor().getUsername());
                if (post.getAuthor().getEmail() != null) {
                    fullPost.append(" (").append(post.getAuthor().getEmail());
                }

                fullPost.append("\n")
                        .append(comment.getContent())
                        .append("\n\n");
            }
        }

        return fullPost.toString();
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPost createPost(@Valid @RequestBody BlogPost post) {
        return BlogPostManager.addPost(post);
    }

    @PutMapping("/posts/{id}")
    public BlogPost updatePost(@PathVariable Integer id, @Valid @RequestBody BlogPost post) throws KeyException {
        BlogPostManager.replacePost(id, post);
        return post;
    }

    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Integer id) throws KeyException {
        BlogPostManager.deletePost(id);
    }

    @ExceptionHandler(KeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleKeyException(KeyException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNotValidException(Exception e) {
        return "Missing required fields";
    }
}
