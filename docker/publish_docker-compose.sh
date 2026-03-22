#!/bin/bash

if [ -z "$1" ]; then
  echo "Uso: ./publish_docker-compose.sh usuario"
  exit 1
fi

USERNAME=$1

docker buildx build \
  --platform linux/amd64 \
  -t $USERNAME/equisdaw-compose:latest \
  --push \
  -f docker-compose.yml .