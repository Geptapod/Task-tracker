import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.service.InMemoryTaskManager;
import tracker.service.TaskManager;
import tracker.task.EpicTask;
import tracker.task.SubTask;
import tracker.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class InMemoryTaskManagerTest {
    public InMemoryTaskManager taskManager;

    public Task task1 = new Task("Запустить приложение", "Run tracker", "NEW", 0,
            Duration.ofMinutes(5), LocalDateTime.now());
    public Task task2 = new Task("Переезд", "Собрать коробки", "NEW", 0,
            Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
    public EpicTask epicTask = new EpicTask("Поездка", "Поездка на дачу", "NEW", 0,
            Duration.ZERO, LocalDateTime.now());
    public SubTask subTask1 = new SubTask("Уборка", "Сделать уборку перед отъездом",
            "NEW", 0, 3,
            Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
    public SubTask subTask2 = new SubTask("Сборы", "Собрать вещи",
            "NEW", 0, 3,
            Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JULY, 1, 10, 0));
    public SubTask subTask3 = new SubTask("Билеты", "Купить билеты",
            "NEW", 0, 3,
            Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0));
    public EpicTask epicTask2 = new EpicTask("Void", "voiding", "NEW", 1,
            Duration.ofMinutes(60), LocalDateTime.MAX);

    @BeforeEach
    public void recreateTaskManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void idShouldBe1() {
        assertEquals(1, taskManager.generateNewId());
    }

    @Test
    public void shouldNotAddTaskWitSameStartTime() {
        taskManager.addSomeTask(task2);
        taskManager.addSomeTask(new Task("Отпуск", "Собрать компанию", "NEW", 0,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JUNE, 1, 10, 0)));
        assertEquals(1,taskManager.getTasksList().size());
    }
}
