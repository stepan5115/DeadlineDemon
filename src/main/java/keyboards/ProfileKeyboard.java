package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ProfileKeyboard {
    public static ReplyKeyboard getInlineKeyboard(IdPair id, boolean adminMode) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser(adminMode);
        else
            return forChat(id.getUserId(), adminMode);
    }
    private static ReplyKeyboard forChat(String userId, boolean adminMode) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (!adminMode) {
            //row 1
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            //button 1
            InlineKeyboardButton button1 = new InlineKeyboardButton("получить права админа");
            button1.setCallbackData("/getAdminRights" + "_" + userId);
            row1.add(button1);
            rows.add(row1);
        }
        //row 2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("информация");
        button1.setCallbackData("/info"  + "_" + userId);
        row2.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("выйти");
        button2.setCallbackData("/logout"  + "_" + userId);
        row2.add(button2);
        rows.add(row2);
        //row 3
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("интервал уведомлений");
        button3.setCallbackData("/setInterval"  + "_" + userId);
        row3.add(button3);
        rows.add(row3);
        //row 4
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        //button 4
        InlineKeyboardButton button4 = new InlineKeyboardButton("назад");
        button4.setCallbackData("/back"  + "_" + userId);
        row4.add(button4);
        rows.add(row4);

        markup.setKeyboard(rows);
        return markup;
    }

    private static ReplyKeyboard forUser(boolean adminMode) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();

        if (!adminMode) {
            //row 1
            KeyboardRow row1 = new KeyboardRow();
            row1.add("/getAdminRights");
            rows.add(row1);
        }
        //row 2
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/info");
        row2.add("/logout");
        rows.add(row2);
        //row 3
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/setInterval");
        rows.add(row3);
        //row 4
        KeyboardRow row4 = new KeyboardRow();
        row4.add("/back");
        rows.add(row4);

        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }
}
