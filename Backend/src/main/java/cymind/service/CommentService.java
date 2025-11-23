package cymind.service;

import cymind.dto.comments.CommentDTO;
import cymind.dto.comments.CreateCommentDTO;
import cymind.enums.UserType;
import cymind.exceptions.AuthorizationDeniedException;
import cymind.model.AbstractUser;
import cymind.model.Article;
import cymind.model.Comment;
import cymind.model.Student;
import cymind.repository.ArticleRepository;
import cymind.repository.CommentRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public CommentDTO addComment(Long articleId, CreateCommentDTO dto) {
        checkAuth(dto.userId());

        Article article = articleRepository.findArticleById(articleId);
        if (article == null) {
            throw new EntityNotFoundException("Article not found");
        }

        Student student = studentRepository.findByAbstractUserId(dto.userId());
        if (student == null) {
            throw new EntityNotFoundException("Student not found");
        }

        Comment comment = new Comment(dto.content(), student, article);
        Comment savedComment = commentRepository.save(comment);

        return mapToDTO(savedComment);
    }

    @Transactional
    public CommentDTO updateComment(Long commentId, CreateCommentDTO dto) {
        Comment comment = commentRepository.findById(commentId.longValue());

        if (comment == null) {
            throw new EntityNotFoundException("Comment not found");
        }

        checkAuth(dto.userId());

        // This is to check if a student deletes its own comment!!!
        if (!comment.getAuthor().getAbstractUser().getId().equals(dto.userId())) {
            throw new AuthorizationDeniedException("You can only edit your own comments");
        }

        comment.setContent(dto.content());
        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId.longValue());

        if (comment == null) {
            throw new EntityNotFoundException("Comment not found");
        }

        checkAuth(userId);

        AbstractUser currentUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAuthor = comment.getAuthor().getAbstractUser().getId().equals(userId);
        boolean isProfessional = currentUser.getUserType() == UserType.PROFESSIONAL;

        if (!isAuthor && !isProfessional) {
            throw new AuthorizationDeniedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentDTO mapToDTO(Comment c) {
        return new CommentDTO(
                c.getId(),
                c.getContent(),
                c.getAuthor().getAbstractUser().getFirstName() + " " + c.getAuthor().getAbstractUser().getLastName(),
                c.getAuthor().getId(),
                c.getCreationDate()
        );
    }

    private void checkAuth(Long userId) {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != userId && authedUser.getUserType() != UserType.PROFESSIONAL) {
            throw new AuthorizationDeniedException("You are not authorized to perform this action.");
        }
    }
}
