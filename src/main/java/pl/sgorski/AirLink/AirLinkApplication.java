package pl.sgorski.AirLink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AirLinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirLinkApplication.class, args);
	}

}
