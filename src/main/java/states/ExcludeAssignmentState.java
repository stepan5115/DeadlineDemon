package states;

import lombok.Getter;
import lombok.Setter;
import sqlTables.Group;
import sqlTables.Subject;

import java.time.LocalDateTime;
import java.util.List;

public class ExcludeAssignmentState extends FilterAssignmentState {
    @Getter
    @Setter
    private Position position = Position.MAIN;

    public enum Position {
        MAIN,
        FILTER_MODE
    }
}