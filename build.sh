#!/usr/bin/env bash

npm run build --prefix health-care-information-system-frontend
rm -R health-care-information-system-backend/src/main/resources/public/*
cp -R health-care-information-system-frontend/build/. health-care-information-system-backend/src/main/resources/public/
