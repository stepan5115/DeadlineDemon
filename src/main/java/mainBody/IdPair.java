package mainBody;

import lombok.Getter;
import states.KeyboardManagerState;

public class IdPair {
    @Getter
    private String chatId;
    @Getter
    private String userId;
    @Getter
    private KeyboardManagerState state = null;

    public IdPair(String chatId, String userId) {
        this.chatId = chatId;
        this.userId = userId;
    }
    public IdPair(String chatId, String userId, KeyboardManagerState state) {
        this.chatId = chatId;
        this.userId = userId;
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof IdPair))
            return false;
        IdPair idPair = (IdPair) obj;
        return (chatId.equals(idPair.chatId) && userId.equals(idPair.userId));
    }
    @Override
    public int hashCode() {
        return chatId.hashCode() + userId.hashCode();
    }
}
