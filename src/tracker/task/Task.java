package tracker.task;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected long id;

    public Task(String name, String description, String status, long id) {
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.id = id;
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

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task someTask = (Task) o;
        return Objects.equals(name,someTask.name) &&
                Objects.equals(description,someTask.description) &&
                Objects.equals(status,someTask.status) &&
                Objects.equals(id,someTask.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        String result = "tracker.tasks.Task{" +
                "name='" + name + '\'';
        if (description!=null) {
            result = result + ", description.length()='" + description.length() + '\'';
        } else {
            result = result + ", description=null'" + '\'';
        }

        result = result +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
        return result;
    }
}
