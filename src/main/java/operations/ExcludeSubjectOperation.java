package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.ExcludeSubjectState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExcludeSubjectOperation extends Operation {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ConcurrentHashMap<IdPair, ExcludeSubjectState> map = bot.getExcludeSubjectState();

    public ExcludeSubjectOperation(IdPair id, String messageId,
                          MyTelegramBot bot, String message,
                          UserRepository userRepository,
                          SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new ExcludeSubjectState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        ExcludeSubjectState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        List<Subject> subjects = getIncludedSubjects(subjectRepository, user);
        if (ifTheseNewAnswer) {
            checkEmptySubjects(id, state, subjects,
                    "Добро пожаловать в меню отключения уведомлений предметам, " +
                            "выбирайте предметы для удаления");
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Optional<Subject> subject = subjects.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
            if (subject.isPresent()) {
                user.getNotificationExcludedSubjects().add(subject.get().getId());
                userRepository.save(user);
                subjects.remove(subject.get());
                checkEmptySubjects(id, state, subjects, String.format("Уведомления для \"%s\" успешно отключены",
                        subject.get().getName()));
            }
            else
                checkEmptySubjects(id, state, subjects,"Этот предмет не входит в число " +
                        "заблокированных или вообще не существует! Выберите из тех что под сообщением");
        }
    }

    private List<Subject> getIncludedSubjects(SubjectRepository subjectRepository, User user) {
        return subjectRepository.getAllIncludedSubjects(user);
    }
    private InlineKeyboardMarkup getInlineKeyboardSubjects(String userId, ExcludeSubjectState state,
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
    private void checkEmptySubjects(IdPair id, ExcludeSubjectState state, List<Subject> subjects,
                                       String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardSubjects(id.getUserId(), state, subjects);
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
        } else {
            setLastMessage(state, "Разблокированных предметов больше нету", null);
            map.remove(id);
        }
    }
}
