package cymind.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cymind.dto.chat.MessageDTO;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;

public class MessageDecoder implements Decoder.Text<MessageDTO> {
    @Override
    public MessageDTO decode(String s) throws DecodeException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        try {
            return mapper.readValue(s, MessageDTO.class);
        } catch (JsonProcessingException e) {
            throw new DecodeException(s, "Unable to decode message");
        }
    }

    @Override
    public boolean willDecode(String s) {
        return s != null && !s.isEmpty();
    }
}
