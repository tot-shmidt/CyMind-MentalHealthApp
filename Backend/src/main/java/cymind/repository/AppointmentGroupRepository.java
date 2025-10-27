package cymind.repository;

import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;
import cymind.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentGroupRepository extends JpaRepository<AppointmentGroup, Long> {
    AppointmentGroup findById(long id);
    List<AppointmentGroup> findAllByMentalHealthProfessionalsContaining(MentalHealthProfessional mentalHealthProfessional);
    List<AppointmentGroup> findAllByStudent(Student student);
}
