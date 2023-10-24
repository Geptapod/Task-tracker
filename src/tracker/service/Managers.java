package tracker.service;

import tracker.server.HttpTaskManager;
import tracker.service.InMemoryTaskManager;
import tracker.service.TaskManager;

public class Managers {

    public static TaskManager getDefault(int flag) {
        if (flag==0) {
            return new InMemoryTaskManager();
        } else if (flag==1) {
            return new FileBackedTasksManager("Tasks");
        } else {
            return new HttpTaskManager("http://localhost:8078/", "Tasks", true);
        }
    }
}
