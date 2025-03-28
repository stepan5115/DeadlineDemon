package operations;

import mainBody.MyTelegramBot;
import mainBody.NamePasswordState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class LogIn extends Operation {
    private UserRepository userRepository;

    public LogIn(String chatId, MyTelegramBot bot, String message, UserRepository userRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You already logged in!");
        }
        else if (bot.getLogInUserStates().containsKey(chatId)) {
            NamePasswordState state = bot.getLogInUserStates().get(chatId);
            if (state.getType() == NamePasswordState.StateType.WAITING_USERNAME) {
                if (userRepository.existsByUsername(message)) {
                    sendMessage.setText("Now, enter your password");
                    bot.getLogInUserStates().put(chatId, new NamePasswordState(NamePasswordState.StateType.WAITING_PASSWORD, message));
                } else {
                    sendMessage.setText("Can't find your name");
                    bot.getLogInUserStates().remove(chatId);
                }
            } else if (state.getType() == NamePasswordState.StateType.WAITING_PASSWORD) {
                Optional<User> user = userRepository.findByUsername(state.getUsername());
                if (message.length() > 15) {
                    sendMessage.setText("Incorrect password");
                    bot.getLogInUserStates().remove(chatId);
                }
                else if (user.isPresent() && PasswordEncryptor.matches(message, user.get().getPassword())) {
                    bot.getLogInUserStates().remove(chatId);
                    bot.getAuthorizedUsers().put(chatId, user.get());
                    sendMessage.setText("You logged in!");
                } else if(user.isPresent() && !PasswordEncryptor.matches(message, user.get().getPassword())) {
                    sendMessage.setText("Incorrect password");
                    bot.getLogInUserStates().remove(chatId);
                }
                else {
                    sendMessage.setText("Something went wrong!");
                    bot.getLogInUserStates().remove(chatId);
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
