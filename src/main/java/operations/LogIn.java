package operations;

import keyboards.UserKeyboard;
import mainBody.MyTelegramBot;
import mainBody.NamePasswordState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class LogIn extends Operation {
    private UserRepository userRepository;
    private String messageId;

    public LogIn(String chatId, MyTelegramBot bot, String message,
                 UserRepository userRepository, String messageId) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.messageId = messageId;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You already logged in!");
            bot.getLogInUserStates().remove(chatId);
        }
        else if (bot.getLogInUserStates().containsKey(chatId)) {
            NamePasswordState state = bot.getLogInUserStates().get(chatId);
            if (state.getType() == NamePasswordState.StateType.WAITING_USERNAME) {
                for (User user : bot.getAuthorizedUsers().values())
                    if (user.getUsername().equals(message)) {
                        sendMessage.setText("This account is busy by other user!");
                        bot.getLogInUserStates().remove(chatId);
                        try {
                            bot.execute(sendMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                if (userRepository.existsByUsername(message)) {
                    sendMessage.setText("Now, enter your password");
                    bot.getLogInUserStates().put(chatId, new NamePasswordState(NamePasswordState.StateType.WAITING_PASSWORD, message));
                } else {
                    sendMessage.setText("Can't find your name");
                    bot.getLogInUserStates().remove(chatId);
                }
            } else if (state.getType() == NamePasswordState.StateType.WAITING_PASSWORD) {
                Optional<User> user = userRepository.findByUsername(state.getUsername());
                if (message.length() > 15)
                    sendMessage.setText("Incorrect password");
                else if (user.isPresent() && PasswordEncryptor.matches(message, user.get().getPassword())) {
                    bot.getAuthorizedUsers().put(chatId, user.get());
                    sendMessage.setText("You logged in!");
                    sendMessage.setReplyMarkup(UserKeyboard.getInlineKeyboard());
                } else if(user.isPresent() && !PasswordEncryptor.matches(message, user.get().getPassword()))
                    sendMessage.setText("Incorrect password");
                else
                    sendMessage.setText("Something went wrong!");
                bot.getLogInUserStates().remove(chatId);
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(Integer.parseInt(messageId));
                //Add delete password in group
                try {
                    bot.execute(deleteMessage);
                } catch (TelegramApiException e) {
                    SendMessage tmp = new SendMessage();
                    tmp.setChatId(chatId);
                    tmp.setText("If I'm in a chat, give me admin rights! This way I can delete the passwords you entered for privacy! This is very important!");
                }
            }
        } else {
            sendMessage.setText("First, enter your name");
            bot.getLogInUserStates().put(chatId, new NamePasswordState(NamePasswordState.StateType.WAITING_USERNAME, null));
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
