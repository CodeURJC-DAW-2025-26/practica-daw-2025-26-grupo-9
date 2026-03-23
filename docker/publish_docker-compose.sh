#!/bin/bash

if [ -z "$1" ]; then
    echo "Uso: $0 <usuario/docker-compose-name>"
    exit 1
fi

COMPOSE_NAME=$1

# Go to the root of the project
cd "$(dirname "$0")/.."

# Export all necessary variables for docker-compose
export DOCKER_IMAGE=neokyouma/equisdaw:latest
export DB_NAME=equis
export DB_USERNAME=root
export DB_PASSWORD=password
export DDL_AUTO=update
export APP_LOAD_SAMPLE_DATA=true

# Publish the docker-compose on Docker Hub
docker compose -f docker/docker-compose.yml publish $COMPOSE_NAME --with-env