package mainBody;

import lombok.Getter;

public class NamePasswordState {

    public enum StateType {
        WAITING_USERNAME,
        WAITING_PASSWORD
    }

    @Getter
    private final String username;
    @Getter
    private final StateType type;

    public NamePasswordState(StateType type, String username) {
        this.type = type;
        this.username = username;
    }
}
