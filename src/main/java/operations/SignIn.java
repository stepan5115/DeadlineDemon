package operations;

import mainBody.MyTelegramBot;
import mainBody.NamePasswordState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

public class SignIn extends Operation {
    private UserRepository userRepository;

    public SignIn(String chatId, MyTelegramBot bot, String message, UserRepository userRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You already logged in!");
        }
        else if (bot.getSignInUserStates().containsKey(chatId)) {
            NamePasswordState state = bot.getSignInUserStates().get(chatId);
            if (state.getType() == NamePasswordState.StateType.WAITING_USERNAME) {
                if (message.length() > 20) {
                    sendMessage.setText("Too long username!");
                    bot.getSignInUserStates().remove(chatId);
                }
                else if (!userRepository.existsByUsername(message)) {
                    sendMessage.setText("Now, enter your password");
                    bot.getSignInUserStates().put(chatId, new NamePasswordState(NamePasswordState.StateType.WAITING_PASSWORD, message));
                } else {
                    sendMessage.setText("User with this name already exists!");
                    bot.getSignInUserStates().remove(chatId);
                }
            } else if (state.getType() == NamePasswordState.StateType.WAITING_PASSWORD) {
                if (message.length() > 15) {
                    sendMessage.setText("Too long password!");
                    bot.getSignInUserStates().remove(chatId);
                }
                else if (!userRepository.existsByUsername(state.getUsername())) {
                    User newUser = new User();
                    newUser.setUsername(state.getUsername());
                    newUser.setPassword(PasswordEncryptor.encrypt(message));
                    userRepository.save(newUser);

                    bot.getSignInUserStates().remove(chatId);
                    bot.getAuthorizedUsers().put(chatId, newUser);
                    sendMessage.setText("Successfully sign in!");
                } else {
                    sendMessage.setText("Something went wrong!");
                    bot.getSignInUserStates().remove(chatId);
                }
            }
        } else {
            sendMessage.setText("First, enter your name");
            bot.getSignInUserStates().put(chatId, new NamePasswordState(NamePasswordState.StateType.WAITING_USERNAME, null));
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
