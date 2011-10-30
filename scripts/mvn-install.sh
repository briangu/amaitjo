#!/bin/bash
mvn install:install-file -Dfile=lib/ants-api.jar -DgroupId=org.linkedin.contest.ants.api -DartifactId=ants-api -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/ants-server.jar -DgroupId=org.linkedin.contest.ants.server -DartifactId=ants-server -Dversion=1.0.0 -Dpackaging=jar


