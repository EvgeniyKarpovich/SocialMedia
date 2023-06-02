package by.karpovich.SocialMedia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    public @Bean
    OpenAPI noteAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Social Media")
                                .description("Imitation social media")
                                .version("0.0.1-SNAPSHOT")
                                .license(
                                        new License().name("MIT").url("https://opensource.org/licenses/MIT")
                                )
                );
    }
}
