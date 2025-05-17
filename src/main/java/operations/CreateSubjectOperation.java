package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.CreateGroupState;
import states.CreateSubjectState;
import states.DeleteGroupState;
import states.IncludeSubjectState;
import utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CreateSubjectOperation extends Operation {

    private final SubjectRepository subjectRepository;
    private final ConcurrentHashMap<IdPair, CreateSubjectState> map = bot.getCreateSubjectStates();

    public CreateSubjectOperation(IdPair id, String messageId, MyTelegramBot bot,
                                  String message, SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.subjectRepository = subjectRepository;
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
                map.put(id, new CreateSubjectState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        CreateSubjectState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        if (ifTheseNewAnswer) {
            setLastMessage(state, "Напишите название для создания предмета",
                    InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            if (InputValidator.isValid(message, false)) {
                Optional<Subject> subject = subjectRepository.findByNameIgnoreCase(message);
                if (subject.isEmpty()) {
                    Subject newSubject = new Subject();
                    newSubject.setName(message);
                    subjectRepository.save(newSubject);
                    setLastMessage(state, String.format("предмет \"%s\" успешно создан", message), null);
                    map.remove(id);
                } else {
                    setLastMessage(state, "дисциплина уже существует, давайте другое название!",
                            InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
                }
            } else {
                setLastMessage(state, "Название не прошло валидацию: " +
                                InputValidator.RULES_DESCRIPTION_TITLE_PASSWORD,
                        InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
            }
        }
    }
}