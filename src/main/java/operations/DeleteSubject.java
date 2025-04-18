package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.AssignmentRepository;
import sqlTables.Subject;
import sqlTables.SubjectRepository;
import sqlTables.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeleteSubject extends Operation {
    private final SubjectRepository subjectRepository;
    private final AssignmentRepository assignmentRepository;

    public DeleteSubject(IdPair id, String messageId,
                         MyTelegramBot bot, String message, SubjectRepository subjectRepository, AssignmentRepository assignmentRepository) {
        super(id, messageId, bot, message);
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        List<Subject> subjects = subjectRepository.findAll();
        List<String> subjectsNames = new ArrayList<>();
        for (Subject subject : subjects)
            subjectsNames.add(subject.getName());
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getDeleteSubjectUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нет прав для удаления предметов");
            bot.getDeleteSubjectUsers().remove(id);
        }
        if (subjects.isEmpty()) {
            sendMessage.setText("В системе нету предметов");
            bot.getDeleteSubjectUsers().remove(id);
        }
        else if (bot.getDeleteSubjectUsers().contains(id)) {
            StringBuilder text = new StringBuilder();
            Optional<Subject> subject = subjectRepository.findByName(message);
            if (subject.isPresent()) {
                if (assignmentRepository.existsBySubjectId(subject.get().getId()))
                    text.append("Вы не можете удалить предмет по которому есть задания");
                else {
                    subjectRepository.delete(subject.get());
                    subjectsNames.remove(subject.get().getName());
                    text.append("Предмет удален");
                }
            } else
                text.append("Предмет не найден");
            if (subjectsNames.isEmpty()) {
                text.append("В системе не осталось предметов");
                bot.getDeleteSubjectUsers().remove(id);
            } else {
                text.append("Выберете еще предмет или закончите операцию");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                        subjectsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        }
        else {
            sendMessage.setText("Выбирайте предметы пока они не закончатся или вы не выберете /break");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true,false,
                    subjectsNames.toArray(new String[0])));
            bot.getDeleteSubjectUsers().add(id);
        }
        sendReply();
    }
}
