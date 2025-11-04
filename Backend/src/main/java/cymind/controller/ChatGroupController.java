package cymind.controller;

import cymind.dto.appointment.CreateAppointmentGroupDTO;
import cymind.dto.chat.ChatGroupDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatGroupController {

    @PostMapping("/chat/groups")
    ResponseEntity<ChatGroupDTO> createChatGroup(@RequestBody @Valid CreateAppointmentGroupDTO createAppointmentGroupDTO) {
        return new ResponseEntity<>(, HttpStatus.CREATED);
    }

    @GetMapping("/chat/groups")
    ResponseEntity<List<ChatGroupDTO>> getChatGroupsForUser() {
        return new ResponseEntity<>(, HttpStatus.OK);
    }

    @GetMapping("/chat/groups/{id}")
    ResponseEntity<ChatGroupDTO> getChatGroup(@PathVariable long id) {
        return new ResponseEntity<>(, HttpStatus.OK);
    }

    @PutMapping("/chat/groups/{id}")
    ResponseEntity<ChatGroupDTO> updateChatGroup(@PathVariable long id, @RequestBody @Valid  CreateAppointmentGroupDTO createAppointmentGroupDTO) {
        return new ResponseEntity<>(, HttpStatus.OK);
    }

    @DeleteMapping("/chat/groups/{id}")
    ResponseEntity<?> deleteChatGroup(@PathVariable long id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}