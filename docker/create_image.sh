#!/bin/bash

if [ -z "$1" ]; then
  echo "Uso: ./create_image.sh usuario/nombre-imagen"
  exit 1
fi

docker build -t $1 -f docker/Dockerfile .
