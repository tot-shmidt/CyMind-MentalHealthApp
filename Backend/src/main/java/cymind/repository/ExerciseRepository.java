package cymind.repository;

import cymind.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import cymind.enums.ExerciseType;
import java.util.List;

public interface ExerciseRepository  extends JpaRepository<Exercise, Long> {
    List<Exercise> findByExerciseType(ExerciseType exerciseType);
}
