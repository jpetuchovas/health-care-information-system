#!/usr/bin/env bash

node_modules/prettier/bin/prettier.js --single-quote --trailing-comma es5 --write 'src/**/*.{js,jsx,json,css}'
