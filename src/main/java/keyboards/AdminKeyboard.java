package keyboards;

import mainBody.IdPair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class AdminKeyboard {

    public static ReplyKeyboard forUser() {
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
        KeyboardRow row6 = new KeyboardRow();
        row6.add("/helpAdmin");


        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);

        markup.setKeyboard(rows);
        markup.setOneTimeKeyboard(false);
        return markup;
    }

    public static ReplyKeyboard forChat(String userId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        //row1
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        //button 1
        InlineKeyboardButton button1 = new InlineKeyboardButton("/generateToken");
        button1.setCallbackData("/generateToken"  + "_" + userId);
        row1.add(button1);
        //button 2
        InlineKeyboardButton button2 = new InlineKeyboardButton("/deleteToken");
        button2.setCallbackData("/deleteToken"  + "_" + userId);
        row1.add(button2);

        //row2
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        //button 3
        InlineKeyboardButton button3 = new InlineKeyboardButton("/getMyTokens");
        button3.setCallbackData("/getMyTokens"  + "_" + userId);
        row2.add(button3);
        //button 4
        InlineKeyboardButton button4 = new InlineKeyboardButton("/createGroup");
        button4.setCallbackData("/createGroup"  + "_" + userId);
        row2.add(button4);

        //row3
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        //button 5
        InlineKeyboardButton button5 = new InlineKeyboardButton("/deleteGroup");
        button5.setCallbackData("/deleteGroup"  + "_" + userId);
        row3.add(button5);
        //button 6
        InlineKeyboardButton button6 = new InlineKeyboardButton("/createAssignment");
        button6.setCallbackData("/createAssignment"  + "_" + userId);
        row3.add(button6);

        //row4
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        //button 7
        InlineKeyboardButton button7 = new InlineKeyboardButton("/deleteAssignment");
        button7.setCallbackData("/deleteAssignment"  + "_" + userId);
        row4.add(button7);
        //button 8
        InlineKeyboardButton button8 = new InlineKeyboardButton("/createSubject");
        button8.setCallbackData("/createSubject"  + "_" + userId);
        row4.add(button8);

        //row5
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        //button 9
        InlineKeyboardButton button9 = new InlineKeyboardButton("/deleteSubject");
        button9.setCallbackData("/deleteSubject"  + "_" + userId);
        row5.add(button9);
        //button 10
        InlineKeyboardButton button10 = new InlineKeyboardButton("/start");
        button10.setCallbackData("/start"  + "_" + userId);
        row5.add(button10);

        //row6
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        //button 11
        InlineKeyboardButton button11 = new InlineKeyboardButton("/helpAdmin");
        button11.setCallbackData("/helpAdmin"  + "_" + userId);
        row6.add(button11);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);

        markup.setKeyboard(rows);
        return markup;
    }

    public static ReplyKeyboard getInlineKeyboard(IdPair id) {
        if (id.getChatId().equals(id.getUserId()))
            return forUser();
        else
            return forChat(id.getUserId());
    }
}
