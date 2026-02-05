# Scripts de déploiement – Hexavolt

## Objectif
Ces scripts permettent de standardiser les principales étapes de déploiement de l’application Hexavolt.
Ils ont été mis en place pendant le développement et sont amenés à évoluer.

## Scripts disponibles

### run-flyway.sh
Exécute les migrations Flyway afin de mettre à jour le schéma de la base de données.

### deploy-backend.sh
Construit le backend Spring Boot et lance l’application.

### build-frontend.sh
Construit l’application Angular pour un déploiement via Nginx.

## Remarque
Ces scripts ne constituent pas un pipeline automatisé complet.
Ils visent à simplifier et sécuriser les déploiements manuels pendant le projet.
