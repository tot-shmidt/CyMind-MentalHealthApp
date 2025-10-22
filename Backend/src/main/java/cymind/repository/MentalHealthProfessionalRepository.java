package cymind.repository;

import cymind.model.MentalHealthProfessional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentalHealthProfessionalRepository extends JpaRepository<MentalHealthProfessional, Long> {
    MentalHealthProfessional findById(long id);
    MentalHealthProfessional findByAbstractUserId(long id);
}
