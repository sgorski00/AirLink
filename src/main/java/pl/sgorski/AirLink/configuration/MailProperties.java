package pl.sgorski.AirLink.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class MailProperties {

    private String username;
    private String defaultEncoding;
}
