package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import sqlTables.Subject;
import sqlTables.SubjectRepository;
import sqlTables.User;

public class CreateSubject extends Operation {
    private final SubjectRepository subjectRepository;

    public CreateSubject(String chatId, String userId, String messageId, MyTelegramBot bot, String message, SubjectRepository subjectRepository) {
        super(chatId, userId, messageId, bot, message);
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getCreateSubjectUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getCreateSubjectUsers().remove(userId);
        }
        else if (bot.getCreateSubjectUsers().contains(userId)) {
            if (subjectRepository.existsByNameIgnoreCase(message)) {
                sendMessage.setText("Subject already exists. Try again");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            }
            else {
                Subject subject = new Subject();
                subject.setName(message);
                subjectRepository.save(subject);
                bot.getCreateSubjectUsers().remove(userId);
                sendMessage.setText("Subject created");
            }
        }
        else {
            sendMessage.setText("Enter subject name");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            bot.getCreateSubjectUsers().add(userId);
        }
        sendReply();
    }
}
