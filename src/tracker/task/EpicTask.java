package tracker.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ArrayList;

public class EpicTask extends Task {

    private ArrayList<Long> idSubTasks;
    private LocalDateTime endTime;

    public EpicTask(String name, String description, String status, long id, Duration duration,
                    LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
        this.idSubTasks = new ArrayList<>();
    }

    public ArrayList<Long> getIdSubTasks() {
        return idSubTasks;
    }

    public void setIdSubTasks(Long idSubTask) {
        this.idSubTasks.add(idSubTask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpicTask someEpic = (EpicTask) o;
        return Objects.equals(name, someEpic.name) &&
                Objects.equals(description, someEpic.description) &&
                Objects.equals(status, someEpic.status) &&
                Objects.equals(id, someEpic.id) &&
                Objects.equals(idSubTasks, someEpic.idSubTasks);
    }

    @Override
    public String toString() {
        String result = "tracker.tasks.EpicTask{" +
                "name='" + name + '\'' +
                ", idEpicTask.size()=" + idSubTasks.size();
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
