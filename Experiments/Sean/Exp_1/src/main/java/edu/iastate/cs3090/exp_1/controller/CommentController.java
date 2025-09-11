package edu.iastate.cs3090.exp_1.controller;

import edu.iastate.cs3090.exp_1.blog.CommentManager;
import edu.iastate.cs3090.exp_1.model.Comment;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;
import java.util.HashMap;

/**
 * GET    /comments
 * GET    /comments/:id
 * POST   /comments
 * PUT    /comments/:id
 * DELETE /comments/:id
 */
@RestController
public class CommentController {
    @GetMapping("/comments")
    public HashMap<Integer, Comment> getComment() {
        return CommentManager.getComments();
    }

    @GetMapping("/comments/{id}")
    public Comment getComment(@PathVariable Integer id) throws KeyException {
        return CommentManager.getComment(id);
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@Valid @RequestBody Comment comment) {
        return CommentManager.addComment(comment);
    }

    @PutMapping("/comments/{id}")
    public Comment updateComment(@PathVariable Integer id, @Valid @RequestBody Comment comment) throws KeyException {
        CommentManager.replacePost(id, comment);
        return comment;
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer id) throws KeyException {
        CommentManager.deletePost(id);
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
