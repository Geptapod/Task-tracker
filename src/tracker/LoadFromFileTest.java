package tracker;

import tracker.service.AddTaskException;
import tracker.service.FileBackedTasksManager;

import java.io.File;
import java.time.Duration;

public class LoadFromFileTest {
    public static void main(String[] args) {
        File file = new File("save.csv");
        try {
            System.out.println(FileBackedTasksManager.loadFromFile(file));
        } catch (AddTaskException e) {
            throw new RuntimeException(e);
        }
    }
}
