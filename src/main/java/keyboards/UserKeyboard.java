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
//
//        if (page == 1) {
//            KeyboardRow row1 = new KeyboardRow();
//            row1.add("/admin");
//            row1.add("/getAdminRights");
//            KeyboardRow row2 = new KeyboardRow();
//            row2.add("next➡");
//            rows.add(row1);
//            rows.add(row2);
//        }
//        else if (page == 2) {
//            KeyboardRow row1 = new KeyboardRow();
//            row1.add("/enableNotifications");
//            row1.add("/disableNotifications");
//            KeyboardRow row2 = new KeyboardRow();
//            row2.add("⬅previous");
//            row2.add("next➡");
//            rows.add(row1);
//            rows.add(row2);
//        }
//        else if (page == 3) {
//            KeyboardRow row1 = new KeyboardRow();
//            row1.add("/enterGroup");
//            row1.add("/exitGroup");
//            KeyboardRow row2 = new KeyboardRow();
//            row2.add("⬅previous");
//            row2.add("next➡");
//            rows.add(row1);
//            rows.add(row2);
//        }
//        else {
//            KeyboardRow row1 = new KeyboardRow();
//        }
//        markup.setKeyboard(rows);
//        return markup;
//        button1.setCallbackData("");
//        InlineKeyboardButton button2 = new InlineKeyboardButton("Включить авто уведомления");
//        button2.setCallbackData("/");
//        InlineKeyboardButton button3 = new InlineKeyboardButton("Выключить авто уведомления");
//        button3.setCallbackData("/");
//        InlineKeyboardButton button4 = new InlineKeyboardButton("Войти в группу");
//        button4.setCallbackData("/");
//        InlineKeyboardButton button5 = new InlineKeyboardButton("Выйти из группы");
//        button5.setCallbackData("/");
//        InlineKeyboardButton button6 = new InlineKeyboardButton("Получить права админа");
//        button6.setCallbackData("");
//        InlineKeyboardButton button7 = new InlineKeyboardButton("Выйти");
//        button7.setCallbackData("/logout");
//
//
//        List<InlineKeyboardButton> row = new ArrayList<>();
//        row.add(button1);
//        row.add(button2);
//        row.add(button3);
//        row.add(button4);
//        row.add(button5);
//        row.add(button6);
//        row.add(button7);
//
//
//        // Добавляем строку в список строк
//        rows.add(row);
//        markup.setKeyboard(rows);
//
//        return markup;
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
        row4.add("/logout");
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        markup.setKeyboard(rows);
        return markup;
    }
}
