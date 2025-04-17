package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class SubjectsKeyboard {
    public static ReplyKeyboard getInlineKeyboard(IdPair id, boolean adminMode) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser(adminMode);
        else
            return forChat(id.getUserId(), adminMode);
    }
    private static ReplyKeyboard forChat(String userId, boolean adminMode) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (adminMode) {
            //row 1
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            //button 1
            InlineKeyboardButton button1 = new InlineKeyboardButton("создать");
            button1.setCallbackData("/createSubject" + "_" + userId);
            row1.add(button1);
            //button 2
            InlineKeyboardButton button2 = new InlineKeyboardButton("удалить");
            button2.setCallbackData("/deleteSubject" + "_" + userId);
            row1.add(button2);
            rows.add(row1);
        }
        //row 2
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("назад");
        button3.setCallbackData("/back"  + "_" + userId);
        row3.add(button3);
        rows.add(row3);

        markup.setKeyboard(rows);
        return markup;
    }

    private static ReplyKeyboard forUser(boolean adminMode) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        if (adminMode) {
            //row 1
            KeyboardRow row1 = new KeyboardRow();
            row1.add("/createSubject");
            row1.add("/deleteSubject");
            rows.add(row1);
        }
        //row 2
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/back");
        rows.add(row2);

        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }
}
