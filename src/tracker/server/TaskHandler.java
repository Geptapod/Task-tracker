package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.adapters.DurationAdapter;
import tracker.adapters.LocalDateAdapter;
import tracker.service.AddTaskException;
import tracker.service.TaskManager;
import tracker.task.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskHandler implements HttpHandler {

    TaskManager manager;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMethod = exchange.getRequestMethod();
        String response = "";
        String query = exchange.getRequestURI().getQuery();
        String path = exchange.getRequestURI().getPath();

        try (exchange) {
            switch (requestMethod) {
                case "GET":
                    response = executeMethodGet(path, query);
                    if (response.isBlank()) {
                        exchange.sendResponseHeaders(204, 0);
                    } else {
                        exchange.sendResponseHeaders(200, 0);
                    }
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        response = executeMethodPost(path, body);
                    } catch (AddTaskException e) {
                        response = e.getMessage();
                    }
                    if ("Задача успешно добавлена.".equals(response) || "Задача успешно обновлена".equals(response)) {
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                case "DELETE":
                    response = executeMethodDelete(path, query);
                    if (response.isBlank()) {
                        exchange.sendResponseHeaders(204, 0);
                    } else {
                        exchange.sendResponseHeaders(200, 0);
                    }
                default:
                    exchange.sendResponseHeaders(404, 0);
                    response = "Такой запрос не обрабатывается.";
            }
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        } finally {
            exchange.close();
        }
    }

    /**
     * Метод обработки GET запроса
     */
    private String executeMethodGet(String path, String query) {
        String response = "";

        String[] splitPath = path.split("/");
        String typeTask = splitPath[2];

        if (query == null) {
            switch (typeTask) {
                case "task":
                    List<Task> taskList = manager.getTasksList();
                    if (!taskList.isEmpty()) {
                        response = gson.toJson(taskList);
                    }
                    break;
                case "epic":
                    List<EpicTask> epicList = manager.getEpicTasksList();
                    if (!epicList.isEmpty()) {
                        response = gson.toJson(epicList);
                    }
                    break;
            }
        } else {
            long idTaskForQuery = getIdFromQuery(query);

            if (idTaskForQuery > 0) {
                Task task = (Task) manager.getTaskById(idTaskForQuery);
                if (task != null) {
                    switch (typeTask) {
                        case "epic":
                            if (task instanceof EpicTask) {
                                response = gson.toJson(task);
                            }
                            break;
                        case "subtask":
                            //получение всех Subtask для переданного id EpicTask
                            if (splitPath.length == 4 && splitPath[splitPath.length - 1].equals("epic")) {
                                List<SubTask> listSubTask = manager.getSubTaskForEpic(idTaskForQuery);
                                if (listSubTask!=null) {
                                    response = gson.toJson(listSubTask);
                                }

                            }

                            //получение Subtask по id
                            if (task instanceof SubTask) {
                                response = gson.toJson(task);
                            }
                            break;
                    }
                }
            }
        }

        return response;
    }

    /**
     * Метод обработки POST запроса
     */
    private String executeMethodPost(String path, String body) throws AddTaskException {
        String response = "";

        String[] splitPath = path.split("/");
        String typeTask = splitPath[2];

        Optional<Task> task = taskFromGson(body, typeTask);

        if (task.isPresent()) {
            if (checkBodyTask(task.get())) {
                if (task.get().getId() == 0) {
                    manager.addSomeTask(task.get());
                    response = "Задача успешно добавлена.";
                } else {
                    manager.updateTask(task.get());
                    response = "Задача успешно обновлена.";
                }
            } else {
                response = "Тело запроса для " + typeTask + " сформировано неверно.";
            }
        } else {
            response = "Что - то пошло не так. Проверте правильность запроса и тело запроса";
        }

        return response;
    }

    /**
     * Метод обработки DELETE запроса
     */
    private String executeMethodDelete(String path, String query) {

        String response = "";

        String[] elementsPath = path.split("/");
        String typeTask = elementsPath[2];

        if (query == null) {

            switch (typeTask) {
                case "task":
                    response = "Все задачи типа Task удалены";
                    manager.deleteTasks();
                    break;
                case "epic":
                    response = "Все задачи типа EpicTask и SubTask удалены";
                    manager.deleteEpicTasks();
                    break;
                case "subtask":
                    //удаление всех сабов не предусмотрено, только по id
                    break;
            }

        } else {

            long idTaskForQuery = getIdFromQuery(query);

            if (idTaskForQuery > 0) {
                Task taskForDelete = (Task) manager.getTaskById(idTaskForQuery);
                if (taskForDelete != null) {

                    switch (typeTask) {
                        case "epic":
                            if (taskForDelete instanceof EpicTask) {
                                manager.deleteTaskById(taskForDelete.getId());
                                response = "Задача типа EpicTask, с ID - " + idTaskForQuery + " удалена.";
                            }
                            break;
                        case "subtask":
                            if (taskForDelete instanceof SubTask) {
                                manager.deleteTaskById(taskForDelete.getId());
                                response = "Задача типа SubTask, с ID - " + idTaskForQuery + " удалена.";
                            }
                            break;
                        case "task":
                            if (taskForDelete instanceof Task) {
                                manager.deleteTaskById(taskForDelete.getId());
                                response = "Задача типа Task, с ID - " + idTaskForQuery + " удалена.";
                            }
                            break;
                    }

                }
            }

        }

        return response;
    }

    /**
     * Метод получения id из URI
     */
    private long getIdFromQuery(String str) {
        String[] elementsStr = str.split("&");
        int idTask = 0;

        if (elementsStr.length > 0) {
            String[] elementsId = elementsStr[0].split("=");
            if (elementsId.length == 2) {
                if (elementsId[0].equals("id")) {
                    idTask = Integer.parseInt(elementsId[1]); // если передано что - то вместо числа, вернёт 400 ошибку
                }
            }
        }
        return idTask;
    }

    /**
     * Метод проверки заполненности полей сформированной задачи
     */
    private boolean checkBodyTask(Task task) {

        Status status = task.getStatus();
        LocalDateTime date = task.getStartTime();
        Duration duration = task.getDuration();

        if (status == null || date == null || duration == null) {
            return false;
        }

        if (task instanceof EpicTask) {
            List<Long> idSubTasks = ((EpicTask) task).getIdSubTasks();
            return idSubTasks != null;
        }

        if (task instanceof SubTask) {
            Long idEpicTask = ((SubTask) task).getIdEpicTask();
            return idEpicTask != null;
        }

        return true;
    }

    private Optional<Task> taskFromGson(String taskGson, String typeTask) {

        return switch (typeTask) {
            case "task" -> Optional.of(gson.fromJson(taskGson, Task.class));
            case "epic" -> Optional.of(gson.fromJson(taskGson, EpicTask.class));
            case "subtask" -> Optional.of(gson.fromJson(taskGson, SubTask.class));
            default -> Optional.empty();
        };
    }
}
