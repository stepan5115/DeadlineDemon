package mainBody;

import lombok.Getter;
import lombok.Setter;
import sqlTables.SubjectRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TitDesGroDeaSubState {
    public enum StateType {
        WAITING_TITLE,
        WAITING_DESCRIPTION,
        WAITING_GROUP,
        WAITING_DEADLINE,
        WAITING_SUBJECT
    }
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private List<String> group = new ArrayList<>();
    @Getter
    @Setter
    private LocalDateTime deadline;
    @Getter
    @Setter
    private StateType state;

    public TitDesGroDeaSubState(StateType state) {
        this.state = state;
    }
}
