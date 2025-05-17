package states;

import lombok.Getter;
import lombok.Setter;

public class GetAssignmentsState extends FilterAssignmentState {
    public GetAssignmentsState() {
        addCompleteFilter = true;
    }
    @Getter
    @Setter
    private GetAssignmentsState.Position position = GetAssignmentsState.Position.MAIN;
    public void setPosition(GetAssignmentsState.Position position) {
        pageNumber = 0;
        this.position = position;
    }

    public enum Position {
        MAIN,
        FILTER_MODE,
        GET_MODE
    }
}
