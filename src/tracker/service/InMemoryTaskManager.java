package tracker.service;

import tracker.task.EpicTask;
import tracker.task.Status;
import tracker.task.SubTask;
import tracker.task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    HashMap<Long, Task> tasks = new HashMap<>();
    HashMap<Long, EpicTask> epicTasks = new HashMap<>();
    HashMap<Long, SubTask> subTasks = new HashMap<>();

    InMemoryHistoryManager memoryHistoryManager = new InMemoryHistoryManager();

    long newId = 0;

    //// Генерирует уникальный id для каждой новой полученной задачи
    @Override
    public long generateNewId() {
        return ++newId;
    }

    @Override
    public void addSomeTask(Task task) {
        if (task != null) {
            if (task.getClass() == Task.class) {
                addTask(task);
            } else if (task.getClass() == EpicTask.class) {
                addEpicTask((EpicTask) task);
            } else if (task.getClass() == SubTask.class) {
                addSubTask((SubTask) task);
            }
        }
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateNewId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        epicTask.setId(generateNewId());
        epicTasks.put(epicTask.getId(), epicTask);
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

    @Override
    public List<Task> getHistory() {
        return memoryHistoryManager.getHistory();
    }

}

