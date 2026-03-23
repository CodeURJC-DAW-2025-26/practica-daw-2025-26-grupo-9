#!/bin/bash

if [ -z "$1" ]; then
    echo "Uso: $0 <usuario/nombre-imagen>"
    exit 1
fi

IMAGE_NAME=$1

# Image Push
docker push $IMAGE_NAME