#!/bin/bash

if [ -z "$1" ]; then
    echo "Uso: $0 <usuario/nombre-imagen>"
    exit 1
fi

IMAGE_NAME=$1

# Go to the root of the project
cd "$(dirname "$0")/.."

# Image Build using Dockerfile
docker build -t $IMAGE_NAME -f docker/Dockerfile .