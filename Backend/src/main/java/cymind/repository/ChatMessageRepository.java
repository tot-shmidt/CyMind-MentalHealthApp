package cymind.repository;

import cymind.model.ChatGroup;
import cymind.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatGroup_IdOrderByTimestampDesc(long id);
}
