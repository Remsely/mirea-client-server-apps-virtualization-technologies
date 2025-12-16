# F1 Microservices - Руководство по развёртыванию в Minikube

## Предварительные требования

- Minikube установлен
- kubectl установлен  
- Helm 3.x установлен
- Docker Desktop (для сборки образов)

---

## Шаг 1: Подготовка кластера

```bash
# Полный сброс (удаление старого кластера)
minikube delete

# Запуск нового кластера с достаточными ресурсами
minikube start --cpus=4 --memory=16384 --driver=docker

# Включение необходимых аддонов
minikube addons enable metrics-server
minikube addons enable ingress
```

---

## Шаг 2: Сборка Docker-образов

```bash
# Переключаемся на Docker демон Minikube
eval $(minikube docker-env)           # Linux/Mac
minikube docker-env | Invoke-Expression    # PowerShell

# Собираем JAR-файлы
./gradlew clean bootJar -x test

# Собираем Docker-образы
docker build -t auth-service:latest -f auth-service/Dockerfile .
docker build -t race-service:latest -f race-service/Dockerfile .
docker build -t telemetry-service:latest -f telemetry-service/Dockerfile .
```

---

## Шаг 3: Добавление Helm-репозиториев

```bash
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add strimzi https://strimzi.io/charts/
helm repo update
```

---

## Шаг 4: Создание Namespace

```bash
kubectl create namespace f1-app
kubectl create namespace monitoring
```

---

## Шаг 5: Установка инфраструктуры

### 5.1 PostgreSQL (3 инстанса — паттерн "1 БД на 1 сервис")

```bash
helm install auth-db bitnami/postgresql \
  -f k8s/helm/infrastructure/postgres-auth/values.yaml \
  -n f1-app

helm install race-db bitnami/postgresql \
  -f k8s/helm/infrastructure/postgres-race/values.yaml \
  -n f1-app

helm install telemetry-db bitnami/postgresql \
  -f k8s/helm/infrastructure/postgres-telemetry/values.yaml \
  -n f1-app
```

### 5.2 Redis (для хранения JWT токенов)

```bash
helm install redis bitnami/redis \
  -f k8s/helm/infrastructure/redis/values.yaml \
  -n f1-app
```

### 5.3 Kafka (Strimzi Operator — KRaft mode без Zookeeper)

```bash
# Установить Strimzi оператор
helm install strimzi strimzi/strimzi-kafka-operator -n f1-app

# Подождать пока оператор запустится
kubectl get pods -n f1-app -l name=strimzi-cluster-operator -w

# Применить Kafka кластер
kubectl apply -f k8s/manifests/kafka-strimzi.yaml -n f1-app

# Подождать пока Kafka запустится
kubectl get pods -n f1-app -l strimzi.io/name=kafka-kafka -w
```

> **Примечание:** Strimzi создаст поды:
> - `kafka-kafka-pool-0` — Kafka брокер/контроллер (KRaft)
> - `kafka-entity-operator-xxx` — оператор для топиков
> - Сервис для подключения: `kafka-kafka-bootstrap:9092`

### 5.4 Проверка инфраструктуры

```bash
kubectl get pods -n f1-app
# Подождите пока все поды станут Running (1/1 или 2/2)
```

---

## Шаг 6: Установка мониторинга

### 6.1 Prometheus + Grafana

```bash
helm install monitoring prometheus-community/kube-prometheus-stack \
  -f k8s/helm/infrastructure/prometheus-stack/values.yaml \
  -n monitoring
```

### 6.2 Jaeger (трассировка)

```bash
kubectl apply -f k8s/manifests/jaeger.yaml -n monitoring
```

### 6.3 Graylog (логирование)

```bash
kubectl apply -f k8s/manifests/graylog.yaml -n monitoring
```

### 6.4 Проверка мониторинга

```bash
kubectl get pods -n monitoring
# Подождите пока все поды станут Running
```

---

## Шаг 7: Установка микросервисов

```bash
helm install auth-service k8s/helm/auth-service -n f1-app
helm install race-service k8s/helm/race-service -n f1-app
helm install telemetry-service k8s/helm/telemetry-service -n f1-app
```

---

## Шаг 8: Установка API Gateway (KrakenD)

```bash
helm install krakend k8s/helm/krakend -n f1-app
```

---

## Шаг 9: Проверка развёртывания

```bash
# Все поды в f1-app
kubectl get pods -n f1-app

# Все сервисы
kubectl get svc -n f1-app

# HPA (автомасштабирование)
kubectl get hpa -n f1-app

# Мониторинг
kubectl get pods -n monitoring
```

---

## Шаг 10: Доступ к сервисам

### Port-forwarding (рекомендуется)

Открыть в отдельных терминалах:

```bash
# KrakenD (API Gateway) - главная точка входа
kubectl port-forward svc/api-gateway 8080:8080 -n f1-app

# Grafana (метрики и дашборды)
kubectl port-forward svc/monitoring-grafana 3000:80 -n monitoring

# Jaeger (трассировка запросов)
kubectl port-forward svc/jaeger 16686:16686 -n monitoring

# Graylog (централизованные логи)
kubectl port-forward svc/graylog 9000:9000 -n monitoring
```

### Альтернатива: Minikube service

```bash
minikube service api-gateway -n f1-app --url
minikube service monitoring-grafana -n monitoring --url
minikube service jaeger -n monitoring --url
minikube service graylog -n monitoring --url
```

