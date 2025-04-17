package operations;

import keyboards.ChooseKeyboard;
import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import mainBody.NamePasswordState;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

public class SignIn extends Operation {
    private final UserRepository userRepository;

    public SignIn(IdPair id,String messageId,
                  MyTelegramBot bot, String message, UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You already logged in!");
        else if (bot.getSignInUserStates().containsKey(id)) {
            NamePasswordState state = bot.getSignInUserStates().get(id);
            if (state.getType() == NamePasswordState.StateType.WAITING_USERNAME) {
                if (message.length() > 20) {
                    sendMessage.setText("Too long username! Try again!");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                }
                else if (!userRepository.existsByUsername(message)) {
                    sendMessage.setText("Now, enter your password");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                    bot.getSignInUserStates().put(id, new NamePasswordState(NamePasswordState.StateType.WAITING_PASSWORD, message));
                } else {
                    sendMessage.setText("User with this name already exists! Try again!");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
                }
            } else if (state.getType() == NamePasswordState.StateType.WAITING_PASSWORD) {
                if (message.length() > 15) {
                    sendMessage.setText("Too long password! Try again!");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                }
                else if (!userRepository.existsByUsername(state.getUsername())) {
                    User newUser = new User();
                    newUser.setUsername(state.getUsername());
                    newUser.setPassword(PasswordEncryptor.encrypt(message));
                    userRepository.save(newUser);

                    bot.getAuthorizedUsers().put(id, newUser);
                    bot.getSignInUserStates().remove(id);
                    sendMessage.setText("Successfully sign in!");
                    sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, newUser.isCanEditTasks()));
                } else {
                    sendMessage.setText("Something went wrong!");
                    bot.getSignInUserStates().remove(id);
                }
                replyForPrivacyMessage();
            }
        } else {
            sendMessage.setText("First, enter your name");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            bot.getSignInUserStates().put(id, new NamePasswordState(NamePasswordState.StateType.WAITING_USERNAME, null));
        }
        sendReply();
    }
}
