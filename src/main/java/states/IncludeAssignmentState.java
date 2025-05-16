package states;

import lombok.Getter;
import lombok.Setter;

public class IncludeAssignmentState extends FilterAssignmentState {
    @Getter
    @Setter
    private Position position = Position.MAIN;
    public void setPosition(Position position) {
        pageNumber = 0;
        this.position = position;
    }

    public enum Position {
        MAIN,
        FILTER_MODE,
        INCLUDE_MODE
    }
}