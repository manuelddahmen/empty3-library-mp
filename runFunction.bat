gcloud functions deploy MovieGeneratorHttpFunction \
  --runtime java22 \
  --trigger-http \
  --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction \
  --source .
gcloud functions deploy motion-weaver-render   --runtime java21   --trigger-http   --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction   --source ./build/libs --region us-central1 --service-account firebase-app-hosting-compute@studio-6v2lo.iam.gserviceaccount.com --memory 4GiB --max-instances 1 --allow-unauthenticated
