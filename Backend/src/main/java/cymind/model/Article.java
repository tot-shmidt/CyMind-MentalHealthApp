package cymind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // Like "How to fight stress vol.2"
    @NotBlank(message = "Resource name cannot be blank")
    private String articleName;

    // Hashtag-like descriptions as on Screen Sketches, like #Meditation
    private String category1;
    private String category2;
    private String category3;

    // Content of the article
    private String content;

    //Authors of the article
    @ManyToMany
    @JoinTable(
            name = "article_authors",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "professional_id")
    )
    @JsonIgnore
    @NotNull(message = "There has to be at least one creator")
    private List<MentalHealthProfessional> authors = new ArrayList<>();

    // List of exercises sutable for this article
    @ManyToMany
    @JoinTable(
            name = "article_exercises",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private List<Exercise> relatedExercises = new ArrayList<>();

    /**
     *  ~~~ Constructor starts HERE ~~~
     */
    public Article(String articleName, String category1, String category2, String category3,
                   String content, MentalHealthProfessional author) {

        if (author == null) {
            throw new NullPointerException("Author is null");
        }

        this.articleName = articleName;
        this.category1 = category1;
        this.category2 = category2;
        this.category3 = category3;
        this.content = content;

        // Add authors to the article and other direction relationship
        this.authors.add(author);
        author.getArticles().add(this);
    }

    /**
     * Can add another author to existing article
     * @param professional
     */
    public void addCoAuthor(MentalHealthProfessional professional) {
        if (professional != null) {
            this.authors.add(professional);
            professional.getArticles().add(this);
        }
    }

    /**
     * Remove co-author
     * @param professional
     */
    public void removeCoAuthor(MentalHealthProfessional professional) {
        if (professional != null) {
            this.authors.remove(professional);
            professional.getArticles().remove(this);
        }
    }
}
