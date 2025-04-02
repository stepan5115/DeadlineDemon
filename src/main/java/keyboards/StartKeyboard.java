package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class StartKeyboard {

    public static ReplyKeyboard getInlineKeyboard(IdPair id) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser();
        else
            return forChat(id.getUserId());
    }
    private static ReplyKeyboard forUser() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/login");
        row1.add("/register");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("/about");

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }
    private static ReplyKeyboard forChat(String userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //row1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("/login");
        button1.setCallbackData("/login"  + "_" + userId);
        row1.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("/register");
        button2.setCallbackData("/register"  + "_" + userId);
        row1.add(button2);

        //row2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("/about");
        button3.setCallbackData("/about"  + "_" + userId);
        row2.add(button3);

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }
}
