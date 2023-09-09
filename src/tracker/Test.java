package tracker;

import tracker.service.FileBackedTasksManager;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        File file = new File("save.txt");
        System.out.println(FileBackedTasksManager.loadFromFile(file));
    }
}
