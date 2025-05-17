package states;

import lombok.Getter;
import lombok.Setter;

public class DeleteAssignmentState extends FilterAssignmentState {
    public DeleteAssignmentState() {
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
        FILTER_MODE,
        DELETE_MOD
    }
}
