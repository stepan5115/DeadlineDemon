package states;

import lombok.Getter;
import lombok.Setter;

public class AuthState extends State {
    @Getter
    private Position position = Position.CHOICE;
    public void setPosition(Position position) {
        pageNumber = 0;
        this.position = position;
    }
    @Getter
    @Setter
    private String username = "";
    @Getter
    @Setter
    private String password = "";
    public enum Position {
        CHOICE,
        ADD_USERNAME,
        ADD_PASSWORD
    }
}