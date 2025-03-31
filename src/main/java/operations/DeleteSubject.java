package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    public DeleteSubject(String chatId, MyTelegramBot bot, String message, SubjectRepository subjectRepository, AssignmentRepository assignmentRepository) {
        super(chatId, bot, message);
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        List<Subject> subjects = subjectRepository.findAll();
        List<String> subjectsNames = new ArrayList<>();
        for (Subject subject : subjects)
            subjectsNames.add(subject.getName());
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteSubjectUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteSubjectUsers().remove(chatId);
        }
        if (subjects.isEmpty()) {
            sendMessage.setText("There are no subjects in system");
            bot.getDeleteSubjectUsers().remove(chatId);
        }
        else if (bot.getDeleteSubjectUsers().contains(chatId)) {
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
                bot.getDeleteSubjectUsers().remove(chatId);
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
            bot.getDeleteSubjectUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
