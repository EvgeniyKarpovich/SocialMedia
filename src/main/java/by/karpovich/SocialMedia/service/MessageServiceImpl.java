package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.message.MessageDto;
import by.karpovich.SocialMedia.api.dto.message.MessageDtoOut;
import by.karpovich.SocialMedia.exception.DuplicateException;
import by.karpovich.SocialMedia.jpa.entity.MessageEntity;
import by.karpovich.SocialMedia.jpa.entity.UserEntity;
import by.karpovich.SocialMedia.jpa.repository.MessageRepository;
import by.karpovich.SocialMedia.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserServiceImpl userService;

    @Transactional
    public void sendMessage(String authorization, Long receiverId, MessageDto dto) {
        UserEntity sender = userService.findUserEntityByIdFromToken(authorization);
        UserEntity receiver = userService.findUserByIdWhichWillReturnModel(receiverId);

        if (!sender.getFriends().contains(receiver)) {
            throw new DuplicateException("You no Friend");
        }

        MessageEntity messageEntity = MessageEntity.builder()
                .text(dto.getText())
                .sender(sender)
                .receiver(receiver)
                .build();

        messageRepository.save(messageEntity);
    }

//    public List<MessageDtoOut> getMessages(String authorization, Long receiverId) {
//        UserEntity userEntity = userService.findUserEntityByIdFromToken(authorization);
//
//        List<MessageEntity> messageEntities = messageRepository.mesBySenId(userEntity.getId(), receiverId);
//
//        List<MessageDtoOut> messageDtoOuts = new ArrayList<>();
//
//        for (MessageEntity messageEntity : messageEntities) {
//            MessageDtoOut messageDtoOut = MessageDtoOut.builder()
//                    .text(messageEntity.getText())
//                    .sender(messageEntity.getSender().getUsername())
//                    .receiver(messageEntity.getReceiver().getUsername())
//                    .date(Utils.mapStringFromInstant(messageEntity.getDateOfCreation()))
//                    .build();
//
//            messageDtoOuts.add(messageDtoOut);
//        }
//
//        return messageDtoOuts.stream()
//                .sorted(Comparator.comparing(MessageDtoOut::getDate))
//                .toList();
//    }

    public List<MessageDtoOut> getMessages(String authorization, Long receiverId) {
        UserEntity userEntity = userService.findUserEntityByIdFromToken(authorization);

        List<MessageEntity> messagesSenderIdAndReceiverId = messageRepository.findBySenderIdAndReceiverId(userEntity.getId(), receiverId);

        List<MessageDtoOut> messageDtoOuts = new ArrayList<>();

        for (MessageEntity messageEntity : messagesSenderIdAndReceiverId) {
            MessageDtoOut messageDtoOut = MessageDtoOut.builder()
                    .text(messageEntity.getText())
                    .sender(messageEntity.getSender().getUsername())
                    .receiver(messageEntity.getReceiver().getUsername())
                    .date(Utils.mapStringFromInstant(messageEntity.getDateOfCreation()))
                    .build();

            messageDtoOuts.add(messageDtoOut);
        }

        return messageDtoOuts.stream()
                .sorted(Comparator.comparing(MessageDtoOut::getDate).reversed())
                .toList();
    }


}
