# F1 Telemetry Microservices Platform

Демонстрационный проект микросервисной архитектуры на тему **Формула 1**, реализованный с использованием Spring Boot и
развёрнутый в Kubernetes (Minikube) с помощью Helm Charts.

## 📋 Описание проекта

Система состоит из трёх микросервисов, объединённых через API Gateway (KrakenD) и взаимодействующих через Apache Kafka:

| Сервис                | Описание                                                                  | Порт |
|-----------------------|---------------------------------------------------------------------------|------|
| **auth-service**      | Авторизация пользователей, генерация JWT токенов, хранение сессий в Redis | 8080 |
| **race-service**      | Управление данными о гонках и пилотах F1, отправка событий в Kafka        | 8081 |
| **telemetry-service** | Приём телеметрии из Kafka, агрегация и хранение данных                    | 8082 |

### Архитектура

```
┌─────────────┐     ┌──────────────────────────────────────────────────────┐
│   Client    │────▶│                    KrakenD                           │
└─────────────┘     │               (API Gateway)                          │
                    └──────────────────────────────────────────────────────┘
                           │              │                │
                           ▼              ▼                ▼
                    ┌────────────┐ ┌────────────┐ ┌────────────────┐
                    │auth-service│ │race-service│ │telemetry-service│
                    └────────────┘ └────────────┘ └────────────────┘
                           │              │                │
                           ▼              │                │
                    ┌────────────┐        │                │
                    │   Redis    │        │                │
                    │  (tokens)  │        │                │
                    └────────────┘        ▼                ▼
                                   ┌────────────┐   ┌────────────┐
                                   │   Kafka    │──▶│  Consumer  │
                                   └────────────┘   └────────────┘
                           │              │                │
                           ▼              ▼                ▼
                    ┌────────────┐ ┌────────────┐ ┌────────────────┐
                    │PostgreSQL  │ │PostgreSQL  │ │  PostgreSQL    │
                    │ (auth_db)  │ │ (race_db)  │ │(telemetry_db)  │
                    └────────────┘ └────────────┘ └────────────────┘
```

---

## 🛠 Технологический стек

### Backend

- **Kotlin** + **Spring Boot 3.5**
- **Spring Data JPA** — работа с PostgreSQL
- **Spring Data Redis** — кэширование токенов
- **Spring Kafka** — асинхронное взаимодействие
- **jjwt** — генерация/валидация JWT токенов

### Infrastructure

- **PostgreSQL** — основное хранилище (3 инстанса, паттерн "1 БД на 1 сервис")
- **Redis** — хранение JWT токенов для auth-service
- **Apache Kafka** (Strimzi, KRaft mode) — шина сообщений между сервисами
- **KrakenD** — API Gateway

### Observability

- **Prometheus** + **Grafana** — метрики и дашборды
- **Jaeger** — распределённая трассировка (OpenTelemetry)
- **Graylog** — централизованное логирование (GELF)

### Kubernetes / Helm

- **Minikube** — локальный кластер
- **Helm Charts** — параметризованное развёртывание
- **HPA** — горизонтальное автомасштабирование

---

## 📁 Структура проекта

```
├── auth-service/                 # Сервис авторизации
│   ├── src/main/kotlin/...
│   │   ├── controller/           # REST API (/api/auth/*)
│   │   ├── service/              # Бизнес-логика, JWT
│   │   ├── repository/           # JPA репозитории
│   │   └── filter/               # RequestLoggingFilter
│   ├── Dockerfile
│   └── build.gradle.kts
│
├── race-service/                 # Сервис гонок и пилотов
│   ├── src/main/kotlin/...
│   │   ├── controller/           # REST API (/api/races/*, /api/drivers/*)
│   │   ├── kafka/                # Kafka Producer
│   │   └── filter/               # JWT фильтр, логирование
│   ├── Dockerfile
│   └── build.gradle.kts
│
├── telemetry-service/            # Сервис телеметрии
│   ├── src/main/kotlin/...
│   │   ├── controller/           # REST API (/api/telemetry/*)
│   │   ├── kafka/                # Kafka Consumer
│   │   └── filter/               # JWT фильтр, логирование
│   ├── Dockerfile
│   └── build.gradle.kts
│
├── k8s/
│   ├── helm/
│   │   ├── auth-service/         # Helm Chart для auth-service
│   │   │   ├── Chart.yaml
│   │   │   ├── values.yaml       # Probes, resources, autoscaling
│   │   │   └── templates/
│   │   │       ├── deployment.yaml
│   │   │       ├── service.yaml
│   │   │       ├── hpa.yaml      # HPA (60% CPU)
│   │   │       ├── configmap.yaml
│   │   │       └── secret.yaml
│   │   │
│   │   ├── race-service/         # Helm Chart для race-service
│   │   ├── telemetry-service/    # Helm Chart для telemetry-service
│   │   ├── krakend/              # Helm Chart для API Gateway
│   │   │
│   │   └── infrastructure/
│   │       ├── postgres-auth/    # PostgreSQL для auth-service
│   │       ├── postgres-race/    # PostgreSQL для race-service
│   │       ├── postgres-telemetry/ # PostgreSQL для telemetry-service
│   │       ├── redis/            # Redis values
│   │       └── prometheus-stack/ # Prometheus + Grafana values
│   │
│   └── manifests/
│       ├── kafka-strimzi.yaml    # Kafka (Strimzi Operator)
│       ├── jaeger.yaml           # Jaeger All-in-One
│       └── graylog.yaml          # Graylog + OpenSearch + MongoDB
│
├── gradle/
│   └── libs.versions.toml        # Централизованные версии зависимостей
│
├── DEPLOYMENT_GUIDE.md           # Пошаговое руководство по развёртыванию
└── README.md                     # Этот файл
```

