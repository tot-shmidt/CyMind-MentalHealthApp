package cymind.controller;

import cymind.dto.appointment.AppointmentGroupDTO;
import cymind.model.Appointment;
import cymind.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;

    @PostMapping("/appointments")
    ResponseEntity<Appointment> createAppointment(@RequestBody @Valid Appointment appointment) {
        return new ResponseEntity<>(appointmentService.create(appointment), HttpStatus.CREATED);
    }

    @GetMapping("/appointments/{id}")
    ResponseEntity<> getAppointment(@PathVariable long id) {
        return new ResponseEntity<>(, HttpStatus.OK);
    }

    @PutMapping("/appointments/{id}")
    ResponseEntity<> updateAppointment(@PathVariable long id, @RequestBody @Valid ) {
        return new ResponseEntity<>(, HttpStatus.OK);
    }

    @DeleteMapping("/appointments/{id}")
    ResponseEntity<?> deleteAppointmen(@PathVariable long id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
