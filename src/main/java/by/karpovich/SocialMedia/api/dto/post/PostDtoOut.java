package by.karpovich.SocialMedia.api.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostDtoOut {

    private String sender;
    private String header;
    private String text;
    private byte[] image;
    private String dateOfCreation;
}
