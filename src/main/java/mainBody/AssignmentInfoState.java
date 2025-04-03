package mainBody;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AssignmentInfoState {
    public enum StateType {
        WAITING_GROUPS,
        WAITING_DEADLINE,
        WAITING_SUBJECTS,
        WAITING_ASSIGNMENT
    }
    private List<String> groups = new ArrayList<>();
    private LocalDateTime deadline;
    private List<String> subjects = new ArrayList<>();
    private StateType state;

    public AssignmentInfoState(AssignmentInfoState.StateType state) {
        this.state = state;
    }
}
