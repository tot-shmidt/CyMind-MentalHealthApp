package edu.iastate.cs3090.exp_1.controller;

import edu.iastate.cs3090.exp_1.blog.BlogPostManager;
import edu.iastate.cs3090.exp_1.model.BlogPost;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;
import java.util.HashMap;

/**
 * GET    /posts
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

    @GetMapping("/posts/{id}")
    public BlogPost getPost(@PathVariable Integer id) throws KeyException {
        return BlogPostManager.getPost(id);
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