---

## Доступы по умолчанию

| Сервис | URL | Логин | Пароль |
|--------|-----|-------|--------|
| **API Gateway** | http://localhost:8080 | - | - |
| **Grafana** | http://localhost:3000 | admin | admin |
| **Graylog** | http://localhost:9000 | admin | admin |
| **Jaeger** | http://localhost:16686 | - | - |

---

## Тестирование API

### 1. Регистрация пользователя

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
```

### 2. Логин (получение JWT токена)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'
```

Ответ содержит токен:
```json
{"token":"eyJhbGciOiJIUzM4NCJ9...","username":"demo"}
```

### 3. Запрос БЕЗ токена (ожидается 401)

```bash
curl http://localhost:8080/api/races
# Ответ: 401 Unauthorized
```

### 4. Запрос С токеном

```bash
curl http://localhost:8080/api/races \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

### 5. Создание гонки (триггерит Kafka)

```bash
curl -X POST http://localhost:8080/api/races \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Monaco GP","circuit":"Monaco","date":"2025-05-25","country":"Monaco"}'
```

### 6. Проверка телеметрии (данные из Kafka)

```bash
curl http://localhost:8080/api/telemetry \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## Настройка Graylog Input

1. Откройте Graylog UI: http://localhost:9000
2. Войдите: **admin** / **admin**
3. Перейдите: **System → Inputs**
4. Выберите: **GELF UDP**
5. Нажмите **Launch new input**
6. Заполните:
   - Title: `GELF UDP Input`
   - Port: `12201`
   - Bind address: `0.0.0.0`
7. **Save**

После этого логи от сервисов начнут поступать в Graylog.

---

## Демонстрация Persistent Volume (сохранение данных)

### Шаг 1: Создаём данные

```bash
# Создаём гонку
curl -X POST http://localhost:8080/api/races \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Monaco GP","circuit":"Monaco","date":"2025-05-25","country":"Monaco"}'

# Проверяем
curl http://localhost:8080/api/races -H "Authorization: Bearer <TOKEN>"
```

### Шаг 2: Удаляем под с PostgreSQL

```bash
# Удаляем под БД race-service
kubectl delete pod -l app.kubernetes.io/instance=race-db -n f1-app

# Следим за восстановлением
kubectl get pods -n f1-app -w
```

### Шаг 3: Проверяем что данные сохранились

```bash
# После восстановления пода данные должны быть на месте
curl http://localhost:8080/api/races -H "Authorization: Bearer <TOKEN>"
```

---

## Демонстрация автомасштабирования (HPA)

```bash
# Проверяем HPA
kubectl get hpa -n f1-app

# Убиваем под для демонстрации перезапуска
kubectl exec -it deployment/auth-service -n f1-app -- kill 1

# Смотрим как создаются новые реплики
kubectl get pods -n f1-app -w
```

---

## Полезные команды

```bash
# Логи пода
kubectl logs -f deployment/auth-service -n f1-app

# Описание пода (для отладки)
kubectl describe pod <pod-name> -n f1-app

# Перезапуск deployment
kubectl rollout restart deployment auth-service -n f1-app

# Статус деплоя
kubectl rollout status deployment auth-service -n f1-app

# Масштабирование вручную
kubectl scale deployment auth-service --replicas=3 -n f1-app
```

---

## Удаление всего

```bash
# Микросервисы и KrakenD
helm uninstall auth-service race-service telemetry-service krakend -n f1-app

# Инфраструктура
helm uninstall auth-db race-db telemetry-db redis -n f1-app
kubectl delete -f k8s/manifests/kafka-strimzi.yaml -n f1-app
helm uninstall strimzi -n f1-app

# Мониторинг
helm uninstall monitoring -n monitoring
kubectl delete -f k8s/manifests/jaeger.yaml -n monitoring
kubectl delete -f k8s/manifests/graylog.yaml -n monitoring

# Namespace
kubectl delete namespace f1-app
kubectl delete namespace monitoring

# Полное удаление кластера
minikube delete
```

---

## Чек-лист для отчёта

### ✅ Конфигурации и Helm Charts
- [ ] Структура проекта
- [ ] values.yaml для сервисов (probes, resources, autoscaling)
- [ ] deployment.yaml шаблоны
- [ ] hpa.yaml (targetCPUUtilization: 60%)
- [ ] PostgreSQL values (persistence, max_connections, monitoring user)
- [ ] KrakenD krakend.json (роутинг)

### ✅ Авторизация
- [ ] Регистрация через `/api/auth/register`
- [ ] Логин через `/api/auth/login` → JWT токен
- [ ] Запрос без токена → 401
- [ ] Запрос с токеном → 200 + данные

### ✅ Kafka
- [ ] Создание гонки → сообщение в Kafka
- [ ] Telemetry-service получает данные

### ✅ Persistent Volume
- [ ] Создать данные
- [ ] Удалить под PostgreSQL
- [ ] Данные сохранились после восстановления

### ✅ Мониторинг
- [ ] **Grafana**: дашборд с метриками PostgreSQL
- [ ] **Jaeger**: трассировка запроса через сервисы
- [ ] **Graylog**: логи с HTTP-методом, URL, IP

### ✅ Probes и перезапуск
- [ ] Показать liveness/readiness/startup probes в deployment
- [ ] Убить под → автоматический перезапуск
- [ ] HPA увеличивает реплики при нагрузке

