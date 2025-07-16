package one.empty3.apps.facedetect.video;

public class Point {
        private String id;
        private String namee;
        private double x;
        private double y;
        private String color;
        private boolean visible;
        private String imageUrl;

        public String getColor() {
                return color;
        }

        public void setColor(String color) {
                this.color = color;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getImageUrl() {
                return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }

        public String getNamee() {
                return namee;
        }

        public void setNamee(String namee) {
                this.namee = namee;
        }

        public boolean isVisible() {
                return visible;
        }

        public void setVisible(boolean visible) {
                this.visible = visible;
        }

        public double getX() {
                return x;
        }

        public void setX(double x) {
                this.x = x;
        }

        public double getY() {
                return y;
        }

        public void setY(double y) {
                this.y = y;
        }
}
