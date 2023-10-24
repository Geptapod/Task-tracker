package tracker.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("yyyy.MM.dd G 'at' HH:mm:ss");
    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("yyyy.MM.dd G 'at' HH:mm:ss");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDate) throws IOException {
        jsonWriter.value(localDate.format(formatterWriter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
    }
}
