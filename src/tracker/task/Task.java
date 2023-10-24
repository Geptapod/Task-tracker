package tracker.task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected long id;
    protected Duration duration;
    protected LocalDateTime startTime;


    public Task(String name, String description, String status, long id, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task someTask = (Task) o;
        return Objects.equals(name, someTask.name) &&
                Objects.equals(description, someTask.description) &&
                Objects.equals(status, someTask.status) &&
                Objects.equals(id, someTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        String result = "tracker.tasks.Task{" +
                "name='" + name + '\'';
        if (description != null) {
            result = result + ", description.length()='" + description.length() + '\'';
        } else {
            result = result + ", description=null'" + '\'';
        }

        result = result +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
        return result;
    }

}
