FROM alpine:latest

LABEL author="Prokopchuk Roman" group="IKBO-01-22"

ENV APP_VERSION=1.0

WORKDIR /app

ADD https://raw.githubusercontent.com/docker-library/hello-world/master/hello.c .

COPY file.txt .

RUN adduser -D remsely

USER remsely

VOLUME /app/data

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c"]

CMD ["echo App ${APP_VERSION} started."]

ONBUILD RUN echo "Creating new image based on my image..."
