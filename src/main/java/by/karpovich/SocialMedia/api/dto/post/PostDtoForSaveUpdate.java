package by.karpovich.SocialMedia.api.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostDtoForSaveUpdate {

    private String header;
    private String text;
}
