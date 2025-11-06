package cymind.repository;

import cymind.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findById(long messageId);
    List<ChatMessage> findAllByChatGroup_IdOrderByTimestampDesc(long id);
    List<ChatMessage> findAllByChatGroup_IdAndContentContainsOrderByTimestampDesc(long id, String search);
}
