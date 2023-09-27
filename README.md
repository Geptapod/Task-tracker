## Это репозиторий проекта "Трекер задач".

---
**Данное приложение было разработано для эффективного планирования своего времени**.

У задачи есть следующие свойства:
1. Название, кратко описывающее суть задачи (например, «Переезд»).
2. Описание, в котором раскрываются детали.
3. Уникальный идентификационный номер задачи, по которому её можно будет найти.
4. Статус, отображающий её прогресс. Мы будем выделять следующие этапы жизни задачи:
      NEW — задача только создана, но к её выполнению ещё не приступили.
      IN_PROGRESS — над задачей ведётся работа.
      DONE — задача выполнена.

В приложении задачи могут быть трёх типов: обычные задачи, эпики и подзадачи. 
Для каждой подзадачи известно, в рамках какого эпика она выполняется.
Каждый эпик знает, какие подзадачи в него входят.
Завершение всех подзадач эпика считается завершением эпика.

**Функционал приложения:**

1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.  
2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):  
   - Получение списка всех задач.  
   - Удаление всех задач.  
   - Получение по идентификатору.  
   - Создание. Сам объект должен передаваться в качестве параметра.  
   - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.  
   - Удаление по идентификатору.    
Дополнительные методы:    
   - Получение списка всех подзадач определённого эпика.  
   - Управление статусами осуществляется по следующему правилу:  
   - Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче.   
   - По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.  
Для эпиков:  
   - если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.  
   - если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.  
   - во всех остальных случаях статус IN_PROGRESS.  
3. Вывод истории просмотренных задач.
4. Сохранение данных менеджера в файл и восстановление из него.
