#!/bin/bash

echo "=== Build du frontend Angular ==="

npm install
npm run build

if [ $? -ne 0 ]; then
  echo "❌ Échec du build frontend"
  exit 1
fi

echo "✅ Frontend prêt (dossier dist/)"
