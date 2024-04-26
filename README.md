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

## Class Diagram:

![image](https://github.com/Osiris3987/bank_microservice/assets/117763292/5caf2805-f304-44bb-90dd-d6d0a6a1e575)


### Transaction API

- POST /api/v1/transactions : создание транзакции

### Client API

- GET /api/v1/client : получение всех клиентов
- DELETE /api/v1/client/{clientId} : удаление клиента по Id (изменение статуса на deleted)
- GET /api/v1/client/{clientId} : получение клиента по Id
- GET /api/v1/client/{clientId}/transactions : получение всех транзакций клиента
- GET /api/v1/client/{clientId}/legalEntities : получение всех юр лиц клиента

### Auth API

- POST /api/v1/auth/register : регистрация клиента
- POST /api/v1/auth/login : вход в аккаунт
- POST /api/v1/auth/refresh : обновление JWT токена

### Legal Entity API
- GET /api/v1/legalEntity получение всех юрлиц
- POST /api/v1/legalEntity : создание юрлица
- GET /api/v1/legalEntity/{legalEntityId} : получение юр лица по Id
- DELETE /api/v1/legalEntity/{legalEntityId} : удаление юр лица по Id (изменение статуса на deleted)
- POST /api/v1/legalEntity/assign : добавление пользователя в юр лицо
- GET /api/v1/legalEntity/{legalEntityId}/transactions : получение всех транзакций юр лица
- GET /api/v1/legalEntity/{legalEntityId}/client : получение всех клиентов юрлица

## Стек

- Java
- Spring (Boot/Security/Data)
- Maven
- PostgreSQL/Liquibase
- Gatling
- Docker
- JUnit/Mockito
- Vaadin

## Дополнительно
- Скрипт по наполнению тестовыми данными.

## Особенности
- Используем оптимистичные блокировки для сохранения консистентности данных при большом количестве параллельных
  обновлений на один и тот же ресурс. Тесты показали выдерживающую нагрузку в 410 RPS
  ![tg_image_288186762](https://github.com/Osiris3987/bank_microservice/assets/117763292/a2e3cd5c-c55a-45ce-8951-df138cdad0c3)
  запросы на обновление баланса одного юр лица используя semaphore + optimistic locks
- Документация к API. (Интерактивная и api-docs.json).
- Для приложения разработана инструкция по сборке. Сборка выполняется с помощью Docker.
- Присутствует файл .env. Представленные данные являются публичными и используются только! при тестировании.

## Ссылки (Доступны во время работы приложения)

- Интерактивная Swagger документация (http://localhost:8080/swagger-ui/index.html#/). (При необходимости замените 8080
  на ваш порт)

## Сборка
### Без Vaadin
Для сборки приложения необходимо выполнить следующие действия.

1. Склонировать репозиторий:

   ```
   git clone https://github.com/Osiris3987/hackathon-becoder
   cd hackathon-becoder
   git branch main-wo-vaadin
   ```
2. Выполнить запуск приложения следующей командой:

   ```
   docker compose up --build
   ```
### С Vaadin
Для сборки приложения необходимо выполнить следующие действия.

1. Склонировать репозиторий:

   ```
   git clone https://github.com/Osiris3987/hackathon-becoder
   cd hackathon-becoder
   ```
2. Сборка проекта
   ```
   mvn clean package
   ```
4. Запуск образа Docker с БД:
   ```
    docker-compose up -d postgres
   ```
5. Запуск приложения:
   ```
   java -jar target/hackathon_becoder_backend-0.0.1-SNAPSHOT.jar
   ```