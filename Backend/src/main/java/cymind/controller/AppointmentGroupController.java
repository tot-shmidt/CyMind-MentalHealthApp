package cymind.controller;

import cymind.model.AppointmentGroup;
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

    @PostMapping("/appointment/group")
    ResponseEntity<AppointmentGroup> createAppointmentGroup(@RequestBody @Valid AppointmentGroup appointmentGroup) {
        return new ResponseEntity<>(appointmentGroupService.create(appointmentGroup), HttpStatus.CREATED);
    }

    @GetMapping("/appointment/group")
    ResponseEntity<List<AppointmentGroup>> getAppointmentGroupForUser() {
        return new ResponseEntity<>(appointmentGroupService.getByUserPrincipal(), HttpStatus.OK);
    }

    @GetMapping("/appointment/group/{id}")
    ResponseEntity<AppointmentGroup> getAppointmentGroup(@PathVariable long id) {
        return new ResponseEntity<>(appointmentGroupService.getByGroupId(id), HttpStatus.OK);
    }

    @PutMapping("/appointment/group/{id}")
    ResponseEntity<AppointmentGroup> updateAppointmentGroup(@PathVariable long id, @RequestBody AppointmentGroup appointmentGroup) {
        return new ResponseEntity<>(appointmentGroupService.update(id, appointmentGroup), HttpStatus.OK);
    }

    @DeleteMapping("/appointment/group/{id}")
    ResponseEntity<?> deleteAppointmentGroup(@PathVariable long id) {
        appointmentGroupService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
