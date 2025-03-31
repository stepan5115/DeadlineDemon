package keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class UserKeyboard {
    public static ReplyKeyboardMarkup getInlineKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/admin");
        row1.add("/getAdminRights");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/enableNotifications");
        row2.add("/disableNotifications");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/enterGroup");
        row3.add("/exitGroup");
        KeyboardRow row4 = new KeyboardRow();
        row4.add("/info");
        row4.add("/logout");
        KeyboardRow row5 = new KeyboardRow();
        row5.add("/help");
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }
}
