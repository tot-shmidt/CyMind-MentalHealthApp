package cymind.controller;

import cymind.dto.appointment.AppointmentDTO;
import cymind.dto.appointment.AppointmentStatusDTO;
import cymind.enums.AppointmentStatus;
import cymind.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;

    @PostMapping("/appointments")
    ResponseEntity<AppointmentDTO> createAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO) {
        return new ResponseEntity<>(appointmentService.create(appointmentDTO), HttpStatus.CREATED);
    }

    @PostMapping("/appointments/{id}/status")
    ResponseEntity<AppointmentDTO> setAppointmentStatus(@PathVariable long id, @RequestBody @Valid AppointmentStatusDTO status) {
        return new ResponseEntity<>(appointmentService.setStatus(id, status), HttpStatus.OK);
    }

    @GetMapping("/appointments/{id}")
    ResponseEntity<AppointmentDTO> getAppointment(@PathVariable long id) {
        return new ResponseEntity<>(appointmentService.get(id), HttpStatus.OK);
    }

    @GetMapping("/appointments/{id}/status")
    ResponseEntity<AppointmentStatusDTO> getAppointmentStatus(@PathVariable long id) {
        return new ResponseEntity<>(appointmentService.getStatus(id), HttpStatus.OK);
    }

    @PutMapping("/appointments/{id}")
    ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable long id, @RequestBody @Valid AppointmentDTO appointmentDTO) {
        return new ResponseEntity<>(appointmentService.update(id, appointmentDTO), HttpStatus.OK);
    }

    @DeleteMapping("/appointments/{id}")
    ResponseEntity<?> deleteAppointment(@PathVariable long id) {
        appointmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
