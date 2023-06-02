package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.message.MessageDto;
import by.karpovich.SocialMedia.api.dto.message.MessageDtoOut;

import java.util.List;

public interface MessageService {

    void sendMessage(String authorization, Long receiverId, MessageDto dto);

    List<MessageDtoOut> getMessages(String authorization, Long receiverId);
}
