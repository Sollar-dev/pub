Backend модуль для телеграмм бота, android приложения.

Данный модуль написанный на java с использованием библиотек: apache POI, jdbc принимает на вход .xlsx файл и выгружает расписание в базу даныых.

Функциональность:

apache POI читает расписание, встроенным функционалом языка java с помощью регулярных выражений разбивает ячейку с данными на необходимые атрибуты для выгрузки в базу данных

База данных представлена файлом .db для написания запросов создания таблиц, вставки значений используется SQLite, что позволяет сохранить малый размер модуля, 
сохраняя необходимую функциональность.
