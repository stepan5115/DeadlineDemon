package mainBody;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sqlTables.AssignmentRepository;
import sqlTables.GroupRepository;
import sqlTables.SubjectRepository;
import sqlTables.UserRepository;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final AssignmentRepository assignmentRepository;

    private final String botUsername = "DeadlineDemonBot"; // Имя твоего бота
    private final String botToken = "8054120880:AAF78Qz9kPvwR3p2OhN4GEFCVznCw-Kf2No"; // Токен бота
    final int RECONNECT_PAUSE =10000;

    public MyTelegramBot(UserRepository userRepository,
                         SubjectRepository subjectRepository,
                         GroupRepository groupRepository,
                         AssignmentRepository assignmentRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String response = "Привет, " + message.getText(); // Пример обработки
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            sendMessage.setText(response);
            try {
                execute(sendMessage); // Отправка сообщения
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
