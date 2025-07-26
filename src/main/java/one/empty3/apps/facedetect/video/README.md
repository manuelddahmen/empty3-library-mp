# Service de génération de vidéos

## Description

Ce service est une fonction Google Cloud qui génère des vidéos à partir d'images et de fichiers texte. Il accepte des fichiers via une requête HTTP multipart/form-data, crée une vidéo et la stocke dans Google Cloud Storage.

## Utilisation

### Endpoint

```
POST https://us-central1-studio-6v2lo.cloudfunctions.net/motion-weaver-render
```

### Types de fichiers acceptés

- Fichiers texte (`.txt`)
- Images JPEG (`.jpg`)
- Images PNG (`.png`)
- Fichiers JSON (`.json`)

### Réponse

En cas de succès, le service renvoie une réponse JSON contenant l'URL de la vidéo générée :

```json
{
  "videoUrl": "https://storage.googleapis.com/...",
  "mimeType": "video/mp4"
}
```

En cas d'erreur, le service renvoie un code d'erreur approprié et un message explicatif au format JSON :

```json
{
  "error": true,
  "code": 500,
  "message": "Description de l'erreur"
}
```

## Déploiement

Pour déployer cette fonction sur Google Cloud Functions :

```bash
./gradlew buildFunction
gcloud functions deploy motion-weaver-render \
  --runtime java21 \
  --trigger-http \
  --allow-unauthenticated \
  --memory 1024MB \
  --timeout 540s \
  --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction \
  --source build/libs
```

## Test local

Pour tester la fonction localement :

```bash
./gradlew runFunction
```

La fonction sera disponible à l'adresse `http://localhost:8080`.
