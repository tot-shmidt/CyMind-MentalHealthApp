package cymind.repository;

import cymind.model.MentalHealthProfessional;
import cymind.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentalHealthProfessionalRepository extends JpaRepository<MentalHealthProfessional, Long> {
    MentalHealthProfessional findById(long id);
    MentalHealthProfessional findByAbstractUserId(long id);
}
