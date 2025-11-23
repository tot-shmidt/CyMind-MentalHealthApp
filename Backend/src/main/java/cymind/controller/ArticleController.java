package cymind.controller;

import cymind.dto.comments.CommentDTO;
import cymind.dto.comments.CreateCommentDTO;
import cymind.dto.user.ProfessionalDTO;
import cymind.dto.article.ArticleDTO;
import cymind.dto.article.CreateArticleDTO;
import cymind.model.Article;
import cymind.model.Exercise;
import cymind.service.ArticleService;
import cymind.service.CommentService;
import cymind.service.ProfessionalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ProfessionalService professionalService;

    @Autowired
    private CommentService commentService;

    /**
     * Professional user creates a new article.
     */
    @PostMapping("/resources/articles")
    ResponseEntity<ArticleDTO> createArticle(@Valid @RequestBody CreateArticleDTO createArticleDTO) {
        return new ResponseEntity<>(articleService.createNewArticle(createArticleDTO), HttpStatus.CREATED);
    }

    /**
     * Get particular article by its id.
     */
    @GetMapping("/resources/articles/{id}")
    ResponseEntity<ArticleDTO> getArticleById(@PathVariable long id) {
        return new ResponseEntity<>(articleService.getArticleById(id), HttpStatus.OK);
    }

    /**
     * Update particular article by its id.
     */
    @PutMapping("/resources/articles/{id}")
    ResponseEntity<ArticleDTO> updateArticle(@PathVariable long id, @Valid @RequestBody CreateArticleDTO createArticleDTO) {
        return new ResponseEntity<>(articleService.updateArticle(id, createArticleDTO), HttpStatus.OK);
    }

    /**
     * Delete article by its id. Only professional can do it.
     */
    @DeleteMapping("/resources/articles/{id}")
    ResponseEntity<?> deleteArticle(@PathVariable long id,
                                         @RequestParam("userId") long userId) {
        articleService.deleteArticle(id, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get specified number of articles.
     * @param number The number of articles to retrieve
     * @return List of articles
     */
    @GetMapping("/resources/articles/limit/{number}")
    ResponseEntity<List<ArticleDTO>> getNumberOfArticles(@PathVariable int number) {
        return new ResponseEntity<>(articleService.getNumberOfArticles(number), HttpStatus.OK);
    }

    /**
     * Get exercises for specified article by its {id} 
     * @param id
     * @return
     */
    @GetMapping("/resources/articles/{id}/exercises")
    ResponseEntity<List<Exercise>> getExercisesForArticle(@PathVariable long id) {
        List<Exercise> exercises = articleService.getExercisesForArticle(id);
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }

    /**
     * Get paginated articles. Examples:
     * /resources/articles/page -> Returns first 10 articles
     * /resources/articles/page?size=5 -> Returns first 5 articles
     * /resources/articles/page?page=1 -> Returns second page of 10 articles
     * /resources/articles/page?page=2&size=5 -> Returns third page with 5 articles per page
     * 
     * @param page Page number (0-based). Default is 0 (first page)
     * @param size Number of articles per page. Default is 10
     * @return List of articles for the requested page
     */
    @GetMapping("/resources/articles/page")
    ResponseEntity<List<ArticleDTO>> getPaginatedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(articleService.getPaginatedArticles(page, size), HttpStatus.OK);
    }

    /**
     * Search articles by category
     * @param category The category to search for
     * @return List of matching articles
     */
    @GetMapping("/resources/articles/category/{category}")
    ResponseEntity<List<ArticleDTO>> getArticlesByCategory(@PathVariable String category) {
        return new ResponseEntity<>(articleService.getArticlesByCategory(category), HttpStatus.OK);
    }

    // ~~~ Endpoints for comments ~~~
    /**
     * Add a comment to an article.
     */
    @PostMapping("/resources/articles/{articleId}/comments")
    ResponseEntity<CommentDTO> createComment(@PathVariable long id, @Valid @RequestBody CreateCommentDTO createCommentDTO) {
        return new ResponseEntity<>(commentService.addComment(id, createCommentDTO), HttpStatus.CREATED);
    }

    @PutMapping("/resources/articles/comments/{commentId}")
    ResponseEntity<CommentDTO> updateComment(@PathVariable long commentId, @Valid @RequestBody CreateCommentDTO createCommentDTO) {
        return new ResponseEntity<>(commentService.updateComment(commentId, createCommentDTO), HttpStatus.OK);
    }

    @DeleteMapping("/resources/articles/comments/{commentId}")
    ResponseEntity<?> deleteComment(@PathVariable long commentId, @RequestParam("userId") long userId) {
        commentService.deleteComment(commentId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
