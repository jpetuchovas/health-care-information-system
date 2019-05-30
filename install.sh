#!/usr/bin/env bash

chmod +x backend.sh frontend.sh build.sh health-care-information-system-backend/run-checkstyle.sh health-care-information-system-frontend/run-prettier.sh
npm install --prefix ./health-care-information-system-frontend
./build.sh
cd health-care-information-system-backend
mvn clean package
cd ..
