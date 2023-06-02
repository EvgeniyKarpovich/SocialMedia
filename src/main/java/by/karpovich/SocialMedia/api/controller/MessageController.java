package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.api.dto.message.MessageDto;
import by.karpovich.SocialMedia.api.dto.message.MessageDtoOut;
import by.karpovich.SocialMedia.service.MessageServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message Controller", description = "Message API")
public class MessageController {

    private final MessageServiceImpl messageService;

    @PutMapping("/{receiverId}")
    public void sendMessage(@RequestBody MessageDto dto,
                            @RequestHeader(value = "Authorization") String authorization,
                            @PathVariable("receiverId") Long receiverId) {
        messageService.sendMessage(authorization, receiverId, dto);
    }

    @GetMapping("/{receiverId}")
    public List<MessageDtoOut> getMessages(@RequestHeader(value = "Authorization") String authorization,
                                           @PathVariable("receiverId") Long receiverId) {
        return messageService.getMessages(authorization, receiverId);
    }
}
