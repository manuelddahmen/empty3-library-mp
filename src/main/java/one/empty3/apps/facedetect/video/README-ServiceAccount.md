# Configuration des identifiants de compte de service pour l'accès à Google Cloud Storage

Ce document explique comment configurer correctement les identifiants de compte de service pour générer des URLs signées dans Google Cloud Storage.

## Étape 1: Créer un compte de service

1. Accédez à la [console Google Cloud](https://console.cloud.google.com/)
2. Sélectionnez votre projet (`studio-6v2lo`)
3. Dans le menu de navigation, allez à "IAM & Admin" > "Service accounts"
4. Cliquez sur "Create Service Account"
5. Donnez un nom à votre compte de service, par exemple "storage-url-signer"
6. Attribuez-lui le rôle "Storage Object Creator" et "Storage Object Viewer"
7. Cliquez sur "Done"

## Étape 2: Créer une clé pour le compte de service

1. Dans la liste des comptes de service, cliquez sur le compte que vous venez de créer
2. Allez dans l'onglet "Keys"
3. Cliquez sur "Add Key" > "Create new key"
4. Sélectionnez le format "JSON"
5. Cliquez sur "Create". Un fichier JSON sera téléchargé sur votre ordinateur

## Étape 3: Configurer l'environnement

### Option 1: En local

Définissez la variable d'environnement `GOOGLE_APPLICATION_CREDENTIALS` pour pointer vers le fichier JSON téléchargé:

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/chemin/vers/votre-fichier-credentials.json"
```

### Option 2: Sur Google Cloud Functions

1. Allez dans la section "Cloud Functions" de la console Google Cloud
2. Sélectionnez votre fonction
3. Cliquez sur "Edit"
4. Dans l'onglet "Runtime, build, connections and security settings"
5. Définissez la variable d'environnement `GOOGLE_APPLICATION_CREDENTIALS`
6. Vous pouvez soit:
   - Utiliser un chemin absolu vers le fichier JSON que vous avez uploadé
   - Ou mieux, utiliser Secret Manager pour stocker le contenu du fichier JSON

## Étape 4: Tester la configuration

Pour vérifier que tout fonctionne correctement, vous pouvez tester la fonction localement:

```bash
./gradlew runFunction
```

Essayez d'envoyer une requête avec des fichiers à traiter et vérifiez que l'URL signée est générée correctement dans la réponse.

## Dépannage

Si vous rencontrez des erreurs liées à l'authentification:

1. Vérifiez que le fichier JSON est accessible et lisible
2. Vérifiez que le compte de service a les permissions nécessaires sur le bucket
3. Assurez-vous que le format du bucket est correct (sans le préfixe `gs://`)
4. Consultez les logs pour plus de détails sur l'erreur

En cas d'échec persistant, la fonction utilisera une URL non signée comme solution de repli, ce qui peut fonctionner si votre bucket est configuré pour être accessible publiquement.
