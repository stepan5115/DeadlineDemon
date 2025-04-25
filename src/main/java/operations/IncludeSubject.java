package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class IncludeSubject extends Operation {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    public IncludeSubject(IdPair id, String messageId,
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
            bot.getIncludeSubject().remove(id);
            sendReply();
            return;
        }
        user.normalizeExcluded(userRepository, assignmentRepository, subjectRepository);
        Set<Long> targetSubjects = user.getNotificationExcludedSubjects();
        Set<String> targetSubjectNames = new HashSet<>();
        for (Long id : targetSubjects) {
            Optional<Subject> subject = subjectRepository.getSubjectById(id);
            if (subject.isEmpty())
                throw new IllegalArgumentException("Can't find subject with id: " + id);
            targetSubjectNames.add(subject.get().getName());
        }
        if (checkEmpty(targetSubjectNames)) {
            sendReply();
            return;
        }
        if (bot.getIncludeSubject().contains(id)) {
            if (targetSubjectNames.contains(message)) {
                Optional<Subject> subjectOptional = subjectRepository.getSubjectByName(message);
                if (subjectOptional.isEmpty())
                    throw new IllegalArgumentException("Cannot find subject with title: " + message);
                Subject subject = subjectOptional.get();
                if (user.getNotificationExcludedSubjects().contains(subject.getId())) {
                    user.getNotificationExcludedSubjects().remove(subject.getId());
                    userRepository.save(user);
                    targetSubjectNames.remove(subject.getName());
                    if (checkEmpty(targetSubjectNames)) {
                        sendMessage.setText("Успешно активировали предмет для уведомлений. Больше не осталось никаких предметов в муте");
                        sendReply();
                        return;
                    }
                }
                sendMessage.setText("Успешно активировали предмет для уведомлений");
            } else
                sendMessage.setText("либо предмета нету, либо он не в муте");
        } else {
            sendMessage.setText("Выбирайте для чего хотите вернуть уведомления");
            bot.getIncludeSubject().add(id);
        }
        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                targetSubjectNames.toArray(new String[0])));
        sendReply();
    }
    private boolean checkEmpty(Set<String> targetSubjectNames) {
        if (targetSubjectNames.isEmpty()) {
            sendMessage.setText("Не нашлось подходящих предметов");
            bot.getIncludeSubject().remove(id);
            return true;
        }
        return false;
    }
}
