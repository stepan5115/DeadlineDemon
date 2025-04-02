package operations;

import keyboards.InstanceKeyboardBuilder;
import keyboards.UserKeyboard;
import mainBody.MyTelegramBot;
import mainBody.NamePasswordState;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

public class SignIn extends Operation {
    private UserRepository userRepository;

    public SignIn(String chatId, String userId,String messageId,
                  MyTelegramBot bot, String message, UserRepository userRepository) {
        super(chatId, userId, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(userId))
            sendMessage.setText("You already logged in!");
        else if (bot.getSignInUserStates().containsKey(userId)) {
            NamePasswordState state = bot.getSignInUserStates().get(userId);
            if (state.getType() == NamePasswordState.StateType.WAITING_USERNAME) {
                if (message.length() > 20) {
                    sendMessage.setText("Too long username! Try again!");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                }
                else if (!userRepository.existsByUsername(message)) {
                    sendMessage.setText("Now, enter your password");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                    bot.getSignInUserStates().put(userId, new NamePasswordState(NamePasswordState.StateType.WAITING_PASSWORD, message));
                } else {
                    sendMessage.setText("User with this name already exists! Try again!");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                }
            } else if (state.getType() == NamePasswordState.StateType.WAITING_PASSWORD) {
                if (message.length() > 15) {
                    sendMessage.setText("Too long password! Try again!");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                }
                else if (!userRepository.existsByUsername(state.getUsername())) {
                    User newUser = new User();
                    newUser.setUsername(state.getUsername());
                    newUser.setPassword(PasswordEncryptor.encrypt(message));
                    userRepository.save(newUser);

                    bot.getAuthorizedUsers().put(userId, newUser);
                    bot.getSignInUserStates().remove(userId);
                    sendMessage.setText("Successfully sign in!");
                    sendMessage.setReplyMarkup(UserKeyboard.getInlineKeyboard());
                } else {
                    sendMessage.setText("Something went wrong!");
                    bot.getSignInUserStates().remove(userId);
                }
                replyForPrivacyMessage();
            }
        } else {
            sendMessage.setText("First, enter your name");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            bot.getSignInUserStates().put(userId, new NamePasswordState(NamePasswordState.StateType.WAITING_USERNAME, null));
        }
        sendReply();
    }
}
