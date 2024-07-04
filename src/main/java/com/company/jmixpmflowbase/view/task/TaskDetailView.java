package com.company.jmixpmflowbase.view.task;

import com.company.jmixpmflowbase.app.OverdueTasksInformer;
import com.company.jmixpmflowbase.entity.Task;

import com.company.jmixpmflowbase.view.main.MainView;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.FileStorageLocator;
import io.jmix.email.*;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Route(value = "tasks/:id", layout = MainView.class)
@ViewController("Task_.detail")
@ViewDescriptor("task-detail-view.xml")
@EditedEntityContainer("taskDc")
public class TaskDetailView extends StandardDetailView<Task> {
    private static final Logger log = LoggerFactory.getLogger(TaskDetailView.class);

    @Autowired
    private Emailer emailer;
    @Autowired
    private FileStorageLocator fileStorageLocator;
    @Autowired
    private Configuration configuration;

    @Subscribe(id = "notifyAssigneeBtn", subject = "clickListener")
    public void onNotifyAssigneeBtnClick(final ClickEvent<JmixButton> event) throws EmailException, IOException {

        Task task = getEditedEntity();
        FileRef attachment = task.getAttachment();
        EmailAttachment emailAttachment = null;
        if (attachment != null) {
            FileStorage fileStorage = fileStorageLocator.getDefault();
            byte[] attachmentBytes;
            try (InputStream is = fileStorage.openStream(task.getAttachment())) {
                attachmentBytes = IOUtils.toByteArray(is);
            }
            emailAttachment = new EmailAttachment(attachmentBytes, task.getAttachment().getFileName());
        }

        EmailInfo emailInfo = EmailInfoBuilder.create()
                .setAddresses(task.getAssignee().getEmail())
                .setSubject("Task assigned")
                .setBody(generateEmailBody(task))
                .build();

        if (emailAttachment != null) {
            emailInfo.setAttachments(Collections.singletonList(emailAttachment));
        }

        emailer.sendEmailAsync(emailInfo);

    }

    private String generateEmailBody(Task task) throws IOException {
        Template template = configuration.getTemplate("task-template.ftl");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("task", task);

        StringWriter stringWriter = new StringWriter();

        try {
            template.process(dataModel, stringWriter);
        } catch (TemplateException e) {
            log.error("Cannot process template!", e);
        }

        return stringWriter.toString();
    }

//    private String generateEmailBody(Task task) {
//        String template = """
//                <html>
//                    <body>
//                        <h1>Hello!</h1>
//                        <p>Task <b>%s</b> is assigned to you</p>
//                    </body>
//               </html>
//                """;
//        return String.format(template, task.getName());
//    }
}