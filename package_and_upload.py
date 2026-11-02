import os
import re
import subprocess
import glob
#gcloud projects create dev-buckets
#gcloud config set project dev_buckets
#gcloud projects add-iam-policy-binding dev_buckets --member="user:dathewolf@gmail.com" --role=ROLE
#gcloud storage buckets create gs://exes/ --uniform-bucket-level-access
# Configuration
SOURCE_DIRS = ["src/main/java"]
MAIN_CLASSES_FILE = "main_classes.txt"
OUTPUT_DIR = "build/executables"
GCS_BUCKET_DIR = "gs://exes/empty3-library-mp/executables/"
MAIN_JAR = "build/libs/empty3-library-mp.jar" # Adjust based on your build output

def find_main_classes():
    """Step 1: Scan .java files for main methods and write to main_classes.txt"""
    print("-> Scanning for main classes...")
    main_classes = []

    pattern = re.compile(r'public\s+static\s+void\s+main\s*\(\s*String\s*\[\s*\]\s*\w+\s*\)', re.MULTILINE)
    package_pattern = re.compile(r'^\s*package\s+([\w.]+)\s*;', re.MULTILINE)
    class_pattern = re.compile(r'^\s*(?:public\s+)?(?:abstract\s+)?class\s+(\w+)', re.MULTILINE)

    for src_dir in SOURCE_DIRS:
        if not os.path.exists(src_dir):
            continue
        for root, _, files in os.walk(src_dir):
            for file in files:
                if file.endswith(".java"):
                    file_path = os.path.join(root, file)
                    try:
                        with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                            content = f.read()

                        if pattern.search(content):
                            pkg_match = package_pattern.search(content)
                            class_match = class_pattern.search(content)

                            if class_match:
                                class_name = class_match.group(1)
                                if pkg_match:
                                    canonical_name = f"{pkg_match.group(1)}.{class_name}"
                                else:
                                    canonical_name = class_name
                                main_classes.append(canonical_name)
                    except Exception as e:
                        print(f"Error reading {file_path}: {e}")

    main_classes = sorted(list(set(main_classes)))
    with open(MAIN_CLASSES_FILE, "w", encoding="utf-8") as f:
        for cls in main_classes:
            f.write(cls + "\n")

    print(f"-> Found {len(main_classes)} main classes. Written to {MAIN_CLASSES_FILE}")
    return main_classes

def package_executables(main_classes):
    """Step 2: Package each main class into an executable using jpackage"""
    print("-> Packaging executables with jpackage...")
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    if not os.path.exists(MAIN_JAR):
        print(f"Warning: Main JAR {MAIN_JAR} not found. Make sure to build the project first (e.g. .\gradlew jar).")
        return

    for canonical_name in main_classes:
        class_simple_name = canonical_name.split('.')[-1]
        app_name = f"{class_simple_name}"
        destination = os.path.join(OUTPUT_DIR, app_name)

        print(f"Packaging {canonical_name} -> {app_name}.exe")

        cmd = [
            "jpackage",
            "--type", "app-image",
            "--name", app_name,
            "--input", "build/libs",
            "--main-jar", os.path.basename(MAIN_JAR),
            "--main-class", canonical_name,
            "--dest", OUTPUT_DIR
        ]

        try:
            subprocess.run(cmd, check=True)
            print(f"Successfully packaged {app_name}")
        except subprocess.CalledProcessError as e:
            print(f"Failed to package {canonical_name}: {e}")

def upload_to_gcs():
    """Step 3: Upload generated executables to Google Cloud Storage"""
    print("-> Uploading executables to GCS...")
    if not os.path.exists(OUTPUT_DIR):
        print(f"Output directory {OUTPUT_DIR} does not exist.")
        return

    cmd = ["gcloud", "storage", "cp", "-r", os.path.join(OUTPUT_DIR, "*"), GCS_BUCKET_DIR]
    try:
        subprocess.run(cmd, check=True)
        print(f"Successfully uploaded executables to {GCS_BUCKET_DIR}")
    except subprocess.CalledProcessError as e:
        print(f"Failed to upload to GCS: {e}")

if __name__ == "__main__":
    print("Starting Packaging & Upload Tool Script...")
    classes = find_main_classes()
    if classes:
        package_executables(classes)
        upload_to_gcs()
    print("Done!")