package cymind.repository;

import cymind.model.MentalHealthProfessional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentalHealthProfessionalRepository extends JpaRepository<MentalHealthProfessional, Long> {
    MentalHealthProfessional findById(long id);
    MentalHealthProfessional findByAbstractUserId(long id);
    List<MentalHealthProfessional> findAllByAbstractUserIdIn(List<Long> ids);
    List<MentalHealthProfessional> findByAbstractUser_FirstNameContainingAndAbstractUser_LastNameContainingOrderByAbstractUser(String firstName, String lastName);

    @Query("SELECT u FROM MentalHealthProfessional u WHERE u.abstractUser.firstName LIKE %:name% OR u.abstractUser.lastName LIKE %:name%")
    List<MentalHealthProfessional> findByName(@Param("name") String name);
}
