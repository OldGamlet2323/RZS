# Перевірка exceptions — MovieLib (ПЗ 1.5)

Мета: з'ясувати, чи працює глобальна обробка помилок (`GlobalExceptionHandler`):
кожна помилка має повертати правильний HTTP-статус і **єдиний формат** тіла:
---

## 1. Контроль: помилок немає (очікуємо 200)

```bash
curl -i http://localhost:8080/api/movies/1
```
Очікується: `HTTP/1.1 200`, JSON фільму.

## 2. 404 — `MovieNotFoundException`

| # | Запит | Очікується |
|---|---|---|
| 2.1 | `curl -i http://localhost:8080/api/movies/999` | 404 |
| 2.2 | `curl -i -X PUT http://localhost:8080/api/movies/999 -H "Content-Type: application/json" -d '{"title":"X","director":"Y","year":2000,"genre":"Z","rating":5}'` | 404 |
| 2.3 | `curl -i -X DELETE http://localhost:8080/api/movies/999` | 404 |

Тіло для 2.1–2.3:
```json
{"status":404,"error":"Not Found","message":"Фільм з id=999 не знайдено","details":[]}
```

Видалення двічі (другий раз — 404):
```bash
curl -i -X DELETE http://localhost:8080/api/movies/2   # 204
curl -i -X DELETE http://localhost:8080/api/movies/2   # 404
```

## 3. 400 — `MethodArgumentNotValidException` (валідація `@Valid`)

**3.1 Порожня назва**
```bash
curl -i -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{ "title": "", "director": "Nolan", "year": 2010, "genre": "Sci-Fi", "rating": 8.8 }'
```
```json
{"status":400,"error":"Bad Request","message":"Помилка валідації","details":["title: назва обов'язкова"]}
```

**3.2 Кілька помилок одразу** (рік < 0, рейтинг > 10, порожній режисер)
```bash
curl -i -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{ "title": "X", "director": "", "year": -5, "genre": "G", "rating": 11.5 }'
```
Очікується 400 і в `details` — 3 рядки (`director`, `rating`, `year`).

**3.3 Рейтинг менший за 0**
```bash
curl -i -X POST http://localhost:8080/api/movies \
  -H "Content-Type: application/json" \
  -d '{ "title": "X", "director": "Y", "year": 2000, "genre": "G", "rating": -1 }'
```
Очікується 400, `details`: `rating: рейтинг не може бути меншим за 0`.

**3.4 Валідація на PUT**
```bash
curl -i -X PUT http://localhost:8080/api/movies/1 \
  -H "Content-Type: application/json" \
  -d '{ "title": " ", "director": "Nolan", "year": 2010, "genre": "Sci-Fi" }'
```
Очікується 400 (а не 200 і не 404).

**3.5 Нічого не змінилося після невалідного запиту**
```bash
curl http://localhost:8080/api/movies/1     # назва досі "Inception"
```

## 4. 400 — інші помилки запиту

| # | Запит | Очікується |
|---|---|---|
| 4.1 | `curl -i -X POST http://localhost:8080/api/movies -H "Content-Type: application/json" -d '{not json'` | 400, `"message":"Некоректне тіло запиту"` |
| 4.2 | `curl -i http://localhost:8080/api/movies/abc` | 400, `"message":"Некоректне значення параметра 'id'"` |

## 5. Порядок перевірки: пріоритет 404 над 400?

Невалідне тіло на неіснуючий id — спрацьовує **валідація** (400), бо `@Valid` виконується до сервісу:
```bash
curl -i -X PUT http://localhost:8080/api/movies/999 \
  -H "Content-Type: application/json" \
  -d '{ "title": "", "director": "Y", "year": 2000, "genre": "G" }'     # 400
```

---

## Чек-лист результату

| Перевірка | Статус | Тіло = `ErrorResponse` | OK? |
|---|---|---|---|
| 2.1 GET неіснуючий | 404 | так | ☐ |
| 2.2 PUT неіснуючий | 404 | так | ☐ |
| 2.3 DELETE неіснуючий | 404 | так | ☐ |
| 3.1 порожня назва | 400 | так, `details` масив | ☐ |
| 3.2 кілька помилок | 400 | 3 елементи в `details` | ☐ |
| 3.3 рейтинг < 0 | 400 | так | ☐ |
| 3.4 PUT невалідний | 400 | так | ☐ |
| 4.1 битий JSON | 400 | так | ☐ |
| 4.2 `id=abc` | 400 | так | ☐ |

Якщо хоч один рядок повертає не `ErrorResponse` (наприклад, стандартну сторінку Spring
з полями `timestamp`/`path`) — відповідний обробник у `GlobalExceptionHandler` не спрацював.
