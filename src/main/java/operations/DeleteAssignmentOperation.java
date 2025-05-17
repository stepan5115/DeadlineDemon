//package operations;
//
//import keyboards.InstanceKeyboardBuilder;
//import mainBody.IdPair;
//import mainBody.MyTelegramBot;
//import sqlTables.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DeleteAssignmentOperation extends Operation {
//
//
//    public DeleteAssignmentOperation(IdPair id, String messageId,
//                                     MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
//        super(id, messageId, bot, message);
//        this.assignmentRepository = assignmentRepository;
//    }
//
//    public void run() {
//        User user = bot.getAuthorizedUsers().get(id);
//        List<Assignment> assignments = assignmentRepository.findAll();
//        List<String> assignmentsNames = new ArrayList<>();
//        for (Assignment assignment : assignments)
//            assignmentsNames.add(assignment.getTitle());
//        if (!bot.getAuthorizedUsers().containsKey(id)) {
//            sendMessage.setText("Для начала войдите в аккаунт");
//            bot.getDeleteAssignmentUsers().remove(id);
//        }
//        else if (!user.isCanEditTasks()) {
//            sendMessage.setText("У вас нет прав на удаление задания");
//            bot.getDeleteAssignmentUsers().remove(id);
//        }
//        else if (assignmentsNames.isEmpty()) {
//            sendMessage.setText("Нету заданий в системе");
//            bot.getDeleteAssignmentUsers().remove(id);
//        }
//        else if (bot.getDeleteAssignmentUsers().contains(id)) {
//            StringBuilder text = new StringBuilder();
//            if (assignmentRepository.existsAssignmentByTitle(message)) {
//                assignmentRepository.deleteAssignmentsByTitle(message);
//                assignmentsNames.remove(message);
//                text.append("Успешно удалено");
//            } else
//                text.append("Не существует");
//            if (assignmentsNames.isEmpty()) {
//                text.append("\nВ системе нету заданий");
//                bot.getDeleteAssignmentUsers().remove(id);
//            } else {
//                text.append("\nВыберите заголовок задания");
//                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
//                        assignmentsNames.toArray(new String[0])));
//            }
//            sendMessage.setText(text.toString());
//        } else {
//            sendMessage.setText("Выбирайте задания из списка пока они не кончатся или вы не выберете /break");
//            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
//                    assignmentsNames.toArray(new String[0])));
//            bot.getDeleteAssignmentUsers().add(id);
//        }
//        sendReply();
//    }
//}
package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeleteAssignmentOperation extends Operation {
    public final String TO_FILTER = "/toFilter";
    public final String TO_FILTER_VISIBLE = "фильтровать";

    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final ConcurrentHashMap<IdPair, DeleteAssignmentState> map = bot.getDeleteAssignmentStates();

    public DeleteAssignmentOperation(IdPair id, String messageId,
                                MyTelegramBot bot, String message,
                                AssignmentRepository assignmentRepository,
                                SubjectRepository subjectRepository, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
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
                map.put(id, new DeleteAssignmentState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        DeleteAssignmentState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        Set<Assignment> assignments = getAllAssignmentsAccordFilters(state, user);
        switch (state.getPosition()) {
            case MAIN -> {
                if (ifTheseNewAnswer) {
                    sendMessage.setText("Здесь вы можете удалить задание");
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                }
                else if (message.equals(TO_FILTER)) {
                    state.setPosition(DeleteAssignmentState.Position.FILTER_MODE);
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                            this, message, groupRepository, subjectRepository, true);
                }
                else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND)) {
                    state.setPosition(DeleteAssignmentState.Position.DELETE_MOD);
                    checkEmptyAssignments(id, state, assignments,
                            "Выберите для удаления задание");
                }
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    setLastMessage(state, "Операция удаления была прервана", null);
                    map.remove(id);
                }
                else {
                    setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
            case FILTER_MODE -> {
                if (triggerFilterAndCheckTheEnd(state))
                    state.setPosition(DeleteAssignmentState.Position.MAIN);
            }
            case DELETE_MOD -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPosition(DeleteAssignmentState.Position.MAIN);
                    setLastMessage(state,"Здесь вы можете удалить задание",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
                else if (basePaginationCheck(state, message)) {
                    state.setPosition(DeleteAssignmentState.Position.MAIN);
                    checkEmptyAssignments(id, state, assignments,
                            "Выберите для удаления задание");
                } else {
                    Optional<Assignment> assignment = assignments.stream().filter(a -> a.getId().toString().equals(message)).findFirst();
                    if (assignment.isPresent()) {
                        assignmentRepository.delete(assignment.get());
                        assignments.remove(assignment.get());
                        checkEmptyAssignments(id, state, assignments,
                                String.format("Успешно удалено задание \"%s\"",
                                        assignment.get().getTitle()));
                    } else {
                        checkEmptyAssignments(id, state, assignments,
                                "Задание не найдено в системе, выберите из тех, что под сообщением");
                    }
                }
            }
        }
    }
    private InlineKeyboardMarkup getInlineKeyboardAssignments(String userId, DeleteAssignmentState state,
                                                              Set<Assignment> assignments) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForAssignments = new ArrayList<>();
            for (Assignment assignment : assignments) {
                //add visible and invisible text
                namesForAssignments.add(new InlineKeyboardBuilder.Pair(
                        assignment.getTitle(),
                        assignment.getId().toString()
                ));
            }
            if (namesForAssignments.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForAssignments.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private Set<Assignment> getAllAssignmentsAccordFilters(DeleteAssignmentState state, User user) {
        Set<Assignment> assignments = new HashSet<>(assignmentRepository.findAll());
        return FilterAssignmentManager.applyFilters(state, assignments, user);
    }
    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {
        return InlineKeyboardBuilder.build(userId, state,
                new InlineKeyboardBuilder.Pair(TO_FILTER_VISIBLE, TO_FILTER)
        );
    }
    private boolean triggerFilterAndCheckTheEnd(DeleteAssignmentState state) {
        state.setPosition(DeleteAssignmentState.Position.FILTER_MODE);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "пользователь не найден!", null);
            map.remove(id);
            return true;
        }
        FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                this, message, groupRepository, subjectRepository, false);
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.COMPLETE) {
            setLastMessage(state, "Фильтр применен!",
                    getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
        return false;
    }
    private boolean checkEmptyAssignments(IdPair id, DeleteAssignmentState state, Set<Assignment> assignments,
                                  String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardAssignments(id.getUserId(), state, assignments);
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
            return false;
        } else {
            state.setPosition(DeleteAssignmentState.Position.MAIN);
            setLastMessage(state, "Не нашлось подходящих заданий!",
                    getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
    }
}