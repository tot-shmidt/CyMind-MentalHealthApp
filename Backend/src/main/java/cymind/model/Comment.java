package cymind.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String content;

    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student author;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    public Comment(String content, Student author, Article article) {
        this.content = content;
        this.author = author;
        this.article = article;
        creationDate = LocalDateTime.now();
    }
}
