package operations;

import keyboards.InstanceKeyboardBuilder;
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

    public DeleteSubject(String chatId, String userId, String messageId,
                         MyTelegramBot bot, String message, SubjectRepository subjectRepository, AssignmentRepository assignmentRepository) {
        super(chatId, userId, messageId, bot, message);
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        List<Subject> subjects = subjectRepository.findAll();
        List<String> subjectsNames = new ArrayList<>();
        for (Subject subject : subjects)
            subjectsNames.add(subject.getName());
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteSubjectUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteSubjectUsers().remove(userId);
        }
        if (subjects.isEmpty()) {
            sendMessage.setText("There are no subjects in system");
            bot.getDeleteSubjectUsers().remove(userId);
        }
        else if (bot.getDeleteSubjectUsers().contains(userId)) {
            StringBuilder text = new StringBuilder();
            Optional<Subject> subject = subjectRepository.findByName(message);
            if (subject.isPresent()) {
                if (assignmentRepository.existsBySubjectId(subject.get().getId()))
                    text.append("You can't delete subject that has a assignment.");
                else {
                    subjectRepository.delete(subject.get());
                    subjectsNames.remove(subject.get().getName());
                    text.append("Subject deleted.");
                }
            } else
                text.append("Subject not found");
            if (subjectsNames.isEmpty()) {
                text.append("There are no subjects in system");
                bot.getDeleteSubjectUsers().remove(userId);
            } else {
                text.append("Enter another subject or break operation");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                        subjectsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        }
        else {
            sendMessage.setText("Choose a subject from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                    subjectsNames.toArray(new String[0])));
            bot.getDeleteSubjectUsers().add(userId);
        }
        sendReply();
    }
}
