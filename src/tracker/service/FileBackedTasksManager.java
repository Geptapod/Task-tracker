package tracker.service;

import tracker.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(String file) {
        this.file = Path.of(file).toFile();
    }

    //static private final TaskManager manager = Managers.getDefault();
    /*static private final InMemoryHistoryManager memoryHistoryManager = new InMemoryHistoryManager();*/

    public static void main(String[] args) {

        FileBackedTasksManager backed = new FileBackedTasksManager("save.txt");
        Task task1 = new Task("Запустить приложение", "Run tracker", "NEW", 0);
        Task task2 = new Task("Переезд", "Собрать коробки",
                "NEW", 0);
        EpicTask epicTask = new EpicTask("Поездка", "Поездка на дачу",
                "NEW", 0);
        SubTask subTask1 = new SubTask("Уборка", "Сделать уборку перед отъездом",
                "NEW", 0, 3);
        SubTask subTask2 = new SubTask("Сборы", "Собрать вещи",
                "NEW", 0, 3);
        SubTask subTask3 = new SubTask("Билеты", "Купить билеты",
                "NEW", 0, 3);
        EpicTask epicTask2 = new EpicTask("Void", "voiding",
                "NEW", 1);

        //проверка наполнения задачами
        backed.addSomeTask(task1);
        backed.addSomeTask(task2);
        backed.addSomeTask(epicTask);
        backed.addSomeTask(subTask1);
        backed.addSomeTask(subTask2);
        backed.addSomeTask(subTask3);
        backed.addSomeTask(epicTask2);


        // Проверка вывода истории
        backed.getTaskById(4L);
        backed.getTaskById(5L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(3L);
        backed.getTaskById(4L);
        backed.getTaskById(7L);
    }

    //будет сохранять текущее состояние менеджера в указанный файл
    private void save() {
        try (BufferedWriter fileOutputStream = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String header = "id,type,name,status,description,epic";
            fileOutputStream.write(header);
            fileOutputStream.newLine();
            if (!tasks.isEmpty()) {
                for (Task task : tasks.values()) {
                    fileOutputStream.write(taskToString(task));
                    fileOutputStream.newLine();
                }
            }
            if (!epicTasks.isEmpty()) {
                for (EpicTask task : epicTasks.values()) {
                    fileOutputStream.write(taskToString(task));
                    fileOutputStream.newLine();
                    if (!task.getIdSubTasks().isEmpty()) {
                        for (Long sub : task.getIdSubTasks()) {
                            fileOutputStream.write(taskToString(subTasks.get(sub)));
                            fileOutputStream.newLine();
                        }
                    }
                }
            }
            fileOutputStream.newLine();
            fileOutputStream.write(historyToString(memoryHistoryManager));
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> tasks = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                tasks.add(fileReader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Integer> history = historyFromString(tasks.get(tasks.size() - 1));
        tasks.remove(0);
        tasks.remove(tasks.size() - 1);
        tasks.remove(tasks.size() - 1);
        FileBackedTasksManager newBackend = new FileBackedTasksManager("saveFromFile");
        for (String task : tasks) {
            newBackend.addSomeTask(fromString(task));
        }
        for (int view : history) {//заполнение InMemoryHistoryManager
            for (Task task : newBackend.tasks.values()) {
                if (task.getId() == view) {
                    newBackend.memoryHistoryManager.add(task);
                }
            }
            for (Task task : newBackend.epicTasks.values()) {
                if (task.getId() == view) {
                    newBackend.memoryHistoryManager.add(task);
                }
            }
            for (Task task : newBackend.subTasks.values()) {
                if (task.getId() == view) {
                    newBackend.memoryHistoryManager.add(task);
                }
            }
        }

        return newBackend;
    }

    //метод создания задачи из строки
    static private Task fromString(String value) {
        String[] str = value.split(",");
        switch (TypesOfTask.valueOf(str[1])) {
            case TASK:
                return new Task(str[2], str[4], str[3], Long.parseLong(str[0]));
            case SUBTASK:
                return new SubTask(str[2], str[4], str[3], Long.parseLong(str[0]), Long.parseLong(str[5]));
            case EPIC:
                return new EpicTask(str[2], str[4], str[3], Long.parseLong(str[0]));
            default:
        }

        return null;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        List<Task> viewHistory = manager.getHistory();
        for (Task task : viewHistory) {
            if (task != viewHistory.get(viewHistory.size() - 1)) {
                builder.append(task.getId() + ",");
            } else {
                builder.append(task.getId());
            }
        }
        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        String[] strHistory = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String str : strHistory) {
            history.add(Integer.parseInt(str));
        }
        return history;
    }

    private String taskToString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId() + ",");
        if (task.getClass() == Task.class) {
            builder.append("TASK,");
        } else if (task instanceof SubTask) {
            builder.append("SUBTASK,");
        } else {
            builder.append("EPIC,");
        }
        builder.append(task.getName() + ",");
        builder.append(task.getStatus() + ",");
        builder.append(task.getDescription());
        if (task instanceof SubTask) {
            builder.append("," + ((SubTask) task).getIdEpicTask().toString());
        }
        return builder.toString();
    }

    @Override
    public void addSomeTask(Task task) {
        super.addSomeTask(task);
        save();
    }

    @Override
    public void getTasks() {
        super.getTasks();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpicTasks() {
        super.deleteEpicTasks();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(Long idTask) {
        super.deleteTaskById(idTask);
        save();
    }

    //Методы для управления статусами
    //Для смены статуса будет передаваться новый объект с новым статусом
    @Override
    public void changeStatusTask(Task task) {
        super.changeStatusTask(task);
        save();
    }

    @Override
    public void changeStatusSubTask(SubTask subTask) {
        super.changeStatusSubTask(subTask);
        save();
    }

    //Метод для обновления статуса у эпик задачи
    @Override
    public void updateStatusEpicTask(EpicTask epicTask) {
        super.updateStatusEpicTask(epicTask);
        save();
    }

    @Override
    public Task getTaskById(Long idTask) {
        super.getTaskById(idTask);
        save();

        return null;
    }

    @Override
    public String toString() {
        return "FileBackedTasksManager{" +
                "tasks=" + tasks +
                ", \nepicTasks=" + epicTasks +
                ", \nsubTasks=" + subTasks +
                ", \nmemoryHistoryManager=" + memoryHistoryManager.getHistory() +
                '}';
    }
}

    /*@Override
    public void addTask(Task task) {
        super.addTask(task);
        save(subTask);
    }

    public Task fromString(String value) {

    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save(subTask);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save(subTask);
    }*/


