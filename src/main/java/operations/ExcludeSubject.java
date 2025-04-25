package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ExcludeSubject extends Operation {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    public ExcludeSubject(IdPair id, String messageId,
                             MyTelegramBot bot, String message, UserRepository userRepository,
                             AssignmentRepository assignmentRepository,
                             SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getExcludeSubject().remove(id);
            sendReply();
            return;
        }
        user.normalizeExcluded(userRepository, assignmentRepository, subjectRepository);
        Set<String> targetSubjects = new HashSet<>();
        for (Subject subject : subjectRepository.findAll())
            if (!user.getNotificationExcludedSubjects().contains(subject.getId()))
                targetSubjects.add(subject.getName());
        if (checkEmpty(targetSubjects)) {
            sendReply();
            return;
        }
        if (bot.getExcludeSubject().contains(id)) {
            if (targetSubjects.contains(message)) {
                Optional<Subject> subjectOptional = subjectRepository.getSubjectByName(message);
                if (subjectOptional.isEmpty())
                    throw new IllegalArgumentException("Cannot find subject with title: " + message);
                Subject subject = subjectOptional.get();
                if (!user.getNotificationExcludedSubjects().contains(subject.getId())) {
                    user.getNotificationExcludedSubjects().add(subject.getId());
                    userRepository.save(user);
                    targetSubjects.remove(subject.getName());
                    if (checkEmpty(targetSubjects)) {
                        sendMessage.setText("Успешно исключили предмет из уведомлений. Больше не осталось никаких заданий вне мута");
                        sendReply();
                        return;
                    }
                }
                sendMessage.setText("Успешно исключили предмет из уведомлений");
            } else
                sendMessage.setText("Это либо несуществующий предмет, либо он уже исключен");
        } else {
            sendMessage.setText("Выбирайте предметы которые хотите исключить пока не захотите завершить");
            bot.getExcludeSubject().add(id);
        }
        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                targetSubjects.toArray(new String[0])));
        sendReply();
    }
    private boolean checkEmpty(Set<String> targetSubjects) {
        if (targetSubjects.isEmpty()) {
            sendMessage.setText("Не нашлось подходящих предметов");
            bot.getExcludeSubject().remove(id);
            return true;
        }
        return false;
    }
}
