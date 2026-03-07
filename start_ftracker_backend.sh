#!/bin/bash

export $(grep -v '^#' .env | xargs)

java -jar build/libs/ftracker-0.0.1-SNAPSHOT.jar
