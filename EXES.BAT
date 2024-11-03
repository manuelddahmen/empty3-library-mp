set JAVA_HOME=C:\Program Files\Java\jdk-21\
set PATH=%PATH;C:\Program Files\Java\jdk-21\bin;C:\Program Files (x86)\GnuPG\bin\
set APPDIR=empty3-library-mp
set PUBLIC_PUB_DIR=jOutputBin
set GRADLE_HOME=C:\Program Files\Gradle\bin
set VERSION_STRING=version-28
mkdir %PUBLIC_PUB_DIR%
mkdir %PUBLIC_PUB_DIR%\%VERSION_STRING%

cd %APPDIR%

jpackage --main-jar empty3-library-generic.jar -i build/libs -n VideoEffects --main-class one.empty3.feature.ClassSchemaBuilder
jpackage --main-jar empty3-library-generic.jar -i build/libs -n MorphUI --main-class one.empty3.apps.MorphUI
jpackage --main-jar empty3-library-generic.jar -i build/libs -n Empty3Gui --main-class one.empty3.gui.Main
