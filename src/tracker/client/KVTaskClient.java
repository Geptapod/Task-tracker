package tracker.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final HttpClient client;
    private final String keyAPI;


    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        this.keyAPI = register();
    }

    public String register() {
        URI uri = URI.create(url + "register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения регистрации возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return "registerFail";
    }

    public void put(String key, String json) {

        String query = "save/" + key + "?API_KEY=" + keyAPI;
        URI uri = URI.create(url + query);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения регистрации возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        String query = "load/" + key + "?API_KEY=" + keyAPI;
        URI uri = URI.create(url + query);

        String value = "loadFail";

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            value = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения регистрации возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return value;
    }

}
