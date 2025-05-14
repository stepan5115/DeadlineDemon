package APIOperations;

import jakarta.transaction.Transactional;
import keyboards.ChooseKeyboard;
import org.springframework.http.ResponseEntity;
import sqlTables.*;
import utils.PasswordEncryptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CreateAssignmentOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String title;
    private final String description;
    private final List<String> groupNames;
    private final LocalDateTime deadline;
    private final String subjectId;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected CreateAssignmentOperation(String username, String password, String title, String description,
                                        List<String> groupNames, LocalDateTime deadline,
                                        String subjectId, UserRepository userRepository,
                                        AssignmentRepository assignmentRepository, GroupRepository groupRepository,
                                        SubjectRepository subjectRepository) {
        this.username = username;
        this.password = password;
        this.title = title;
        this.description = description;
        this.groupNames = groupNames;
        this.deadline = deadline;
        this.subjectId = subjectId;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public void run() {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            result = "WRONG: Пользователь не найден";
        } else if (password.length() > 15) {
            result = "WRONG: Неверный пароль";
        } else if (PasswordEncryptor.matches(password, user.get().getPassword())) {
            if (user.get().isCanEditTasks()) {
                try {
                    Optional<Assignment> tmp = assignmentRepository.getAssignmentByTitleIgnoreCase(title);
                    if (tmp.isEmpty()) {
                        Optional<Subject> subject = subjectRepository.findById(Long.parseLong(subjectId));
                        if (subject.isPresent()) {
                            if (deadline.isAfter(LocalDateTime.now())) {
                                boolean flag = true;
                                for (String groupName : groupNames)
                                    if (!groupRepository.existsByName(groupName)) {
                                        flag = false;
                                        break;
                                    }
                                if (flag) {
                                    Assignment newAssignment = new Assignment();
                                    newAssignment.setTitle(title);
                                    newAssignment.setDescription(description);
                                    for (String groupName : groupNames)
                                        newAssignment.getTargetGroups().add(groupName);
                                    newAssignment.setDeadline(deadline);
                                    newAssignment.setSubject(subject.get());
                                    assignmentRepository.save(newAssignment);
                                    result = "OK";
                                } else
                                    result = "WRONG: одна или более групп отсутствуют в системе";
                            } else
                                result = "WRONG: дедлайн уже прошел";
                        } else
                            result = "WRONG: дисциплина не найдена";
                    } else
                        result = "WRONG: название занято другим заданием";
                } catch (Throwable e) {
                    result = "WRONG: server error";
                }
            } else
                result = "WRONG: у вас нет прав на это";
        } else
            result = "WRONG: Неверный пароль";

        if (callback != null) {
            callback.onComplete(result);
        }
    }
    @Override
    public void setCallback(OperationCallback callback) {
        this.callback = callback;
    }

    @Override
    public String getResult() {
        return result;
    }
}