package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AssignmentRepository;
import sqlTables.Subject;
import sqlTables.SubjectRepository;
import sqlTables.User;

import java.util.List;

public class CreateSubject extends Operation {
    private final SubjectRepository subjectRepository;

    public CreateSubject(String chatId, MyTelegramBot bot, String message, SubjectRepository subjectRepository) {
        super(chatId, bot, message);
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getCreateSubjectUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getCreateSubjectUsers().remove(chatId);
        }
        else if (bot.getCreateSubjectUsers().contains(chatId)) {
            if (subjectRepository.existsByNameIgnoreCase(message)) {
                sendMessage.setText("Subject already exists. Try again");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            }
            else {
                Subject subject = new Subject();
                subject.setName(message);
                subjectRepository.save(subject);
                bot.getCreateSubjectUsers().remove(chatId);
                sendMessage.setText("Subject created");
            }
        }
        else {
            sendMessage.setText("Enter subject name");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            bot.getCreateSubjectUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
