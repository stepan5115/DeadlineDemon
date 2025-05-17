package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.IncludeSubjectState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IncludeSubjectOperation extends Operation {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ConcurrentHashMap<IdPair, IncludeSubjectState> map = bot.getIncludeSubjectStates();

    public IncludeSubjectOperation(IdPair id, String messageId,
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
                map.put(id, new IncludeSubjectState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        IncludeSubjectState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        Set<Subject> subjects = getExcludedSubjects(subjectRepository, user);
        if (ifTheseNewAnswer) {
            checkEmptySubjects(id, state, subjects,
                    "Добро пожаловать в меню включения уведомлений предметам, " +
                            "выбирайте предметы для включения");
        }
        else if (basePaginationCheck(state, message)) {
            checkEmptySubjects(id, state, subjects,
                    "Добро пожаловать в меню включения уведомлений предметам, " +
                            "выбирайте предметы для включения");
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Optional<Subject> subject = subjects.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
            if (subject.isPresent()) {
                user.getNotificationExcludedSubjects().remove(subject.get().getId());
                userRepository.save(user);
                subjects.remove(subject.get());
                checkEmptySubjects(id, state, subjects, String.format("Уведомления для \"%s\" успешно включены",
                        subject.get().getName()));
            }
            else
                checkEmptySubjects(id, state, subjects,"Этот предмет не входит в число " +
                        "разблокированных или вообще не существует! Выберите из тех что под сообщением");
        }
    }

    private Set<Subject> getExcludedSubjects(SubjectRepository subjectRepository, User user) {
        return user.getExcludedSubjects(subjectRepository);
    }
    private InlineKeyboardMarkup getInlineKeyboardSubjects(String userId, IncludeSubjectState state,
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
    private void checkEmptySubjects(IdPair id, IncludeSubjectState state, Set<Subject> subjects,
                                    String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardSubjects(id.getUserId(), state, subjects.stream().toList());
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
        } else {
            setLastMessage(state, "Заблокированных предметов больше нету", null);
            map.remove(id);
        }
    }
}
