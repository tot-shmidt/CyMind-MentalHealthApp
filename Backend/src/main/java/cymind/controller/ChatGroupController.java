package cymind.controller;

import cymind.dto.appointment.CreateAppointmentGroupDTO;
import cymind.dto.chat.ChatGroupDTO;
import cymind.dto.chat.CreateChatGroupDTO;
import cymind.dto.chat.MessageDTO;
import cymind.repository.ChatGroupRepository;
import cymind.service.ChatGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatGroupController {
    @Autowired
    ChatGroupService chatGroupService;

    @PostMapping("/chat/groups")
    ResponseEntity<ChatGroupDTO> createChatGroup(@RequestBody @Valid CreateChatGroupDTO createChatGroupDTO) {
        return new ResponseEntity<>(chatGroupService.create(createChatGroupDTO), HttpStatus.CREATED);
    }

    @GetMapping("/chat/groups")
    ResponseEntity<List<ChatGroupDTO>> getChatGroupsForUser() {
        return new ResponseEntity<>(chatGroupService.getByUserPrincipal(), HttpStatus.OK);
    }

    @GetMapping("/chat/groups/{id}")
    ResponseEntity<ChatGroupDTO> getChatGroup(@PathVariable long id) {
        return new ResponseEntity<>(chatGroupService.get(id), HttpStatus.OK);
    }

    @PutMapping("/chat/groups/{id}")
    ResponseEntity<ChatGroupDTO> updateChatGroup(@PathVariable long id, @RequestBody @Valid  CreateChatGroupDTO createChatGroupDTO) {
        return new ResponseEntity<>(chatGroupService.update(id, createChatGroupDTO), HttpStatus.OK);
    }

    @DeleteMapping("/chat/groups/{id}")
    ResponseEntity<?> deleteChatGroup(@PathVariable long id) {
        chatGroupService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/chat/groups/{id}/messages")
    ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long id, @RequestParam(name = "search", required = false) String search) {
        return new ResponseEntity<>(chatGroupService.getMessages(id, search), HttpStatus.OK);
    }
}