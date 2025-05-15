package states;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public abstract class State {
    @Getter
    @Setter
    protected int pageNumber = 0;
    @Getter
    @Setter
    boolean breakButton = true;
    @Getter
    @Setter
    boolean completeButton = true;
    @Getter
    @Setter
    Integer messageForWorkId = null;
    @Getter
    @Setter
    InlineKeyboardMarkup lastKeyboardMarkup = null;
}
