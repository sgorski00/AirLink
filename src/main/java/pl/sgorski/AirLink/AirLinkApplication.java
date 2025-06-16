package pl.sgorski.AirLink;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@OpenAPIDefinition(
		info = @Info(
				title = "AirLink API",
				version = "1.0",
				description = "API for AirLink application"
		)
)
@SpringBootApplication
@EnableCaching
public class AirLinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirLinkApplication.class, args);
	}

}
