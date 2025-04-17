package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ChooseKeyboard {
    public static ReplyKeyboard getInlineKeyboard(IdPair id, boolean adminMode) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser(adminMode);
        else
            return forChat(id.getUserId(), adminMode);
    }
    private static ReplyKeyboard forChat(String userId, boolean adminMode) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //row1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("уведомления");
        button1.setCallbackData("/notifications"  + "_" + userId);
        row1.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("группы");
        button2.setCallbackData("/groups"  + "_" + userId);
        row1.add(button2);
        rows.add(row1);

        //row2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("профиль");
        button3.setCallbackData("/profile"  + "_" + userId);
        row2.add(button3);
        //button 4
        InlineKeyboardButton button4 = new InlineKeyboardButton("задачки");
        button4.setCallbackData("/tasks"  + "_" + userId);
        row2.add(button4);
        rows.add(row2);

        if (adminMode) {
            //row3
            List<InlineKeyboardButton> row3 = new ArrayList<>();
            //button 5
            InlineKeyboardButton button5 = new InlineKeyboardButton("токены");
            button5.setCallbackData("/tokens" + "_" + userId);
            row3.add(button5);
            //button 6
            InlineKeyboardButton button6 = new InlineKeyboardButton("дисциплины");
            button6.setCallbackData("/subjects" + "_" + userId);
            row3.add(button6);
            rows.add(row3);
        }

        //row4
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        //button 7
        InlineKeyboardButton button7 = new InlineKeyboardButton("помощь");
        button7.setCallbackData("/help"  + "_" + userId);
        row4.add(button7);
        rows.add(row4);

        markup.setKeyboard(rows);
        return markup;
    }

    private static ReplyKeyboard forUser(boolean adminMode) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        //row 1
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/notifications");
        row1.add("/groups");
        rows.add(row1);
        //row 2
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/profile");
        row2.add("/tasks");
        rows.add(row2);
        if (adminMode) {
            //row 3
            KeyboardRow row3 = new KeyboardRow();
            row3.add("/tokens");
            row3.add("/subjects");
            rows.add(row3);
        }
        //row 4
        KeyboardRow row4 = new KeyboardRow();
        row4.add("/help");
        rows.add(row4);

        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }
}
