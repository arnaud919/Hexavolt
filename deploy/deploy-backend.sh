#!/bin/bash

echo "=== Build du backend Spring Boot ==="

mvn clean package -DskipTests

if [ $? -ne 0 ]; then
  echo "❌ Échec du build backend"
  exit 1
fi

echo "✅ Build réussi"

echo "=== Lancement de l'application ==="
java -jar target/*.jar
