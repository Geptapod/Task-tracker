import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tracker.service.InMemoryTaskManager;
import tracker.task.EpicTask;
import tracker.task.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTaskTest {

    private static InMemoryTaskManager taskManager;

    @BeforeAll
    public static void newInInMemoryTaskManager(){
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldBeStatusNewIfListSubTasksIsEmpty() {
        EpicTask epicTask2 = new EpicTask("Void", "voiding", "NEW", 1,
                Duration.ofMinutes(60), LocalDateTime.MAX);
        taskManager.addSomeTask(epicTask2);
        assertEquals("NEW",taskManager.getTaskById(1L).getStatus().toString());
    }

    @Test
    public void shouldBeStatusDoneIfListSubTasksIsDone() {
        EpicTask epicTask2 = new EpicTask("Void", "voiding", "NEW", 1,
                Duration.ofMinutes(60), LocalDateTime.MAX);
        SubTask subTask2 = new SubTask("Сборы", "Собрать вещи",
                "DONE", 0, 1,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JULY, 1, 10, 0));
        taskManager.addSomeTask(epicTask2);
        taskManager.addSomeTask(subTask2);
        assertEquals("DONE",taskManager.getTaskById(1L).getStatus().toString());
    }

    @Test
    public void shouldBeStatusDoneIfListSubTasksIsInProgress() {
        EpicTask epicTask2 = new EpicTask("Void", "voiding", "NEW", 1,
                Duration.ofMinutes(60), LocalDateTime.MAX);
        SubTask subTask2 = new SubTask("Сборы", "Собрать вещи",
                "IN_PROGRESS", 0, 1,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JULY, 1, 10, 0));
        SubTask subTask3 = new SubTask("Сборы", "Собрать вещи",
                "DONE", 0, 1,
                Duration.ofMinutes(60 * 10), LocalDateTime.of(2024, Month.JULY, 1, 10, 0));
        taskManager.addSomeTask(epicTask2);
        taskManager.addSomeTask(subTask2);
        taskManager.addSomeTask(subTask3);
        assertEquals("IN_PROGRESS",taskManager.getTaskById(1L).getStatus().toString());
    }
}

