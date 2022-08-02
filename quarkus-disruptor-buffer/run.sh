#!/bin/bash
set -evuo pipefail
cd ${0%/*}

mvn clean package

java -jar target/quarkus-app/quarkus-run.jar
