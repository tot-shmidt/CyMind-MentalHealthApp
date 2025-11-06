package cymind.websocket.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cymind.dto.chat.MessageDTO;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class MessageEncoder implements Encoder.Text<MessageDTO> {
    @Override
    public String encode(MessageDTO messageDTO) throws EncodeException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            return mapper.writeValueAsString(messageDTO);
        } catch (Exception e) {
            throw new EncodeException(messageDTO, "Unable to encode message");
        }
    }
}
