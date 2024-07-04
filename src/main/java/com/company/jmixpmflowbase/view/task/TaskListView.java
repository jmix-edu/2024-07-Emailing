package com.company.jmixpmflowbase.view.task;

import com.company.jmixpmflowbase.app.OverdueTasksInformer;
import com.company.jmixpmflowbase.entity.Task;

import com.company.jmixpmflowbase.view.main.MainView;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "tasks", layout = MainView.class)
@ViewController("Task_.list")
@ViewDescriptor("task-list-view.xml")
@LookupComponent("tasksDataGrid")
@DialogMode(width = "64em")
public class TaskListView extends StandardListView<Task> {

    @Autowired
    private OverdueTasksInformer overdueTasksInformer;

    @Subscribe(id = "checkOverdueTasksBtn", subject = "clickListener")
    public void onDetailActionsClick(final ClickEvent<HorizontalLayout> event) {
        overdueTasksInformer.inform();
    }
}