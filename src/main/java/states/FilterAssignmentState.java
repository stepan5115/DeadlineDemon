package states;

import lombok.Getter;
import lombok.Setter;
import sqlTables.Group;
import sqlTables.Subject;

import java.time.LocalDateTime;
import java.util.List;

public class FilterAssignmentState extends State {
    @Getter
    private PositionFilter positionFilter = PositionFilter.MAIN;
    @Getter
    @Setter
    private String titleFilter = null;
    @Getter
    @Setter
    private String descriptionFilter = null;
    @Getter
    @Setter
    private List<Group> filterGroups = List.of();
    @Getter
    @Setter
    private List<Subject> filterSubjects = List.of();
    @Getter
    @Setter
    private LocalDateTime deadlineFilter = null;
    @Getter
    @Setter
    boolean global = false; //generate buttons of user entities or all in system
    public void setPositionFilter(PositionFilter positionFilter) {
        pageNumber = 0;
        this.positionFilter = positionFilter;
    }
    @Getter
    @Setter
    private String assignmentName = "";
    public enum PositionFilter {
        MAIN,
        FILTER_TITLE,
        FILTER_TITLE_ADD,
        FILTER_DESCRIPTION,
        FILTER_DESCRIPTION_ADD,
        FILTER_GROUPS,
        FILTER_GROUPS_ADD,
        FILTER_SUBJECTS,
        FILTER_SUBJECTS_ADD,
        FILTER_DEADLINE,
        FILTER_DEADLINE_ADD,
        COMPLETE,
        BREAK
    }
}
