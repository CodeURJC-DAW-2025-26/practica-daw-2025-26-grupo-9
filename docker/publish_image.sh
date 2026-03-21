#!/bin/bash

if [ -z "$1" ]; then
  echo "Uso: ./publish_image.sh usuario/nombre-imagen"
  exit 1
fi

docker push $1
