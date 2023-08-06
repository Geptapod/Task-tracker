package tracker.task;

import java.util.Objects;

public class SubTask extends Task {

    private long idEpicTask;

    public SubTask(String name, String description, String status, long id, long idEpicTask) {
        super(name, description, status, id);
        this.idEpicTask = idEpicTask;
    }

    public Long getIdEpicTask() {
        return idEpicTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask someTask = (SubTask) o;
        return Objects.equals(name,someTask.name) &&
                Objects.equals(description,someTask.description) &&
                Objects.equals(status,someTask.status) &&
                Objects.equals(id,someTask.id) &&
                Objects.equals(idEpicTask,someTask.idEpicTask);
    }

    @Override
    public String toString() {
        String result = "tracker.tasks.SubTask{" +
                "name='" + name + '\'' +
                ", idEpicTask=" + idEpicTask;
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
