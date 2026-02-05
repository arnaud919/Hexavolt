# Déploiement de l’application Hexavolt

## 1. Objectif

Ce document décrit la procédure de déploiement minimale de l’application Hexavolt.
L’objectif est de permettre l’exécution de l’application hors du mode développement, dans un contexte simple et maîtrisé.

---

## 2. Architecture générale

L’application repose sur une architecture classique :

- Frontend : Angular
- Backend : Spring Boot
- Base de données : MySQL

Le frontend et le backend sont déployés séparément.

---

## 3. Environnements

### Développement
- Exécution locale
- Frontend lancé avec le serveur de développement Angular
- Backend lancé via Maven
- Base de données MySQL locale

### Pré-production
- Environnement proche de la production
- Utilisé pour les tests d’intégration et la recette fonctionnelle
- Déploiement manuel

---

## 4. Déploiement minimal

### Backend

Le backend est compilé puis exécuté sous forme de fichier JAR.

Étapes :

mvn clean package
java -jar target/hexavolt.jar

---

### Frontend

Le frontend Angular est compilé afin de générer des fichiers statiques.

Étapes :

npm install
npm run build

Le dossier `dist/` contient l’application prête à être servie par un serveur web.

---

### Base de données

Le schéma de la base de données est géré par Flyway.

Étape :

mvn flyway:migrate

---

## 5. Scripts de déploiement

Des scripts simples ont été mis en place afin de standardiser les principales étapes du déploiement (build frontend, build backend, exécution des migrations).
Ils sont regroupés dans un dossier dédié.

---

## 6. Réseau et CORS

Le CORS n’est pas configuré explicitement côté backend.
En environnement de déploiement cible, le frontend et le backend sont servis sous la même origine ce qui permet d’éviter les problématiques CORS.

---

## 7. Vérifications post-déploiement

Après le déploiement, les vérifications suivantes sont effectuées :
- l’application est accessible,
- le backend démarre correctement,
- les migrations sont appliquées,
- les appels API fonctionnent.

---

## 8. Conclusion

Cette procédure décrit un déploiement volontairement simple, dapté à un projet en cours de développement.