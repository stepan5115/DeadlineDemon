package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AssignmentRepository;
import sqlTables.Subject;
import sqlTables.SubjectRepository;
import sqlTables.User;

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
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteSubjectUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteSubjectUsers().remove(chatId);
        }
        else if (bot.getDeleteSubjectUsers().contains(chatId)) {
            Optional<Subject> subject = subjectRepository.findByName(message);
            if (subject.isPresent()) {
                if (assignmentRepository.existsBySubjectId(subject.get().getId()))
                    sendMessage.setText("You can't delete subject that has a assignment.");
                else {
                    subjectRepository.delete(subject.get());
                    sendMessage.setText("Subject deleted.");
                }
            } else
                sendMessage.setText("Subject not found");
            bot.getDeleteSubjectUsers().remove(chatId);
        }
        else {
            List<Subject> subjects = subjectRepository.findAll();
            if (subjects.isEmpty())
                sendMessage.setText("There are no subjects in system");
            else {
                StringBuilder text = new StringBuilder("Choose a subject from list:");
                for (Subject subject : subjects)
                    text.append("\n").append(subject.getName());
                bot.getDeleteSubjectUsers().add(chatId);
                sendMessage.setText(text.toString());
            }
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
