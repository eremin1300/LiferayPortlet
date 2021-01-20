# LiferayPortlet

Приложение соответствует заданию: 

Напишите приложение (портлет) для портала на платформе Liferay 6.2, собирающий
вакансии с портала HH.RU в г. Новосибирск, проф. Область «Информационные
технологии, интернет, телеком», сохраняющий их в базу данных и предоставляющий
возможности по просмотру и поиску.

Что реализовано: 

с HH.ru приложение собирает 100 вакансий (есть возможность расширить до 2000, необходимо раскомментировать 2 строчки в коде, но тогда портлет вешается на долгое время - долго пишется БД)
Реализован постраничный вывод вакансий
Реализован поиск

Скриншоты и готовый WAR-файл тоже залиты. 
Подключение к БД реализовано через JDBC, база у меня была со следующими свойствами подключения: 

        jdbc:postgresql://localhost/lportal
        Пользователь: postgres
        Пароль: 123
        
Данные опции меняются в классе DBService в методе getDBConnection(). Можно прикрутить чтение параметров из файла или из формы. 

Вывод файла для логгирования настроен в папку C:\liferay\  название файла log + текущая дата
