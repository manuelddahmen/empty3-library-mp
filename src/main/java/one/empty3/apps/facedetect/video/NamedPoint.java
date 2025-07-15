package one.empty3.apps.facedetect.video;

import one.empty3.library.Point3D;

public class NamedPoint extends Point3D {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }