REM gcloud auth login
REM gcloud config set project studio-6v2lo
REM gcloud functions deploy NOM_DE_VOTRE_FONCTION \
REM   --region=REGION \
REM   --runtime=RUNTIME \
REM   --trigger-http \
REM   --allow-unauthenticated
./gradlew jar extractDeps
del ./build/libs/empty3-library-mp.jar
gcloud functions deploy motion-weaver-render   --runtime java21   --trigger-http   --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction   --source ./build/libs --region us-central1 --service-account firebase-app-hosting-compute@studio-6v2lo.iam.gserviceaccount.com

gcloud projects add-iam-policy-binding motion-weaver --member="user:dathewolf@gmail.com" --role="roles/serviceusage.serviceUsageAdmin"
gcloud projects add-iam-policy-binding motion-weaver --member="user:dathewolf@gmail.com" --role="roles/cloudfunctions.developer"
gcloud projects add-iam-policy-binding motion-weaver --member="user:dathewolf@gmail.com" --role="roles/iam.serviceAccountUser"


# Grant permission to enable APIs
gcloud projects add-iam-policy-binding motion-weaver --member="user:dathewolf@gmail.com" --role="roles/serviceusage.serviceUsageAdmin"

# Grant permission to develop and manage functions
gcloud projects add-iam-policy-binding motion-weaver --member="user:dathewolf@gmail.com" --role="roles/cloudfunctions.developer"

# Grant permission to act as the function's runtime service account
gcloud projects add-iam-policy-binding motion-weaver --member="user:dathewolf@gmail.com" --role="roles/iam.serviceAccountUser"