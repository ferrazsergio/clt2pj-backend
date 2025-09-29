package io.github.ferrazsergio.clt2pj;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Clt2pjApplication  {

	public static void main(String[] args) {
		// Carrega variáveis do .env e adiciona ao ambiente
		Dotenv dotenv = Dotenv.configure()
				.filename(".env.development")
				.ignoreIfMissing()
				.load();
		// Adiciona as variáveis do .env no ambiente do sistema
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		SpringApplication.run(Clt2pjApplication.class, args);
	}
}