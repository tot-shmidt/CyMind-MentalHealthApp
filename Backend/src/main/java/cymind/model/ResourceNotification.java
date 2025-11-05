package cymind.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ResourceNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime timestamp;

    // --- Relationship added here ---
    @ManyToOne(fetch = FetchType.EAGER) // Eager is okay here as we usually need the ID immediately for the DTO
    @JoinColumn(name = "article_id")
    private Article relatedArticle;

    public ResourceNotification(String message, Article article) {
        this.message = message;
        this.relatedArticle = article;
        this.timestamp = LocalDateTime.now();
    }
}
