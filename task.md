В нашем приложении нужно использовать один объект SessionFactory. Его мы загрузим через Spring Context.
Класс Main.



    @Bean(destroyMethod = "close")

    public SessionFactory sf() {

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()

                .configure().build();

        return new MetadataSources(registry).buildMetadata().buildSessionFactory();

    }

В классах персистенции используем ссылку через конструктор.



    package ru.job4j.todo.store;

    import lombok.AllArgsConstructor;
    import org.hibernate.SessionFactory;
    import org.springframework.stereotype.Repository;

    @Repository
    @AllArgsConstructor
    public class TaskStore {
        private final SessionFactory sf;
    }

1. Cхема таблицы Task с полями id, description, created, done. Расположение /db/
 Скрипт загружать через liquibase.


    CREATE TABLE tasks (
        id SERIAL PRIMARY KEY,
        description TEXT,
        created TIMESTAMP,
        done BOOLEAN
    );


2. Виды.
    - Страница со списком всех заданий. В таблице отображаем имя, дату создания и состояние (выполнено или нет)
    - На странице со списком добавить кнопку "Добавить задание". 
    - На странице со списком добавьте три ссылки: "Все", "Выполненные", "Новые". При переходе по ссылкам в таблице нужно отображать: все задания, только выполненные или только новые.
    - При клике на задание переходим на страницу с подробным описанием задания.
    - На странице с подробным описанием добавить кнопки: "Выполнено", "Редактировать", "Удалить".
    - Если нажали на кнопку выполнить, то задание переводится в состояние выполнено.
    - Кнопка "Редактировать" переводит пользователя на отдельную страницу для редактирования.
    - Кнопка "Удалить" удаляет задание и переходит на список всех заданий.

3. Приложение должно иметь три слоя: Контроллеры, Сервисы, Персистенции.
    - Объект SessionFactory создается один раз в классе Main с аннотацией @Bean. По аналогии с loadDabaseSource в проекте "Работа мечты".
    - Объект TaskStore принимает параметр SessionFactory через конструктор.

4. Заполните README. Добавьте скриншоты запущенного приложения в Readme.md. 


5. Примеры похожих проектов 

https://github.com/anton415/job4j_dreamjob

https://github.com/anton415/job4j_cinema

https://github.com/anton415/job4j_cars

https://github.com/anton415/job4j_todo