# Приклади запитів — MovieLib (ПЗ 1.3)

Запуск (Java 21+): `./mvnw spring-boot:run` (порт 8080; інший порт: `-Dspring-boot.run.arguments=--server.port=8081`).
Тести: `./mvnw test`.

Стартові дані (5 фільмів, id 1–5) додає `MovieSeeder` при запуску.

## Перелік → 200
```bash
curl http://localhost:8080/api/movies
```

## Фільтр за жанром (самостійна робота) → 200
```bash
curl "http://localhost:8080/api/movies?genre=Sci-Fi"
```

## Один фільм → 200
```bash
curl http://localhost:8080/api/movies/1
```

## Неіснуючий id → 404
```bash
curl -i http://localhost:8080/api/movies/999
```

## Створити → 201
```bash
curl -i -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{ "title": "Dune", "director": "Villeneuve", "year": 2021, "genre": "Sci-Fi", "rating": 8.0, "posterUrl": "/posters/dune.jpg" }'
```

## Оновити → 200 / 404
```bash
curl -i -X PUT http://localhost:8080/api/movies/1 \
  -H "Content-Type: application/json" \
  -d '{ "title": "Inception (reissue)", "director": "Nolan", "year": 2010, "genre": "Sci-Fi", "rating": 8.8 }'

# неіснуючий → 404
curl -i -X PUT http://localhost:8080/api/movies/999 \
  -H "Content-Type: application/json" \
  -d '{ "title": "X", "director": "Y", "year": 2000, "genre": "Z", "rating": 1.0 }'
```

## Видалити → 204 / 404
```bash
curl -i -X DELETE http://localhost:8080/api/movies/2   # 204
curl -i -X DELETE http://localhost:8080/api/movies/2   # 404 (вже видалено)
```
