package one.empty3.apps.testobject;

import one.empty3.library.Camera;
import one.empty3.library.Scene;
import one.empty3.library.ZBufferImpl;

public class TestObjet implements Runnable {
    public Scene scene;
    public ZBufferImpl z;
    private boolean running = false;
    public static final int GENERATE_IMAGE = 0;
    private Camera camera
            = new Camera();
    private boolean publish;
    private int generate = GENERATE_IMAGE;


    public ZBufferImpl z() {
        return z;
    }

    public void z(ZBufferImpl z) {
        this.z = z;
    }

    public void stop() {
    }

    public void setMaxFrames(int i) {
    }

    public void loop(boolean b) {
    }

    protected void setDimension(Resolution resolution) {

    }

    public Object camera() {
        return null;
    }

    public Scene scene() {
        return scene;
    }


    public boolean isRunning() {
        return running;
    }

    protected void setGenerate(int generateImage) {
        this.generate = generateImage;
    }

    protected void setPublish(boolean b) {
        this.publish = b;
    }

    public void afterRender() {

    }
    public Camera camera(Camera c) {
        return camera;
    }

    @Override
    public void run() {

    }
}