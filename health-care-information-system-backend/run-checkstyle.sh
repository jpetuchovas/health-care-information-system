#!/usr/bin/env bash

mvn checkstyle:check -Dcheckstyle.config.location=src/main/resources/checkstyle.xml -Dcheckstyle.violationSeverity=warning
