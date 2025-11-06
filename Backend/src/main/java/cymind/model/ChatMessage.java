package cymind.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Lob
    @NotNull
    String content;

    LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    AbstractUser sender;

    @ManyToOne
    @JoinColumn(name = "group_id")
    ChatGroup chatGroup;

    public ChatMessage(AbstractUser sender, ChatGroup group, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.chatGroup = group;
        this.content = content;
        this.timestamp = timestamp;
    }
}
