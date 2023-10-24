package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import tracker.adapters.DurationAdapter;
import tracker.adapters.LocalDateAdapter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import tracker.task.*;

public class PrioritizedAndHistoryHandler implements HttpHandler {
    TaskManager manager;

    public PrioritizedAndHistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;

        String[] path = exchange.getRequestURI().getPath().split("/");
        String lastElementPath = path[path.length - 1];
        try (exchange) {
            if ("GET".equals(exchange.getRequestMethod())) {

                if (lastElementPath.equals("tasks")) {
                    TreeSet<Task> prioritized = manager.getPrioritizedTasks();
                    response = toGsonObject(prioritized);
                } else if (lastElementPath.equals("history")) {
                    List<Task> history = manager.getHistory();
                    response = toGsonObject(history);
                } else {
                    response = "Вероятно введён неверный путь.";
                }

                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseHeaders().add("Content-Type", "application/json");

            } else {
                exchange.sendResponseHeaders(404, 0);
                response = "Такой запрос не обрабатывается.";
            }

            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        } finally {
            exchange.close();
        }
    }

    private String toGsonObject(Object o) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        return gson.toJson(o);

    }
}
