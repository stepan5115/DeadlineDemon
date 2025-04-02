package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Subject;
import sqlTables.SubjectRepository;
import sqlTables.User;

public class CreateSubject extends Operation {
    private final SubjectRepository subjectRepository;

    public CreateSubject(IdPair id, String messageId, MyTelegramBot bot, String message, SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getCreateSubjectUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getCreateSubjectUsers().remove(id);
        }
        else if (bot.getCreateSubjectUsers().contains(id)) {
            if (subjectRepository.existsByNameIgnoreCase(message)) {
                sendMessage.setText("Subject already exists. Try again");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true));
            }
            else {
                Subject subject = new Subject();
                subject.setName(message);
                subjectRepository.save(subject);
                bot.getCreateSubjectUsers().remove(id);
                sendMessage.setText("Subject created");
            }
        }
        else {
            sendMessage.setText("Enter subject name");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true));
            bot.getCreateSubjectUsers().add(id);
        }
        sendReply();
    }
}
