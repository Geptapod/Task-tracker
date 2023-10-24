package tracker.server;

import com.sun.net.httpserver.HttpServer;
import tracker.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks/task", new TaskHandler(manager));
        httpServer.createContext("/tasks/epic", new TaskHandler(manager));
        httpServer.createContext("/tasks/subtask", new TaskHandler(manager));
        httpServer.createContext("/tasks", new PrioritizedAndHistoryHandler(manager));
        httpServer.createContext("/tasks/history", new PrioritizedAndHistoryHandler(manager));
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Сервер на порту " + PORT + " остановлен");
        httpServer.stop(3);
    }
}
