package es.urjc.daw.equis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EquisApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquisApplication.class, args);
	}

}
