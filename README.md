# Hackathon \</beCoder> 2024
### Команда : "Наш слон"
#### Участники:
1. Ломакин Олег (Капитан)
2. Карабанов Андрей
3. Лисейчиков Глеб
4. Афанасьев Кирилл
5. Мальков Павел

## Тема хакатона
Решение прикладных бизнес-задач на java для клиентов из нефтегазовой и финансовой отрасли.

## Задание 
Необходимо реализовать микросервис с собственной базой для контроля баланса на счету юридического лица.
Данным счетом пользуются много независимых внешних клиентов. По этой причине изменение баланса счета может 
происходить одновременно по нескольким независимым запросам, как в сторону увеличения, так и в сторону уменьшения.

1) Списания по балансу должно происходить в момент покупки, без задержек для пользователя;
2) Значение баланса может увеличиваться и уменьшаться в один момент в несколько потоков, поэтому решение должно
предусматривать корректность проведения операций с балансом в многопоточной среде;
3) Значение баланса не может стать отрицательным.

### Transaction API

- Создание транзакции

## Стек

- Java
- Spring Boot
- Maven
- PostgreSQL

## Дополнительно

- Скрипт по наполнению тестовыми данными.

## Особенности

- Документация к API.(Интерактивная и api-example.yaml)
- Для приложения разработана инструкция по сборке. Сборка выполняется с помощью Docker.
- Присутствует файл .env. Представленные данные являются публичными и используются только! при тестировании.

## Ссылки (Доступны во время работы приложения)

- Интерактивная Swagger документация (http://localhost:8080/swagger-ui/index.html#/). (При необходимости замените 8080 
на ваш порт)

## Сборка

Для сборки приложения необходимо выполнить следующие действия.

1. Склонировать репозиторий:

   ```
   git clone https://github.com/Osiris3987/hackathon-becoder
   cd hackathon-becoder
   ```
2. Выполнить запуск приложения следующей командой:

   ```
   docker compose up --build
   ```
