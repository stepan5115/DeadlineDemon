package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.AdminTokenRepository;
import sqlTables.UserRepository;
import states.EnterTokenState;

public class KeyboardManager extends Operation {
    public KeyboardManager(IdPair id, String messageId,
                               MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                //setLastMessage();
                return;
            }
            if (checkNoAdminRights()) {
                //map.remove(id);
                return;
            }
            //if (!map.containsKey(id))
            //    map.put(id, new EnterTokenState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            //map.remove(id);
        }
    }
}
