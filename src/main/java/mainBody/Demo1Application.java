package mainBody;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "sqlTables")
@EntityScan(basePackages = "sqlTables")
public class Demo1Application {

	public static void main(String[] args) throws TelegramApiException {
		//
		MyTelegramBot myBot = null;
		var contest = SpringApplication.run(Demo1Application.class, args);
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			myBot = contest.getBean(MyTelegramBot.class);
			botsApi.registerBot(myBot);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
