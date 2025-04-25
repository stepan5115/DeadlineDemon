package mainBody;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class TitDesGroDeaSubState {
    public enum StateType {
        WAITING_TITLE,
        WAITING_DESCRIPTION,
        WAITING_GROUP,
        WAITING_DEADLINE,
        WAITING_SUBJECT
    }
    private String title;
    private String description;
    private Set<String> group = new HashSet<>();
    private LocalDateTime deadline;
    private StateType state;

    public TitDesGroDeaSubState(StateType state) {
        this.state = state;
    }
}
