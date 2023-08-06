package tracker.service;

import tracker.service.InMemoryTaskManager;
import tracker.service.TaskManager;

public class Managers<T extends TaskManager> {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
