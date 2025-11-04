package cymind.model;

import cymind.enums.ExerciseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Exercise name cannot be blank")
    private String exerciseName;

    /**
     * Content of the exercise
     */
    private String content;

    /**
     * Enum type of the exercise
     */
    private ExerciseType exerciseType;

    // Reverse relation to Article
    @ManyToMany(mappedBy = "relatedExercises")
    private List<Article> articles = new ArrayList<>();

}
