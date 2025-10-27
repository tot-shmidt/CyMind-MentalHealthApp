package cymind.controller;

import cymind.dto.appointment.AppointmentGroupDTO;
import cymind.dto.appointment.CreateAppointmentGroupDTO;
import cymind.service.AppointmentGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppointmentGroupController {
    @Autowired
    AppointmentGroupService appointmentGroupService;

    @PostMapping("/appointments/groups")
    ResponseEntity<AppointmentGroupDTO> createAppointmentGroup(@RequestBody @Valid CreateAppointmentGroupDTO createAppointmentGroupDTO) {
        return new ResponseEntity<>(appointmentGroupService.create(createAppointmentGroupDTO), HttpStatus.CREATED);
    }

    @GetMapping("/appointments/groups")
    ResponseEntity<List<AppointmentGroupDTO>> getAppointmentGroupForUser() {
        return new ResponseEntity<>(appointmentGroupService.getByUserPrincipal(), HttpStatus.OK);
    }

    @GetMapping("/appointments/groups/{id}")
    ResponseEntity<AppointmentGroupDTO> getAppointmentGroup(@PathVariable long id) {
        return new ResponseEntity<>(appointmentGroupService.getByGroupId(id), HttpStatus.OK);
    }

    @PutMapping("/appointments/groups/{id}")
    ResponseEntity<AppointmentGroupDTO> updateAppointmentGroup(@PathVariable long id, @RequestBody @Valid CreateAppointmentGroupDTO createAppointmentGroupDTO) {
        return new ResponseEntity<>(appointmentGroupService.update(id, createAppointmentGroupDTO), HttpStatus.OK);
    }

    @DeleteMapping("/appointments/groups/{id}")
    ResponseEntity<?> deleteAppointmentGroup(@PathVariable long id) {
        appointmentGroupService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
