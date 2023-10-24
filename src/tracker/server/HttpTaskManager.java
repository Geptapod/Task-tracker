package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import tracker.adapters.DurationAdapter;
import tracker.adapters.LocalDateAdapter;
import tracker.client.KVTaskClient;
import tracker.service.AddTaskException;
import tracker.service.FileBackedTasksManager;
import tracker.task.EpicTask;
import tracker.task.SubTask;
import tracker.task.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskManager(String url, String fileName, boolean startFromServer) {
        super(fileName);
        this.client = startClient(url);

        if (startFromServer) {
            try {
                load();
            } catch (AddTaskException e) {
                System.out.println("При старте из сервера произошла ошибка: ");
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Метод сохранения на сервер
     */
    @Override
    public void save() {

        String gsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", gsonTasks);

        String gsonEpicTasks = gson.toJson(new ArrayList<>(epicTasks.values()));
        client.put("epictasks", gsonEpicTasks);

        String gsonSubTasks = gson.toJson(new ArrayList<>(subTasks.values()));
        client.put("subtasks", gsonSubTasks);

        String gsonHistory = gson.toJson(new ArrayList<>(getHistory()));
        client.put("history", gsonHistory);

    }


    /**
     * Метод восстановления данных из сервера
     */
    private void load() throws AddTaskException {

        Type itemsListTypeTask = new TypeToken<List<Task>>() {
        }.getType();
        Type itemsListTypeEpic = new TypeToken<List<EpicTask>>() {
        }.getType();
        Type itemsListTypeSub = new TypeToken<List<SubTask>>() {
        }.getType();

        long maxId = 0;

        List<Task> tasksFromServer = new ArrayList<>();

        String tasksGson = client.load("tasks");
        if (!tasksGson.isBlank()) {
            tasksFromServer.addAll(gson.fromJson(tasksGson, itemsListTypeTask));
        }

        String epicTasksGson = client.load("epictasks");
        String subTasksGson = client.load("subtasks");
        //если не вернулись эпики или сабы, то не добавляем ничего, так как они связаны
        if (!epicTasksGson.isBlank() && !subTasksGson.isBlank()) {
            tasksFromServer.addAll(gson.fromJson(epicTasksGson, itemsListTypeEpic));
            tasksFromServer.addAll(gson.fromJson(subTasksGson, itemsListTypeSub));
        }


        for (Task task : tasksFromServer) {

            if (task.getClass() == Task.class) {
                tasks.put(task.getId(), task);
            } else if (task.getClass() == EpicTask.class) {
                epicTasks.put(task.getId(), (EpicTask) task);
            } else if (task.getClass() == SubTask.class) {
                subTasks.put(task.getId(), (SubTask) task);
            }

            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }

        newId = maxId; //устанавливаю максимальное значение, с коготорого дальше пойдёт присвоение

        List<Task> historyFromServer = new ArrayList<>();

        String historyGson = client.load("history");
        if (!historyGson.isBlank()) {
            historyFromServer.addAll(gson.fromJson(historyGson, itemsListTypeTask));
        }

        //заполняю историю
        for (Task task : historyFromServer) {
            getTaskById(task.getId());
        }

    }

    /**
     * Методо регистраций на клиенте и сервере
     */
    private KVTaskClient startClient(String url) {
        return new KVTaskClient(url);
    }

    public void registerOnServer() {
        client.register();
    }


}
