package com.company.jmixpmflowbase.app;

import com.company.jmixpmflowbase.entity.Task;
import com.company.jmixpmflowbase.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.Authenticated;
import io.jmix.email.EmailInfo;
import io.jmix.email.EmailInfoBuilder;
import io.jmix.email.Emailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OverdueTasksInformer {

    private final Emailer emailer;

    protected DataManager dataManager;

    public OverdueTasksInformer(DataManager dataManager, Emailer emailer) {
        this.dataManager = dataManager;
        this.emailer = emailer;
    }

    @Authenticated
    public void inform() {
        List<Task> overdueTasks = dataManager.load(Task.class)
                .query("select t from Task_ t where t.dueDate <= :currentDate " +
                        "and (t.completed = false or t.completed is null)")
                .parameter("currentDate", LocalDateTime.now())
                .list();

        Map<User, List<Task>> tasksByUser = overdueTasks.stream()
                .filter(task -> task.getAssignee() != null)
                .collect(Collectors.groupingBy(Task::getAssignee));

        for (Map.Entry<User, List<Task>> entry : tasksByUser.entrySet()) {
            User user = entry.getKey();
            List<Task> tasks = entry.getValue();

            EmailInfo emailInfo = EmailInfoBuilder.create()
                    .setAddresses(user.getEmail())
                    .setSubject("Tasks overdue!!!")
                    .setBody(generateEmailBody(tasks))
                    .build();
            emailer.sendEmailAsync(emailInfo);
        }
    }

    private String generateEmailBody(List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tasks: " + "\n" );
        for (Task task : tasks) {
            sb.append(task.getName());
            sb.append("\n");
        }

        return sb.toString();
    }
}