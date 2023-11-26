#!/bin/sh

mvn -f software/pom.xml clean package -DskipTests=true

cd infra

cdk deploy --outputs-file target/output.json