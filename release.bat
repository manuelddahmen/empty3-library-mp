REM combine username and password (on maven) : https://mixedanalytics.com/tools/basic-authentication-generator/
call ./gradlew jreleaserConfig
call ./gradlew clean
call ./gradlew publish
call ./gradlew jreleaserFullRelease
