REM gcloud auth login
REM gcloud config set project studio-6v2lo
REM gcloud functions deploy NOM_DE_VOTRE_FONCTION \
REM   --region=REGION \
REM   --runtime=RUNTIME \
REM   --trigger-http \
REM   --allow-unauthenticated
./gradlew jar extractDeps
del ./build/libs/empty3-library-mp.jar
gcloud functions deploy motion-weaver-render   --runtime java21   --trigger-http   --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction   --source ./build/libs --region us-central1
