package operations;

import keyboards.ChooseKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import mainBody.NamePasswordState;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class LogIn extends Operation {
    private final UserRepository userRepository;

    public LogIn(IdPair id, String messageId,
                 MyTelegramBot bot, String message,
                 UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Вы уже вошли");
            bot.getLogInUserStates().remove(id);
        }
        else if (bot.getLogInUserStates().containsKey(id)) {
            NamePasswordState state = bot.getLogInUserStates().get(id);
            if (state.getType() == NamePasswordState.StateType.WAITING_USERNAME) {
                for (IdPair userId : bot.getAuthorizedUsers().keySet())
                    if (bot.getAuthorizedUsers().get(userId).getUsername().equals(message)
                            && !userId.getUserId().equals(id.getUserId())) {
                        sendMessage.setText("Этот аккаунт сейчас занят другим пользователем!");
                        bot.getLogInUserStates().remove(id);
                        sendReply();
                        return;
                    }
                if (userRepository.existsByUsername(message)) {
                    sendMessage.setText("Теперь, введите пароль");
                    bot.getLogInUserStates().put(id, new NamePasswordState(NamePasswordState.StateType.WAITING_PASSWORD, message));
                } else {
                    sendMessage.setText("Не могу найти введенный логин");
                    bot.getLogInUserStates().remove(id);
                }
            } else if (state.getType() == NamePasswordState.StateType.WAITING_PASSWORD) {
                Optional<User> user = userRepository.findByUsername(state.getUsername());
                if (message.length() > 15)
                    sendMessage.setText("Неправильный пароль");
                else if (user.isPresent() && PasswordEncryptor.matches(message, user.get().getPassword())) {
                    bot.getAuthorizedUsers().put(id, user.get());
                    sendMessage.setText("Успешный вход");
                    sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, user.get().isCanEditTasks()));
                } else if(user.isPresent() && !PasswordEncryptor.matches(message, user.get().getPassword()))
                    sendMessage.setText("Неверный пароль");
                else
                    sendMessage.setText("Что-то пошло не так");
                bot.getLogInUserStates().remove(id);
                replyForPrivacyMessage();
            }
        } else {
            sendMessage.setText("Введите логин");
            bot.getLogInUserStates().put(id, new NamePasswordState(NamePasswordState.StateType.WAITING_USERNAME, null));
        }
        sendReply();
    }
}
