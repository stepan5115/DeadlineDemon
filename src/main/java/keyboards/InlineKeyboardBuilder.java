package keyboards;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import states.State;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardBuilder {
    public static String NEXT_COMMAND = "/next";
    public static String NEXT_COMMAND_VISIBLE = "далее";
    public static String PREV_COMMAND = "/prev";
    public static String PREV_COMMAND_VISIBLE = "назад";
    public static String BREAK_COMMAND = "/break";
    public static String BREAK_COMMAND_VISIBLE = "прервать";
    public static String COMPLETE_COMMAND = "/complete";
    public static String COMPLETE_COMMAND_VISIBLE = "выполнить";
    public static String CLEAR_COMMAND = "/clear";
    public static String CLEAR_COMMAND_VISIBLE = "очистить";
    private static int LIMIT = 7;

    private static List<List<InlineKeyboardButton>> formButtons(String userId, State state, Pair... visibleAndInvisible) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int countButtons = visibleAndInvisible.length;
        int maxIndexOfPage = (countButtons - 1)/LIMIT;
        state.setPageNumber(Math.max(state.getPageNumber(),0));
        state.setPageNumber(Math.min(state.getPageNumber(),maxIndexOfPage));
        int startIndex = state.getPageNumber()*LIMIT;
        for (int i = 0; i < LIMIT - 1; i++)
            if (startIndex + i < countButtons) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(visibleAndInvisible[startIndex + i].getVisible());
                button.setCallbackData(visibleAndInvisible[startIndex + i].getInvisible() + "_" + userId);
                rows.add(List.of(button));
            }
        List<InlineKeyboardButton> buttonsForPagination = getPaginationButtons(userId, state, maxIndexOfPage);
        if (!buttonsForPagination.isEmpty())
            rows.add(buttonsForPagination);
        if (state.isCompleteButton()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(COMPLETE_COMMAND_VISIBLE);
            button.setCallbackData(COMPLETE_COMMAND + "_" + userId);
            rows.add(List.of(button));
        }
        if (state.isBreakButton()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(BREAK_COMMAND_VISIBLE);
            button.setCallbackData(BREAK_COMMAND + "_" + userId);
            rows.add(List.of(button));
        }
        return rows;
    }

    public static InlineKeyboardMarkup build(String userId, State state, Pair... visibleAndInvisible) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(formButtons(userId, state, visibleAndInvisible));
        return markup;
    }

    public static InlineKeyboardMarkup getSimpleBreak(String userId, State state, Pair... visibleAndInvisible) {
        boolean tmp = state.isCompleteButton();
        state.setCompleteButton(false);
        InlineKeyboardMarkup result = build(userId, state, visibleAndInvisible);
        state.setCompleteButton(tmp);
        return result;
    }

    public static InlineKeyboardMarkup getSimpleClearComplete(String userId, State state, Pair... visibleAndInvisible) {
        boolean tmp = state.isBreakButton();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        state.setBreakButton(false);

        List<List<InlineKeyboardButton>> rows = formButtons(userId, state, visibleAndInvisible);
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(CLEAR_COMMAND_VISIBLE);
        button.setCallbackData(CLEAR_COMMAND + "_" + userId);
        rows.add(List.of(button));
        markup.setKeyboard(rows);

        state.setBreakButton(tmp);
        return markup;
    }

    private static List<InlineKeyboardButton> getPaginationButtons(String userId, State state, int maxIndexOfPage) {
        state.setPageNumber(Math.max(state.getPageNumber(),0));
        state.setPageNumber(Math.min(state.getPageNumber(),maxIndexOfPage));
        List<InlineKeyboardButton> buttonsForPagination = new ArrayList<>();
        if (maxIndexOfPage > state.getPageNumber()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(NEXT_COMMAND_VISIBLE);
            button.setCallbackData(NEXT_COMMAND + "_" + userId);
            buttonsForPagination.add(button);
        }
        if (state.getPageNumber() > 0) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(PREV_COMMAND_VISIBLE);
            button.setCallbackData(PREV_COMMAND + "_" + userId);
            buttonsForPagination.add(button);
        }
        return buttonsForPagination;
    }

    public static class Pair {
        @Getter
        @Setter
        private String visible;
        @Getter
        @Setter
        private String invisible;
        public Pair(String visible, String invisible) {
            this.visible = visible;
            this.invisible = invisible;
        }
    }
}