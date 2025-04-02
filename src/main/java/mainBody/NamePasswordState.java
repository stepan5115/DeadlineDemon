package mainBody;

import lombok.Getter;

@Getter
public class NamePasswordState {

    public enum StateType {
        WAITING_USERNAME,
        WAITING_PASSWORD
    }

    private final String username;
    private final StateType type;

    public NamePasswordState(StateType type, String username) {
        this.type = type;
        this.username = username;
    }
}
