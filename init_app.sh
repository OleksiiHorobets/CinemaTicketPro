#!/bin/bash

echo "Running Gradle clean and build..."
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "Gradle build failed. Exiting."
  exit 1
fi

DOCKER_IMG="cinematicketpro-cinema-ticket:latest"
COMPOSE_PROJECT_NAME="cinematicketpro"
DOCKER_COMPOSE_FILE="./docker-compose.yaml"

if docker images -q "$DOCKER_IMG" &>/dev/null; then
  echo "Deleting Docker image: $DOCKER_IMG"
  docker rmi "$DOCKER_IMG"
else
  echo "Docker image '$DOCKER_IMG' not found."
fi

echo "Stopping and removing Docker container: $COMPOSE_PROJECT_NAME"
docker-compose -f "$DOCKER_COMPOSE_FILE" down

echo "Starting up Docker Compose"
docker-compose -f "$DOCKER_COMPOSE_FILE" up -d --build