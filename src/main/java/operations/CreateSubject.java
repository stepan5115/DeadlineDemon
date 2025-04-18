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
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getCreateSubjectUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нет на это прав");
            bot.getCreateSubjectUsers().remove(id);
        }
        else if (bot.getCreateSubjectUsers().contains(id)) {
            if (subjectRepository.existsByNameIgnoreCase(message)) {
                sendMessage.setText("Предмет уже создан, попробуйте снова");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            }
            else {
                Subject subject = new Subject();
                subject.setName(message);
                subjectRepository.save(subject);
                bot.getCreateSubjectUsers().remove(id);
                sendMessage.setText("Предмет создан");
            }
        }
        else {
            sendMessage.setText("Введите имя предмета");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            bot.getCreateSubjectUsers().add(id);
        }
        sendReply();
    }
}
