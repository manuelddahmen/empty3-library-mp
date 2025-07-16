gcloud functions deploy MovieGeneratorHttpFunction \
  --runtime java22 \
  --trigger-http \
  --entry-point one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction \
  --source .
