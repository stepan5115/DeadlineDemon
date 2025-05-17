package states;

import lombok.Getter;
import lombok.Setter;

public class CreateAssignmentState extends FilterAssignmentState {
    public CreateAssignmentState() {
        isCreate = true;
        global = true;
    }
    @Getter
    @Setter
    private Position position = Position.MAIN;
    public void setPosition(Position position) {
        pageNumber = 0;
        this.position = position;
    }

    public enum Position {
        MAIN,
        FILTER_MODE
    }
}
