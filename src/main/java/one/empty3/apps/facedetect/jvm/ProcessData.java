package one.empty3.apps.facedetect.jvm;

import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class ProcessData implements Runnable {
    boolean isRunning = true;
    EditPolygonsMappings editPolygonsMappings;
    Map<String, byte[]> data;
    private int count = 0;

    /***
     * Constructor
     * @param data POST data encoded as String and Base64 for files
     */
    public ProcessData(Map<String, byte[]> data) {
        this.data = data;
    }

    @Override
    public void run() {

    }
}
