#!/bin/bash

echo "=== Migration de la base de données (Flyway) ==="

mvn -P env flyway:migrate

if [ $? -ne 0 ]; then
  echo "❌ Erreur lors des migrations Flyway"
  exit 1
fi

echo "✅ Base de données à jour"
