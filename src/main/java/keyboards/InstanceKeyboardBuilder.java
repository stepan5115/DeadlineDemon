package keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InstanceKeyboardBuilder {
    public static InlineKeyboardMarkup getInlineKeyboard(String userId, boolean breakButton, boolean nextButton, String ... args) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button1 = new InlineKeyboardButton(args[i]);
            button1.setCallbackData(args[i]  + "_" + userId);
            row.add(button1);
            i++;
            if (i < args.length) {
                InlineKeyboardButton button2 = new InlineKeyboardButton(args[i]);
                button2.setCallbackData(args[i]  + "_" + userId);
                row.add(button2);
            }
            rows.add(row);
        }
        if (breakButton) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton("Закончить");
            button.setCallbackData("/breakOperation_" + userId);
            row.add(button);
            rows.add(row);
        }
        if (nextButton) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton("Далее");
            button.setCallbackData("/next_" + userId);
            row.add(button);
            rows.add(row);
        }
        markup.setKeyboard(rows);
        return markup;
    }
}
