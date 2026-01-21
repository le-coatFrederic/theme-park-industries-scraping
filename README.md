# Theme Park Industries - Data Scraper

Application backend Java/Spring Boot permettant de scraper automatiquement les données du jeu en ligne [Theme Park Industries](https://themeparkindustries.com), de les parser et de les stocker en base de données PostgreSQL.

## Fonctionnalités

- **Scraping automatisé** des données de jeu via Selenium WebDriver (headless Chrome)
- **Extraction des attractions** : prix, capacité, fiabilité, type (manège/coaster)
- **Extraction des villes** : population, surface, difficulté, prix au m²
- **Suivi du tableau de bord** : achats/ventes d'attractions, acquisitions de terrain
- **Parsing intelligent** des activités avec pattern matching (regex)
- **Tâches planifiées** : rafraîchissement automatique des données (1min / 30min / 24h)
- **Dockerisé** pour un déploiement simplifié

## Stack technique

| Catégorie | Technologies |
|-----------|--------------|
| **Backend** | Java 25, Spring Boot 4.0, Spring Data JPA |
| **Scraping** | Selenium WebDriver, WebDriverManager, JSoup |
| **Base de données** | PostgreSQL, Hibernate |
| **Infrastructure** | Docker, Docker Compose |
| **Build** | Maven |

## Architecture

```
backend/
├── entities/          # Modèles JPA (Player, Park, Ride, City, DashboardActivity)
├── repositories/      # Couche d'accès aux données
├── services/          # Logique métier et scraping
├── parsers/           # Parsers d'événements (achat, vente, destruction)
├── schedulers/        # Tâches planifiées automatiques
└── configuration/     # Configuration Selenium et credentials
```

### Modèle de données

```
Player ──1:N──> PlayerData (money, level, xp)
   │
   └──1:N──> Park ──M:N──> Ride
               │
               └──N:1──> City

DashboardActivity (logs des actions : achats, ventes, etc.)
```

## Installation

### Prérequis
- Docker & Docker Compose
- PostgreSQL (ou via Docker)

### Configuration

Créer un fichier `.env` dans `/backend` :

```env
URL_POSTGRES=jdbc:postgresql://localhost:5432/theme-park-industries
USER_POSTGRESQL=postgres
PASSWORD_POSTGRESQL=your_password

URL_TPI=https://themeparkindustries.com/tpiv4/
USER_TPI=your_email
PASSWORD_TPI=your_password
```

### Lancement

```bash
docker-compose up -d
```

## Compétences démontrées

- **Web Scraping** : Automatisation avec Selenium, gestion des sessions, contournement de détection
- **Architecture backend** : Conception en couches, injection de dépendances, patterns de service
- **Modélisation BDD** : Relations JPA (1:N, M:N), entités avec audit (createdOn/updatedOn)
- **DevOps** : Dockerisation multi-stage, configuration par environnement
- **Parsing de données** : Extraction structurée via regex et pattern matching
- **Planification** : Tâches cron avec Spring Scheduler

## Auteur

Frédéric Lecoat
