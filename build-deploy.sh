#!/bin/sh

mvn -f after/pom.xml clean package -DskipTests=true

cd infra

cdk deploy