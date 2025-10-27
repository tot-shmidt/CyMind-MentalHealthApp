package cymind.repository;

import cymind.model.Appointment;
import cymind.model.AppointmentGroup;
import cymind.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findById(long id);
    List<Appointment> findByAppointmentGroupOrderByStartTimeDesc(AppointmentGroup appointmentGroup);
    List<Appointment> findByAppointmentGroup_StudentOrderByStartTimeDesc(Student student);
}
