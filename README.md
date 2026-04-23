# my-market-app
Учебный проект 6 спринта для курса мидл java-разработчик, яндекс практикум.

## Технологии

* **Java 21** — последняя LTS‑версия Java с новыми возможностями;
* **Spring Boot** — упрощение настройки и запуска Spring‑приложений;
* **Spring WebFlux** — реактивный веб‑фреймворк для построения неблокирующих REST API;
* **Spring Data R2DBC** — реактивная работа с базой данных через репозитории (вместо JPA);
* **PostgreSQL** — База данных
* **H2 Database** (в памяти) — для разработки и тестирования;
* **Docker** — контейнеризация приложения;
* **Gradle** — сборка проекта.

## Требования

- Java 21
- Gradle
- Docker
- PostgreSQL(Опционально)

## Настройка и запуск

Клонируйте репозиторий:
```bash
git clone https://github.com/lesha226/my-market-app.git
cd ./my-market-app
```

Соберите проект:
```bash
gradle clean build
```

Запуск c бд h2
```bash
java -Dspring.profiles.active=local-h2 -jar build/libs/my-market-app-0.0.1-SNAPSHOT.jar
```
Запуск c бд PostgreSQL
```bash
export SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5432/market?currentSchema=market
export SPRING_R2DBC_USERNAME=postgres
export SPRING_R2DBC_PASSWORD=password
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/market?currentSchema=market
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=password
java -Dspring.profiles.active=local-pg -jar build/libs/my-market-app-0.0.1-SNAPSHOT.jar
```
Запуск в контейнере
```bash
docker-compose up --build -d
```

Приложение будет доступно по адресу: ```http://localhost:8080```

## Web API

| Method  | URL                                   | Description                                                                    | Optional params                            |
|---------|---------------------------------------|--------------------------------------------------------------------------------|--------------------------------------------|
| `GET`   | `/items`, `/`                         | Эндпоинт получения товаров на странице                                         | `search`, `sort`, `pageNumber`, `pageSize` | 
| `POST`  | `/items?id=[id]&action=[action]`      | Эндпоинт уменьшения/увеличения количества товара в корзине со страницы товаров | `search`, `sort`, `pageNumber`, `pageSize` |  
| `GET`   | `/items/{id}`                         | Эндпоинт получения страницы с товаром                                          |                                            |
| `POST`  | `/items/{id}?action=[action]`         | Эндпоинт уменьшения/увеличения количества товара в корзине со страницы товара  |                                            |
| `GET`   | `/cart/items`                         | Эндпоинт получения страницы со списком товаров в корзине                       |                                            |
| `POST`  | `/cart/items?id=[id]&action=[action]` | Эндпоинт уменьшения/увеличения количества товара в корзине со страницы корзины |                                            |
| `GET`   | `/orders`                             | Эндпоинт получения страницы со списком заказов                                 |
| `GET`   | `/orders/{id}?newOrder=[newOrder]`    | Эндпоинт получения страницы заказа                                             |
| `POST`  | `/buy`                                | Эндпоинт совершения заказа                                                     |


## Лицензия
Это приложение распространяется под лицензией Apache License Version 2.0. Подробнее см. в файле LICENSE.

## Контакты
* **Автор:** Lesha226
* **Email:** lesha226@yandex.ru
* **GitHub:** https://github.com/lesha226