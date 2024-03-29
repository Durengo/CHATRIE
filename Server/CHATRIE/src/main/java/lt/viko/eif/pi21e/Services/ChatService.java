package lt.viko.eif.pi21e.Services;

import lombok.RequiredArgsConstructor;
import lt.viko.eif.pi21e.Entities.Chat;
import lt.viko.eif.pi21e.Repositories.ChatRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final RabbitTemplate rabbitTemplate;

    public void saveChatMessage(Chat chat) {
        chatRepository.save(chat);
    }
}