---

## 🚀 Быстрый старт

Подробное руководство по развёртыванию см. в [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md).

```bash
# 1. Запуск Minikube
minikube delete && minikube start --cpus=4 --memory=16384 --driver=docker
minikube addons enable metrics-server

# 2. Сборка образов
minikube docker-env | Invoke-Expression  # PowerShell
./gradlew clean bootJar -x test
docker build -t auth-service:latest -f auth-service/Dockerfile .
docker build -t race-service:latest -f race-service/Dockerfile .
docker build -t telemetry-service:latest -f telemetry-service/Dockerfile .

# 3. Установка инфраструктуры (PostgreSQL, Redis, Kafka)
kubectl create namespace f1-app
helm install auth-db bitnami/postgresql -f k8s/helm/infrastructure/postgres-auth/values.yaml -n f1-app
# ... и далее по инструкции

# 4. Установка сервисов
helm install auth-service k8s/helm/auth-service -n f1-app
helm install race-service k8s/helm/race-service -n f1-app
helm install telemetry-service k8s/helm/telemetry-service -n f1-app
helm install krakend k8s/helm/krakend -n f1-app

# 5. Доступ к API
kubectl port-forward svc/api-gateway 8080:8080 -n f1-app
```

---

## 🔌 API Endpoints

### Auth Service (`/api/auth`)

| Метод | Endpoint             | Описание                   | Auth |
|-------|----------------------|----------------------------|------|
| POST  | `/api/auth/register` | Регистрация пользователя   | ❌    |
| POST  | `/api/auth/login`    | Логин, получение JWT       | ❌    |
| GET   | `/api/auth/validate` | Проверка токена            | ✅    |
| POST  | `/api/auth/logout`   | Выход (инвалидация токена) | ✅    |

### Race Service (`/api/races`, `/api/drivers`)

| Метод  | Endpoint          | Описание       | Auth |
|--------|-------------------|----------------|------|
| GET    | `/api/races`      | Список гонок   | ✅    |
| POST   | `/api/races`      | Создать гонку  | ✅    |
| GET    | `/api/races/{id}` | Получить гонку | ✅    |
| DELETE | `/api/races/{id}` | Удалить гонку  | ✅    |
| GET    | `/api/drivers`    | Список пилотов | ✅    |
| POST   | `/api/drivers`    | Создать пилота | ✅    |

### Telemetry Service (`/api/telemetry`)

| Метод | Endpoint                         | Описание         | Auth |
|-------|----------------------------------|------------------|------|
| GET   | `/api/telemetry`                 | Вся телеметрия   | ✅    |
| GET   | `/api/telemetry/{id}`            | Телеметрия по ID | ✅    |
| GET   | `/api/telemetry/source/{source}` | По источнику     | ✅    |
| GET   | `/api/telemetry/stats`           | Статистика       | ✅    |

---

## ⚙️ Конфигурация Kubernetes

### Resource Limits & Requests

```yaml
resources:
  requests:
    cpu: "100m"
    memory: "256Mi"
  limits:
    cpu: "500m"
    memory: "512Mi"
```

### Horizontal Pod Autoscaler (HPA)

```yaml
spec:
  minReplicas: 1
  maxReplicas: 3
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60  # Target 60% CPU
```

### Health Probes

```yaml
startupProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 30
  failureThreshold: 30

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```

### PostgreSQL Configuration

- **Persistent Volume**: 1Gi
- **Max Connections**: 100
- **Monitoring User**: `postgres_exporter`
- **Metrics**: postgres-exporter на порту 9187

---

## 📊 Observability

### Grafana (http://localhost:3000)

- Логин: `admin` / Пароль: `admin`
- Дашборды: PostgreSQL, Kubernetes Pods, JVM Metrics

### Jaeger (http://localhost:16686)

- Трассировка запросов между сервисами
- Визуализация latency

### Graylog (http://localhost:9000)

- Логин: `admin` / Пароль: `admin`
- Централизованные логи с HTTP-методом, URL, IP

---

## 📝 Логирование

Каждый сервис логирует входящие запросы через `RequestLoggingFilter`:

```json
{
  "http_method": "POST",
  "request_uri": "/api/auth/login",
  "client_ip": "172.17.0.1",
  "service": "auth-service",
  "@timestamp": "2025-12-16T12:00:00Z"
}
```

Логи отправляются в Graylog через GELF UDP (порт 12201).

---

## 🔒 Авторизация

1. **Регистрация** → сохранение пользователя в PostgreSQL
2. **Логин** → генерация JWT (HS384), сохранение в Redis
3. **Запрос к API** → валидация JWT в сервисе
4. **Logout** → удаление токена из Redis

JWT секрет синхронизирован между всеми сервисами через Kubernetes Secrets.

---

## 🐳 Docker Images

Все образы используют multi-stage build:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
# Сборка JAR

FROM eclipse-temurin:21-jre-alpine
# Runtime с минимальным размером
```

---

## 📚 Документация

- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) — пошаговое руководство по развёртыванию
- [Postman Collection](postman/F1_Minikube.postman_collection.json) — коллекция для тестирования API (с развернутым
  кластером)
