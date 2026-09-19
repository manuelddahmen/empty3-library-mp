REM combine username and password (on maven) : https://mixedanalytics.com/tools/basic-authentication-generator/
call ./gradlew jreleaserConfig --no-daemon
call ./gradlew clean --no-daemon
call ./gradlew -x test publish --no-daemon
call ./gradlew -x test jreleaserFullRelease --no-daemon
REM call 7z.exe a "./build/staging-deploy/one.zip" "./build/staging-deploy/one/"
REM cmd curl.exe --request POST  --verbose --header 'Authorization: Bearer S0JkMUVvaEU6RldzNmkxSVMzYXZ3cStSeXZlcFlTQkFZbDB5eHVDem1GRGtMdTAzNVJZc2k=' --form bundle=@one.zip https://central.sonatype.com/api/v1/publisher/upload
