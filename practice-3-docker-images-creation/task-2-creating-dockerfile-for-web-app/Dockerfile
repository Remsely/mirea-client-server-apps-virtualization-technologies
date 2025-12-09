FROM gradle:8.14.3-jdk21 AS build

WORKDIR /app

COPY . .

RUN apt-get update && apt-get install -y wget && \
    wget -P src/main/resources/static https://www.mirea.ru/upload/medialibrary/80f/MIREA_Gerb_Colour.png

RUN gradle build -x test --no-daemon

FROM openjdk:21-slim

LABEL author="Прокопчук Р.О." group="ИКБО-01-22"

WORKDIR /app

ENV DB_PORT=5432

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ONBUILD RUN echo "Сборка и запуск произведены. Автор: Прокопчук Р.О."

ENTRYPOINT ["java", "-jar", "app.jar"]
