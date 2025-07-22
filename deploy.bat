REM gcloud auth login
REM gcloud config set project studio-6v2lo
REM gcloud functions deploy NOM_DE_VOTRE_FONCTION \
REM   --region=REGION \
REM   --runtime=RUNTIME \
REM   --trigger-http \
REM   --allow-unauthenticated
call ./gradlew jar extractDeps
call del ./build/libs/empty3-library-mp.jar
call gcloud functions deploy motion-weaver-render   --runtime java21   --trigger-http   --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction   --source ./build/libs --region us-central1
