package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class GroupsKeyboard {
    public static ReplyKeyboard getInlineKeyboard(IdPair id, boolean adminMode) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser(adminMode);
        else
            return forChat(id.getUserId(), adminMode);
    }
    private static ReplyKeyboard forChat(String userId, boolean adminMode) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //row 1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("войти");
        button1.setCallbackData("/enter"  + "_" + userId);
        row1.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("выйти");
        button2.setCallbackData("/exit"  + "_" + userId);
        row1.add(button2);
        rows.add(row1);

        if (adminMode) {
            //row 2
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            //button 3
            InlineKeyboardButton button3 = new InlineKeyboardButton("создать");
            button3.setCallbackData("/createGroup" + "_" + userId);
            row2.add(button3);
            //button 4
            InlineKeyboardButton button4 = new InlineKeyboardButton("удалить");
            button4.setCallbackData("/deleteGroup" + "_" + userId);
            row2.add(button4);
            rows.add(row2);
        }

        //row 3
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        //button 5
        InlineKeyboardButton button5 = new InlineKeyboardButton("назад");
        button5.setCallbackData("/back"  + "_" + userId);
        row3.add(button5);
        rows.add(row3);

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
        row1.add("/enter");
        row1.add("/exit");
        rows.add(row1);
        if (adminMode) {
            //row 2
            KeyboardRow row2 = new KeyboardRow();
            row2.add("/createGroup");
            row2.add("/deleteGroup");
            rows.add(row2);
        }
        //row 3
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/back");
        rows.add(row3);

        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }
}
