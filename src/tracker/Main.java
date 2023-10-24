package tracker;

import tracker.server.HttpTaskServer;
import tracker.server.KVServer;
import tracker.service.AddTaskException;
import tracker.service.TaskManager;
import tracker.task.EpicTask;
import tracker.service.Managers;
import tracker.task.SubTask;
import tracker.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager manager = Managers.getDefault(3);

        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();

        Task task1 = new Task("Запустить приложение", "Run tracker", "NEW", 0,
                Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Переезд", "Собрать коробки", "NEW", 0,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
        EpicTask epicTask = new EpicTask("Поездка", "Поездка на дачу", "NEW", 0,
                Duration.ZERO, LocalDateTime.now());
        SubTask subTask1 = new SubTask("Уборка", "Сделать уборку перед отъездом",
                "NEW", 0, 3,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
        SubTask subTask2 = new SubTask("Сборы", "Собрать вещи",
                "NEW", 0, 3,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JULY, 1, 10, 0));
        SubTask subTask3 = new SubTask("Билеты", "Купить билеты",
                "NEW", 0, 3,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
        EpicTask epicTask2 = new EpicTask("Void", "voiding", "NEW", 1,
                Duration.ofMinutes(60), LocalDateTime.MAX);

        //проверка наполнения задачами
        try {
            manager.addSomeTask(task1);
            manager.addSomeTask(task2);
            manager.addSomeTask(epicTask);
            manager.addSomeTask(subTask1);
            manager.addSomeTask(subTask2);
            manager.addSomeTask(subTask3);
            manager.addSomeTask(epicTask2);


        } catch (AddTaskException e) {
            throw new RuntimeException(e);
        }

        manager.getPrioritizedTasks();

        // Проверка вывода истории
        manager.getTaskById(4L);
        manager.getTaskById(5L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(3L);
        manager.getTaskById(4L);
        manager.getTaskById(7L);

        System.out.println("История просмотра задач:");
        for (Task task:manager.getHistory())
            System.out.println(task);

        manager.deleteTaskById(3L);
        System.out.println("История просмотра задач после удаления:");
        for (Task task:manager.getHistory())
            System.out.println(task);

        System.out.println("Запросы получения:");
        manager.getTasks();
        manager.getEpicTasks();
        manager.getSubTasks();

        //Проверка удаления задач
        manager.deleteTasks();
        manager.deleteEpicTasks();

        //Проверим что вернёт после удаления
        manager.getTasks();
        manager.getEpicTasks();
        manager.getSubTasks();
    }
}

