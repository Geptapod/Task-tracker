package tracker.service;

import tracker.task.EpicTask;
import tracker.task.Status;
import tracker.task.SubTask;
import tracker.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Long, Task> tasks = new HashMap<>();
    protected HashMap<Long, EpicTask> epicTasks = new HashMap<>();
    protected HashMap<Long, SubTask> subTasks = new HashMap<>();

    protected InMemoryHistoryManager memoryHistoryManager = new InMemoryHistoryManager();

    protected long newId = 0;

    //// Генерирует уникальный id для каждой новой полученной задачи
    @Override
    public long generateNewId() {
        return ++newId;
    }

    /**
     * Выполняется проверка типа добавляемой задачи и возможное пересечение по времени
     */
    @Override
    public void addSomeTask(Task task) throws AddTaskException {
        if (task != null) {
            if (task instanceof EpicTask) {
                addEpicTask((EpicTask) task);
            } else if (task instanceof SubTask) {
                if (checkIntersectionsTime(task)) {
                    addSubTask((SubTask) task);
                }
            } else {
                if (checkIntersectionsTime(task)) {
                    addTask(task);
                }
            }
        } else {
            throw new AddTaskException("В метод добавления задач попал null");
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateNewId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        if (!epicTasks.containsKey(epicTask.getId())) {
            epicTask.setId(generateNewId());
            epicTasks.put(epicTask.getId(), epicTask);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(generateNewId());
        Long keyForEpicTasks = subTask.getIdEpicTask(); // получаем id Epic
        EpicTask epicTask = epicTasks.get(keyForEpicTasks);
        epicTask.setIdSubTasks(subTask.getId());
        subTasks.put(subTask.getId(), subTask);

        //После добавления новой саб задачи к эпику обновляем статус
        updateStatusEpicTask(epicTask);
        //Обновление StartTime и Duration
        updateStartTimeEpicTask(epicTask);
        updateDurationEpicTask(epicTask);
        epicTask.setEndTime(epicTask.getEndTime());
    }

    @Override
    public void getTasks() {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                System.out.println(task + "; ");
            }
        } else {
            System.out.println("Список задач пуст!");
        }
    }

    @Override
    public void getEpicTasks() {
        if (!epicTasks.isEmpty()) {
            for (EpicTask task : epicTasks.values()) {
                System.out.println(task + "; ");
            }
        } else {
            System.out.println("Список эпиков пуст!");
        }
    }

    @Override
    public void getSubTasks() {
        if (!subTasks.isEmpty()) {
            for (SubTask task : subTasks.values()) {
                System.out.println(task + "; ");
            }
        } else {
            System.out.println("Список подзадач пуст!");
        }
    }

    @Override
    public Task getTaskById(Long idTask) {
        if (tasks.containsKey(idTask)) {
            memoryHistoryManager.add(tasks.get(idTask));
            return tasks.get(idTask);
        } else if (epicTasks.containsKey(idTask)) {
            memoryHistoryManager.add(epicTasks.get(idTask));
            return epicTasks.get(idTask);
        } else if (subTasks.containsKey(idTask)) {
            memoryHistoryManager.add(subTasks.get(idTask));
            return subTasks.get(idTask);
        }
        return null;
    }

    //Методы удаления задач
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpicTasks() {
        epicTasks.clear();
        subTasks.clear(); //Сабы тоже очистим, не логино чтобы сабы жили без своих эпиков
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteTaskById(Long idTask) {
        if (idTask != null) {
            if (tasks.containsKey(idTask)) {
                memoryHistoryManager.remove(idTask);
                tasks.remove(idTask);
            } else if (epicTasks.containsKey(idTask)) {
                //Если удаляем эпик задачу, нужно сначала удалить все её сабы
                EpicTask epicTask = epicTasks.get(idTask);

                for (Long idSubTask : epicTask.getIdSubTasks()) {
                    if (subTasks.containsKey(idSubTask)) {
                        memoryHistoryManager.remove(idSubTask);
                        subTasks.remove(idSubTask);
                    }
                }
                memoryHistoryManager.remove(idTask);
                epicTasks.remove(idTask);
            } else if (subTasks.containsKey(idTask)) {
                //У эпика этой subTask нужно из ArrayList удалить id этой subTask
                long idEpicTask = subTasks.get(idTask).getIdEpicTask();

                if (epicTasks.containsKey(idEpicTask)) {
                    ArrayList<Long> listIdSubTaskOnEpic = epicTasks.get(idEpicTask).getIdSubTasks();
                    if (listIdSubTaskOnEpic.contains(idTask)) {
                        memoryHistoryManager.remove(idTask);
                        listIdSubTaskOnEpic.remove(idTask);
                    }
                }
                memoryHistoryManager.remove(idTask);
                subTasks.remove(idTask);
                updateStatusEpicTask(epicTasks.get(idEpicTask)); //После удаления саба нужно обновить статус эпика
                updateStartTimeEpicTask(epicTasks.get(idEpicTask));
                updateDurationEpicTask(epicTasks.get(idEpicTask));
            }
        }
    }

    //Методы для управления статусами
    //Для смены статуса будет передаваться новый объект с новым статусом
    @Override
    public void changeStatusTask(Task task) {
        if (task != null) {
            if (tasks.containsKey(task.getId())) {
                tasks.get(task.getId()).setStatus(task.getStatus());
            }
        }
    }

    @Override
    public void changeStatusSubTask(SubTask subTask) {
        if (subTask != null) {
            if (subTasks.containsKey(subTask.getId())) {
                subTasks.get(subTask.getId()).setStatus(subTask.getStatus());
            }

            //После смены статуса сабы, обновим статус её эпика
            if (epicTasks.containsKey(subTask.getIdEpicTask())) {
                updateStatusEpicTask(epicTasks.get(subTask.getIdEpicTask()));
            }
        }
    }

    //Метод для обновления статуса у эпик задачи
    @Override
    public void updateStatusEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            if (epicTasks.containsKey(epicTask.getId())) {
                ArrayList<SubTask> listSubTasks = new ArrayList<>();

                for (Long idSubTask : epicTask.getIdSubTasks()) {
                    if (subTasks.containsKey(idSubTask)) {
                        listSubTasks.add(subTasks.get(idSubTask));
                    }
                }

                if (listSubTasks.size() == 0) {
                    epicTask.setStatus(Status.NEW);
                } else {
                    int amountNew = 0;
                    int amountInProgress = 0;
                    int amountDone = 0;

                    for (SubTask task : listSubTasks) {
                        if (Status.NEW.equals(task.getStatus())) {
                            amountNew++;
                        } else if (task.getStatus().equals(Status.IN_PROGRESS)) {
                            amountInProgress++;
                        } else if (task.getStatus().equals(Status.DONE)) {
                            amountDone++;
                        }
                    }

                    if (amountInProgress > 0) {
                        epicTask.setStatus(Status.IN_PROGRESS);
                    } else {
                        if (listSubTasks.size() == amountNew) {
                            epicTask.setStatus(Status.NEW);
                        } else {
                            if (listSubTasks.size() == amountDone) {
                                epicTask.setStatus(Status.DONE);
                            } else {
                                epicTask.setStatus(Status.IN_PROGRESS);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Метод обновления задачи
     */
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            if (task.getClass() == Task.class) {
                if (tasks.containsKey(task.getId())) {
                    if (checkIntersectionsTime(task)) { //проверяет насложение времени, если наложение не возникло вернёт true
                        tasks.put(task.getId(), task);
                    }
                }
            } else if (task.getClass() == EpicTask.class) {
                if (epicTasks.containsKey(task.getId())) {
                    epicTasks.put(task.getId(), (EpicTask) task);
                }
            } else if (task.getClass() == SubTask.class) {
                if (subTasks.containsKey(task.getId())) {
                    if (checkIntersectionsTime(task)) { //проверяет насложение времени, если наложение не возникло вернёт true
                        subTasks.put(task.getId(), (SubTask) task);
                        updateStatusEpicTask(epicTasks.get(((SubTask) task).getIdEpicTask()));
                        updateStartTimeEpicTask(epicTasks.get(((SubTask) task).getIdEpicTask()));
                        updateDurationEpicTask(epicTasks.get(((SubTask) task).getIdEpicTask()));
                    }
                }
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return memoryHistoryManager.getHistory();
    }

    private void updateDurationEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            List<Long> listIdSubTasks = epicTask.getIdSubTasks();
            if (!listIdSubTasks.isEmpty()) {
                Duration duration = listIdSubTasks.stream()
                        .map(id -> subTasks.get(id).getDuration())
                        .reduce(Duration.ZERO, Duration::plus);
                epicTask.setDuration(duration);
            }

        }
    }

    private void updateStartTimeEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            List<Long> listIdSubTasks = epicTask.getIdSubTasks();
            if (!listIdSubTasks.isEmpty()) {
                LocalDateTime minDateTime = LocalDateTime.MAX;
                for (Long id : listIdSubTasks) {
                    if (subTasks.containsKey(id) && subTasks.get(id).getStartTime().isBefore(minDateTime)) {
                        minDateTime = subTasks.get(id).getStartTime();
                    }
                }
                epicTask.setStartTime(minDateTime);
            }
        }
    }

    /**
     * Метод получения списка задач с приоритетом по полю starTime
     */
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (int) (o1.getStartTime().toEpochSecond(ZoneOffset.UTC) -
                        o2.getStartTime().toEpochSecond(ZoneOffset.UTC));
            }
        });
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());
        return prioritizedTasks;
    }

    /**
     * Проверка пересечения дат
     */
    public boolean checkIntersectionsTime(Task task) {
        boolean allowedToAddTask = true;
        LocalDateTime startTask = task.getStartTime();
        LocalDateTime endTask = task.getEndTime();

        if (task != null && !getPrioritizedTasks().isEmpty()) {
            for (Task taskFromSet : getPrioritizedTasks()) {
                LocalDateTime startTaskFromSet = taskFromSet.getStartTime();
                LocalDateTime endTaskFromSet = taskFromSet.getEndTime();
                if (startTask.isEqual(startTaskFromSet)//начало совпадает
                        || (startTask.isBefore(startTaskFromSet) && endTask.isAfter(startTaskFromSet))//началась раньше но заканчивается раньше
                        || (startTask.isAfter(startTaskFromSet) && endTask.isBefore(endTaskFromSet)) //началась позже но заканчивается раньше
                        || endTask.isEqual(endTaskFromSet)
                ) {
                    allowedToAddTask = false;
                }
            }
        }
        return allowedToAddTask;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getEpicTasksList() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getSubTaskForEpic(Long idEpicTask) {

        List<SubTask> list = new ArrayList<>();

        if (epicTasks.containsKey(idEpicTask)) {
            EpicTask epicTask = epicTasks.get(idEpicTask);
            for (Long id : epicTask.getIdSubTasks()) {
                list.add(subTasks.get(id));
            }
        }
        return list;
    }
}

