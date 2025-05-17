package mainBody;

import lombok.Getter;

public class IdPair {
    @Getter
    private String chatId;
    @Getter
    private String userId;

    public IdPair(String chatId, String userId) {
        this.chatId = chatId;
        this.userId = userId;
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
