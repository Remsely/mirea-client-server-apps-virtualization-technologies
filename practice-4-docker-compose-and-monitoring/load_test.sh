#!/bin/bash

echo "Начало нагрузочного теста..."

for i in {1..100}
do
  curl -X POST http://localhost:8080/api/students \
    -H "Content-Type: application/json" \
    -d "{
      \"firstName\": \"Student$i\",
      \"lastName\": \"Test$i\",
      \"email\": \"test$i@student.mirea.ru\",
      \"studentNumber\": \"ST$i\",
      \"courseId\": 1
    }" &

  curl http://localhost:8080/api/students > /dev/null 2>&1 &

  curl http://localhost:8080/api/courses > /dev/null 2>&1 &

  if [ $((i % 10)) -eq 0 ]; then
    echo "Выполнено запросов: $i"
    sleep 1
  fi
done

wait
echo "Нагрузочный тест завершён!"
