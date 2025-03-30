package keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class AdminKeyboard {

    public static ReplyKeyboardMarkup getInlineKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/generateToken");
        row1.add("/deleteToken");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/getMyTokens");
        row2.add("/createGroup");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/deleteGroup");
        row3.add("/createAssignment");
        KeyboardRow row4 = new KeyboardRow();
        row4.add("/deleteAssignment");
        row4.add("/createSubject");
        KeyboardRow row5 = new KeyboardRow();
        row5.add("/deleteSubject");
        row5.add("/start");


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
