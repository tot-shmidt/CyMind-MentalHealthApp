package cymind.repository;

import cymind.enums.AppointmentStatus;
import cymind.model.Appointment;
import cymind.model.AppointmentGroup;
import cymind.model.MentalHealthProfessional;
import cymind.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findById(long id);
    List<Appointment> findByAppointmentGroupOrderByStartTimeDesc(AppointmentGroup appointmentGroup);
    List<Appointment> findAllByAppointmentGroup_StudentOrderByStartTimeAsc(Student student);
    List<Appointment> findAllByAppointmentGroup_StudentAndStatusInOrderByStartTimeAsc(Student student, List<AppointmentStatus> status);
    List<Appointment> findAllByAppointmentGroup_MentalHealthProfessionalsContainingOrderByStartTimeAsc(MentalHealthProfessional professional);
    List<Appointment> findAllByAppointmentGroup_MentalHealthProfessionalsContainingAndStatusInOrderByStartTimeAsc(MentalHealthProfessional professional, List<AppointmentStatus> status);
}
