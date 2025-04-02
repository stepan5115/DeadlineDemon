package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class UserKeyboard {

    public static ReplyKeyboard getInlineKeyboard(IdPair id) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser();
        else
            return forChat(id.getUserId());
    }

    private static ReplyKeyboard forChat(String userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //row1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("/admin");
        button1.setCallbackData("/admin"  + "_" + userId);
        row1.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("/getAdminRights");
        button2.setCallbackData("/getAdminRights"  + "_" + userId);
        row1.add(button2);

        //row2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("/enableNotifications");
        button3.setCallbackData("/enableNotifications"  + "_" + userId);
        row2.add(button3);
        //button 4
        InlineKeyboardButton button4 = new InlineKeyboardButton("/disableNotifications");
        button4.setCallbackData("/disableNotifications"  + "_" + userId);
        row2.add(button4);

        //row3
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        //button 5
        InlineKeyboardButton button5 = new InlineKeyboardButton("/enterGroup");
        button5.setCallbackData("/enterGroup"  + "_" + userId);
        row3.add(button5);
        //button 6
        InlineKeyboardButton button6 = new InlineKeyboardButton("/exitGroup");
        button6.setCallbackData("/exitGroup"  + "_" + userId);
        row3.add(button6);

        //row4
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        //button 7
        InlineKeyboardButton button7 = new InlineKeyboardButton("/info");
        button7.setCallbackData("/info"  + "_" + userId);
        row4.add(button7);
        //button 8
        InlineKeyboardButton button8 = new InlineKeyboardButton("/logout");
        button8.setCallbackData("/logout"  + "_" + userId);
        row4.add(button8);

        //row5
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        //button 9
        InlineKeyboardButton button9 = new InlineKeyboardButton("/help");
        button9.setCallbackData("/help"  + "_" + userId);
        row5.add(button9);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);

        markup.setKeyboard(rows);
        return markup;
    }
    
    private static ReplyKeyboard forUser() {
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
