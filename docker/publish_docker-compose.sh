#!/bin/bash

docker login
docker compose -f docker/docker-compose.yml push
