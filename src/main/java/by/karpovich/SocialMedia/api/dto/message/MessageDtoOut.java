package by.karpovich.SocialMedia.api.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MessageDtoOut {

    private String text;
    private String sender;
    private String receiver;
    private String date;
}
