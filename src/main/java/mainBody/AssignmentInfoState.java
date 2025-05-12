package mainBody;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class AssignmentInfoState {
    public enum StateType {
        WAITING_GROUPS,
        WAITING_DEADLINE,
        WAITING_SUBJECTS,
        WAITING_COMPLETE_STATUS,
        WAITING_ASSIGNMENT
    }
    private Set<String> groups = new HashSet<>();
    private LocalDateTime deadline;
    private Set<String> subjects = new HashSet<>();
    private String complete;
    private StateType state;

    public AssignmentInfoState(AssignmentInfoState.StateType state) {
        this.state = state;
    }
}
