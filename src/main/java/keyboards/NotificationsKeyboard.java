package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class NotificationsKeyboard {
    public static ReplyKeyboard getInlineKeyboard(IdPair id) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser();
        else
            return forChat(id.getUserId());
    }
    private static ReplyKeyboard forChat(String userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //row 1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("выключить");
        button1.setCallbackData("/disable"  + "_" + userId);
        row1.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("включить");
        button2.setCallbackData("/enable"  + "_" + userId);
        row1.add(button2);
        rows.add(row1);
        //row 2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("отключить задание");
        button3.setCallbackData("/excludeAssignment"  + "_" + userId);
        row2.add(button3);
        //button 4
        InlineKeyboardButton button4 = new InlineKeyboardButton("включить задание");
        button4.setCallbackData("/includeAssignment"  + "_" + userId);
        row2.add(button4);
        rows.add(row2);
        //row 3
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        //button 5
        InlineKeyboardButton button5 = new InlineKeyboardButton("отключить предмет");
        button5.setCallbackData("/excludeSubject"  + "_" + userId);
        row3.add(button5);
        //button 4
        InlineKeyboardButton button6 = new InlineKeyboardButton("включить предмет");
        button6.setCallbackData("/includeAssignment"  + "_" + userId);
        row3.add(button6);
        rows.add(row3);
        //row 4
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        //button 7
        InlineKeyboardButton button7 = new InlineKeyboardButton("назад");
        button7.setCallbackData("/back"  + "_" + userId);
        row4.add(button7);
        rows.add(row4);

        markup.setKeyboard(rows);
        return markup;
    }

    private static ReplyKeyboard forUser() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        //row 1
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/disable");
        row1.add("/enable");
        rows.add(row1);
        //row 2
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/excludeAssignment");
        row2.add("/includeAssignment");
        rows.add(row2);
        //row 3
        KeyboardRow row3 = new KeyboardRow();
        row3.add("/excludeSubject");
        row3.add("/includeSubject");
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
