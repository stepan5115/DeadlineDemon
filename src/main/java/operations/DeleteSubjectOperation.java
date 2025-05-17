package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.DeleteGroupState;
import states.DeleteSubjectState;
import states.IncludeSubjectState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeleteSubjectOperation extends Operation {

    private final SubjectRepository subjectRepository;
    private final AssignmentRepository assignmentRepository;
    private final ConcurrentHashMap<IdPair, DeleteSubjectState> map = bot.getDeleteSubjectStates();

    public DeleteSubjectOperation(IdPair id, String messageId,
                                  MyTelegramBot bot, String message,
                                  SubjectRepository subjectRepository,
                                  AssignmentRepository assignmentRepository) {
        super(id, messageId, bot, message);
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (checkAdminRights()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new DeleteSubjectState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        DeleteSubjectState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        List<Subject> subjects = subjectRepository.findAll();
        if (ifTheseNewAnswer) {
            checkEmptySubjects(id, state, subjects,
                    "Добро пожаловать в меню удаления дисциплин");
        }
        else if (basePaginationCheck(state, message)) {
            checkEmptySubjects(id, state, subjects,
                    "Добро пожаловать в меню удаления дисциплин");
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Optional<Subject> subject = subjects.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
            if (subject.isPresent()) {
                if (!assignmentRepository.existsBySubjectId(subject.get().getId())) {
                    subjectRepository.delete(subject.get());
                    subjects.remove(subject.get());
                    checkEmptySubjects(id, state, subjects, String.format("Успешно удалена дисциплина \"%s\"",
                            subject.get().getName()));
                }
                else {
                    checkEmptySubjects(id, state, subjects, "Нельзя удалить предмет по которому есть задания!");
                }
            }
            else
                checkEmptySubjects(id, state, subjects,"Предмет не найден в системе, выберите из кнопок под сообщением!");
        }
    }
    private InlineKeyboardMarkup getInlineKeyboardGroups(String userId, DeleteSubjectState state,
                                                         List<Subject> subjects) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForSubjects = new ArrayList<>();
            for (Subject subject : subjects) {
                //add visible and invisible text
                namesForSubjects.add(new InlineKeyboardBuilder.Pair(
                        subject.getName(),
                        subject.getId().toString()
                ));
            }
            if (namesForSubjects.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForSubjects.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private void checkEmptySubjects(IdPair id, DeleteSubjectState state, List<Subject> subjects,
                                  String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardGroups(id.getUserId(), state, subjects.stream().toList());
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
        } else {
            setLastMessage(state, "больше нету предметов в системе", null);
            map.remove(id);
        }
    }
}