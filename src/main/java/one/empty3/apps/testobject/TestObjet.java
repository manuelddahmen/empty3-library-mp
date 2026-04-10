/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

/*
 *

/*
 * 2013-2020 Manuel Dahmen
 */
package one.empty3.apps.testobject;

import one.empty3.gui.DataModel;
import one.empty3.library.*;
import one.empty3.library.ImageContainer;
import one.empty3.library.core.export.ObjExport;
import one.empty3.library.core.export.STLExport;
import one.empty3.library.core.script.ExtensionFichierIncorrecteException;
import one.empty3.library.core.script.Loader;
import one.empty3.library.core.script.VersionNonSupporteeException;
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The TestObjet class provides functionality for testing objects in the library.
 *
 * @author Manuel DAHMEN
 * Created: 15-04-2014
 * Updated: 04-02-2024
 */
public abstract class TestObjet implements Test, Runnable {
    public static File configFile = new File("./empty3.config");
    protected IShowTestResult str;
    protected Class<? extends IShowTestResult> strClass = ShowTestResult.class;
    public static boolean skipInit = false;
    protected static Logger logger = Logger.getLogger(TestObjet.class.getName());
    public static final int GENERATE_NOTHING = 0;
    public static final int GENERATE_IMAGE = 1;
    public static final int GENERATE_MODEL = 2;
    public static final int GENERATE_OPENGL = 4;
    public static final int GENERATE_MOVIE = 8;
    public static final int GENERATE_OBJ = 16;
    public static final int GENERATE_NO_IMAGE_FILE_WRITING = 32;
    public static final int GENERATE_SAVE_IMAGE = 64;
    public static final int GENERATE_SAVE_XML = 128;
    public static final int GENERATE_SAVE_OBJ = 256;
    public static final int GENERATE_SAVE_STL = 512;
    public static final int GENERATE_SAVE_ZIP = 1024;
    public static final int GENERATE_LOG = 1024 * 2;
    public static final ArrayList<TestInstance.Parameter> initParams = new ArrayList<TestInstance.Parameter>();
    public static final int ON_TEXTURE_ENDS_STOP = 0;
    public static final int ON_TEXTURE_ENDS_LOOP_TEXTURE = 1;
    public static final int ON_MAX_FRAMES_STOP = 0;
    public static final int ON_MAX_FRAMES_CONTINUE = 1;
    public static final int ENCODER_MONTE = 0;
    public static final int ENCODER_HUMBLE = 1;
    public static Resolution PAL = new Resolution(1280, 720);
    public static Resolution HD720 = new Resolution(1280, 720);
    public static Resolution HD1080 = new Resolution(1920, 1080);
    public static Resolution UHD = new Resolution(1920 * 2, 1080 * 2);
    public static Resolution VGA = new Resolution(640, 480);
    public static Resolution VGA200 = new Resolution(320, 200);
    protected Scene scene = new Scene();
    protected String description = "@ Manuel Dahmen";
    protected Camera c;
    protected int frame = 0;
    protected ArrayList<TestInstance.Parameter> dynParams;
    protected ITexture couleurFond;
    protected ZBufferImpl z;
    Properties properties = new Properties();
    protected File avif;
    //protected AVIWriter aw;
    protected boolean aviOpen = false;
    public String filmName;
    protected int idxFilm;
    protected boolean unterminable = false;
    protected long timeStart;
    protected long lastInfoEllapsedMillis;
    protected int generate = GENERATE_IMAGE | GENERATE_MOVIE | GENERATE_SAVE_ZIP |
            GENERATE_SAVE_IMAGE | GENERATE_SAVE_OBJ | GENERATE_OBJ | GENERATE_SAVE_XML | GENERATE_MODEL |
            GENERATE_SAVE_STL | GENERATE_SAVE_ZIP | GENERATE_LOG;
    protected int version = 1;
    protected String template = "";
    protected String type = "JPEG";
    protected String filenameZIP = "one/empty3/test/tests";
    protected String fileextZIP = "diapo";
    protected File file = null;
    protected int resx = 640;
    protected int resy = 480;
    protected File dir = null;
    protected Image ri;
    protected String filename = "frame";
    protected String fileExtension = "PNG";
    protected boolean publish = true;
    protected boolean isometrique = false;
    protected boolean loop = true;
    protected int maxFrames = 5000;
    protected String text = "scene";
    protected File fileScene;
    protected boolean saveTxt = true;
    protected String binaryExtension = "mood";
    protected int serie = 0;
    protected File serid = null;
    protected boolean initialise;
    protected boolean structure = false;
    protected boolean noZoom;
    public String sousdossier;
    protected boolean D3 = false;
    protected one.empty3.library.ImageContainer biic;
    protected Image riG;
    protected Image riD;
    protected File fileG;
    protected File fileD;
    protected boolean pause = false;
    protected boolean pauseActive = false;
    protected boolean stop = false;
    protected int onTextureEnds = ON_TEXTURE_ENDS_STOP;
    protected int onMaxFrameEvent = ON_MAX_FRAMES_STOP;
    protected File audioTrack;
    protected boolean isAudioDone;
    protected int audioTrackNo;
    protected int videoTrackNo;
    protected int fps = 25;
    //protected Buffer buf;
    //protected ManualVideoCompile compiler;
    protected boolean isVBR;
    protected Resolution dimension = HD1080;
    protected String name;
    protected File file0;
    protected Thread threadGLafter;
    protected boolean threadGLafterHasRun = false;
    protected boolean LOG = true;
    protected boolean running = false;
    static int numInstancesRunning = 0;
    protected Object applicationContext;
    protected File androidDirData;
    protected String date;
    private ArrayList<Image> frames;
    private File subDir;

    public File getDir0() {
        return dir0;
    }

    public void setDir0(File dir0) {
        this.dir0 = dir0;
    }

    private File dir0 = new File("output" + File.separator + "frames" + File.separator + getClass().getCanonicalName());
    ;

    public void setAndroidContext(Object applicationContext) {
        if (isAndroid) {
            this.applicationContext = applicationContext;
        }
    }

    /**
     * The TestObjet class represents an object used for testing purposes.
     */
    public TestObjet() {

        init();
    }

    public TestObjet(ArrayList<TestInstance.Parameter> params) {
        init();
    }

    public TestObjet(boolean binit) {
        if (binit) {
            init();
            setResx(dimension.x());
            setResy(dimension.y());
            setDimension(new Resolution(resx, resy));
        } else {
        }
    }

    /**
     * Get the ZBuffer implementation used by the class.
     *
     * @return The ZBuffer implementation used by the class
     */
    public ZBufferImpl z() {
        return z;
    }

    public void setProperties(Properties p) {
        this.getClass();
    }


    public int getIdxFilm() {
        return idxFilm;
    }

    public File getSubfolder() {
        return configFile;
    }

    public void setResolution(int x, int y) {
        setResx(x);
        setResy(y);
        dimension = new Resolution(x, y);
    }

    public Image img() {
        return ri;
    }


    private boolean unterminable() {
        return unterminable;
    }

    public void setAviOpen(boolean aviOpen) {
        this.aviOpen = aviOpen;
    }

    public boolean getGenerate(int GENERATE) {
        return (generate & GENERATE) > 0;
    }

    private String runtimeInfoSucc() {
        System.nanoTime();

        long displayLastIntervalTimeInterval = (System.nanoTime() - lastInfoEllapsedMillis);
        long displayPartialTimeInterval = (lastInfoEllapsedMillis - timeStart);
        lastInfoEllapsedMillis = System.nanoTime();
        return "Dernier intervalle de temps : " + (displayLastIntervalTimeInterval * 1E-9) + "\nTemps total partiel : " + (displayPartialTimeInterval * 1E-9);
    }


    public abstract void afterRenderFrame();

    public String applyTemplate(String template, Properties properties) {
        return "";
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public Camera camera() {
        return scene().cameraActive();
    }

    public void camera(Camera c) {

        if (scene() != null) {
            if (z().scene() == null)
                z().scene(scene());
            scene().cameraActive(c);
            z().camera(c);
        } else {
            if (z().scene() != null)
                scene(z().scene());
            else {
                scene = new Scene();
                scene.cameraActive(c);
                z().scene(scene);
            }
            scene = new Scene();
            scene.cameraActive(c);
            z().camera(c);
        }
    }

    public boolean D3() {
        return D3;
    }

    public void description(String d) {
        description = d;
    }

    public File directory() {
        return configFile;
    }

    protected void ecrireImage(Image ri, String type, File fichier) {
        if (fichier == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "Erreur OBJET FICHIER (java.io.File) est NULL");
            System.exit(1);
        }

    }

    private static boolean isAndroid = false;

    static {
        isAndroid = isAndroidContext();
    }

    public static boolean isAndroidContext() {
        try {
            // Try to load an Android-specific class
            Class.forName("android.os.Build");
            return true; // If no exception, we're in Android
        } catch (ClassNotFoundException e) {
            // Check system properties as a fallback
            String vmVendor = System.getProperty("java.vm.vendor");
            String vmName = System.getProperty("java.vm.name");
            if ((vmVendor != null && vmVendor.toLowerCase().contains("android")) ||
                    (vmName != null && vmName.toLowerCase().contains("dalvik"))) {
                return true;
            }
            return false; // Not in Android
        }
    }

    public void exportFrame(String format, String filename) throws IOException {

        STLExport.save(
                file0 = new File(configFile.getAbsolutePath() + File.separator + "stlExportFormatTXT" + filename + ".stl"),
                scene(),
                false);
        ObjExport.save(
                /*file0=*/new File(configFile.getAbsolutePath() + File.separator + "objExportFormatTXT" + filename + ".obj"),
                scene(),
                false);
    }

    public abstract void finit() throws Exception;

    public int frame() {
        return frame;
    }

    ArrayList<TestInstance.Parameter> getDynParams() {
        return this.dynParams;
    }

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String fn) {
        this.filename = fn;
    }

    public int getGenerate() {
        return generate;
    }

    public void setGenerate(int generate) {
        this.generate = generate;
    }

    public ArrayList<TestInstance.Parameter> getInitParams() {
        return initParams;
    }

    public int getMaxFrames() {
        return maxFrames;
    }

    public void setMaxFrames(int maxFrames) {
        this.maxFrames = maxFrames;
    }


    public int getResx() {
        return resx;
    }

    @Deprecated
    public void setResx(int resx) {
        this.resx = resx;
        dimension = new Resolution(resx, resy);
        z = ZBufferFactory.instance(resx, resy, D3);
    }

    public int getResy() {
        return resy;
    }

    @Deprecated
    public void setResy(int resy) {
        this.resy = resy;
        dimension = new Resolution(resx, resy);
        z = ZBufferFactory.instance(resx, resy, D3);
    }

    public abstract void ginit();

    public void someMethod() {
        if (TestObjet.isAndroid) {
            // Code specific to Android
            Logger.getAnonymousLogger().log(Level.INFO, "Running in Android");
        } else {
            // Code specific to JVM
            Logger.getAnonymousLogger().log(Level.INFO, "Running in JVM");
        }
    }


    /**
     * Initializes the object.
     * <p>
     * This method performs the initialization of the object by setting up various parameters and creating necessary directories and files.
     * It also initializes the camera and loads the configuration properties.
     * </p>
     * <p>
     * <b>Note:</b> This method is called only once during the initialization process.
     * </p>
     */

    private void init() {

        if (skipInit) {
            dir = configFile;
            c = new Camera(new Point3D(0d, 0d, -10d), Point3D.O0);
            loop(true);
            return;
        }
        try {

            loop(true);

            if (initialise) {
                return;
            }
            c = new Camera(new Point3D(0d, 0d, -10d), Point3D.O0);

            File dir1 = null;

            boolean noConfigFile = false;

            Properties config = new Properties();
            if (!TestObjet.isAndroid) {
                configFile = new File(System.getProperty("user.home")
                        + File.separator + "empty3.config");
                try {
                    if (configFile != null && !configFile.exists()) {
                        try {
                            configFile.createNewFile();
                        } catch (RuntimeException | IOException ex) {
                            noConfigFile = true;
                        }
                    }
                    if (configFile.exists()) {
                        config.load(new FileInputStream(configFile));
                    }
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                //Use Android context
                configFile = new File(androidDirData, "empty3.config");
                //You need to pass the context to the class
                try {
                    if (configFile != null && !configFile.exists()) {
                        try {
                            configFile.createNewFile();
                        } catch (RuntimeException | IOException ex) {
                            noConfigFile = true;
                        }
                    }
                    if (configFile.exists()) {
                        config.load(new FileInputStream(configFile));
                    }
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!noConfigFile && configFile != null && configFile.exists()) {
                config = new Properties();
                config.load(new FileReader(configFile));
                config.putIfAbsent("folderoutput",
                        System.getProperty("user.home")
                                + File.separator + "EmptyCanvasTest");
                config.store(new FileOutputStream(configFile), "Config file for empty3.one library");


                dir1 = new File((String) config.get("folderoutput"));


                this.dir = new File(dir1.getAbsolutePath() + File.separator
                        + this.getClass().getName());
                if (dir1 != null) {
                    dir = dir1;
                    dir0 = dir1;
                }
                if (!this.dir.exists()) {
                    this.dir.mkdirs();
                } else {
                    Logger.getAnonymousLogger().log(Level.INFO, "Repertoire cree avec SUCCES");
                    // System.exit(1);
                }
                serid = new File(this.dir.getAbsolutePath() + File.separator
                        + "__SERID");

                sousdossier = "FICHIERS_" + dateForFilename(new Date());

                configFile = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier);
                configFile.mkdirs();
//        new File(directory.getAbsolutePath() + File.separator + "GAUCHE").mkdir();
//        new File(directory.getAbsolutePath() + File.separator + "DROITE").mkdir();
            }
            ///setResolution(dimension.x(), dimension.y());
            initialise = true;
            ///publishResult(false);
            ///setMaxFrames(100);
            loop(true);

            //compiler = new ManualVideoCompile();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initialise = true;
        loop(true);
    }

    private String dateForFilename(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(date);
    }

    public void isometrique(boolean isISO) {
        isometrique = isISO;
    }

    public void isometrique(boolean isISO, boolean noZoom) {
        this.isometrique = isISO;
        this.noZoom = noZoom;

    }

    public boolean isPause() {
        return pause;
    }

    public boolean isPauseActive() {
        return pauseActive;
    }

    private boolean isSaveBMood() {
        return !saveTxt;
    }

    public boolean isStructure() {
        return structure;
    }

    public void setStructure(boolean structure) {
        this.structure = structure;
    }

    public boolean loop() {
        return loop;
    }

    public void loop(boolean isLooping) {
        this.loop = isLooping;
    }

    public boolean nextFrame() {
        frame++;

        if (!getGenerate(GENERATE_SAVE_IMAGE))
            return true;

        if (D3()) {
            fileG = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "GAUCHE" + File.separator
                    + "__SERID_" + (serie) + "__" + filename
                    + (1000000 + frame) + "." + fileExtension);
            while (fileG == null || fileG.exists()) {
                serie++;
                fileG = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier + File.separator + "GAUCHE"
                        + File.separator + "__SERID_" + (serie) + "__"
                        + filename + (1000000 + frame) + "." + fileExtension);
            }

            fileD = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "DROITE" + File.separator
                    + "__SERID_" + (serie) + "__" + filename
                    + (1000000 + frame) + "." + fileExtension);
            while (fileD == null || fileD.exists()) {
                serie++;
                fileD = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier + File.separator + "DROITE"
                        + File.separator + "__SERID_" + (serie) + "__"
                        + filename + (1000000 + frame) + "." + fileExtension);
            }
        } else {
            file = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "__SERID_" + (serie)
                    + "__" + filename + (1000000 + frame) + "." + fileExtension);
            fileScene = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "__SERID_" + (serie)
                    + "__" + filename + (1000000 + frame) + "."
                    + binaryExtension);
            while (file == null || file.exists()) {
                serie++;

                String sub = (name == null ? sousdossier : name);
                if (!(sub.endsWith("/") || sub.endsWith("\\") || sub.endsWith(File.separator)))
                    sub = sub + File.separator;

                file = new File(this.dir.getAbsolutePath() + File.separator
                        + sub + "__SERID_" + (serie)
                        + "__" + filename + (1000000 + frame) + "."
                        + fileExtension);
                fileScene = new File(this.dir.getAbsolutePath()
                        + File.separator + sousdossier + File.separator
                        + "__SERID_" + (serie) + "__" + filename
                        + (1000000 + frame) + "." + binaryExtension);
            }
        }


        /*
         * ObjectOutputStream oos = null; try { oos = new ObjectOutputStream(new
         * FileOutputStream(serid)); oos.writeInt(serie); } catch (IOException
         * ex) { Logger.getAnonymousLogger().log(Level.INFO,
         * null, ex); } finally { try { oos.close(); } catch (IOException ex) {
         * Logger.getAnonymousLogger().log(Level.INFO, null,
         * ex); } }
         */

        return !(loop() && frame > maxFrames || (frame > 1 && !loop()));

    }

    public boolean nextFrame2UnknownDiplicate() {
        if (D3()) {
            fileG = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "GAUCHE" + File.separator
                    + "__SERID_" + (serie) + "__" + filename
                    + (1000000 + frame) + "." + fileExtension);
            while (fileG == null || fileG.exists()) {
                serie++;
                fileG = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier + File.separator + "GAUCHE"
                        + File.separator + "__SERID_" + (serie) + "__"
                        + filename + (1000000 + frame) + "." + fileExtension);
            }

            fileD = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "DROITE" + File.separator
                    + "__SERID_" + (serie) + "__" + filename
                    + (1000000 + frame) + "." + fileExtension);
            while (fileD == null || fileD.exists()) {
                serie++;
                fileD = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier + File.separator + "DROITE"
                        + File.separator + "__SERID_" + (serie) + "__"
                        + filename + (1000000 + frame) + "." + fileExtension);
            }
        } else {/*
            file = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "__SERID_" + (serie)
                    + "__" + filename + (1000000 + frame) + "." + fileExtension);
            fileScene = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + "__SERID_" + (serie)
                    + "__" + filename + (1000000 + frame) + "."
                    + binaryExtension);
            while (file == null || file.exists()) {
                serie++;
                file = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier + File.separator + "__SERID_" + (serie)
                        + "__" + filename + (1000000 + frame) + "."
                        + fileExtension);
                fileScene = new File(this.dir.getAbsolutePath()
                        + File.separator + sousdossier + File.separator
                        + "__SERID_" + (serie) + "__" + filename
                        + (1000000 + frame) + "." + binaryExtension);
            }
        */
        }
        /*
         * ObjectOutputStream oos = null; try { oos = new ObjectOutputStream(new
         * FileOutputStream(serid)); oos.writeInt(serie); } catch (IOException
         * ex) { Logger.getAnonymousLogger().log(Level.INFO,
         * null, ex); } finally { try { oos.close(); } catch (IOException ex) {
         * Logger.getAnonymousLogger().log(Level.INFO, null,
         * ex); } }
         */

        return !(loop() && frame > maxFrames || (frame > 1 && !loop()));

    }

    public void PAUSE() {

        pause = !pause;

    }


    private boolean getPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    /**
     * Sets the publish flag of the object.
     *
     * @param publish The boolean flag indicating if the test results should be published
     *                sets to false for a console mode or true for GUI process controls.
     */
    public void publishResult(boolean publish) {
        this.publish = publish;
    }

    public void reportException(Exception ex) {
        ex.printStackTrace();
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/FAILED.png");

            if (is == null) {
                Logger.getAnonymousLogger().log(Level.INFO, "Erreur d'initialisation: pas correct!");
                System.exit(-1);
            }

            ri = (Image) Image.getFromInputStream(is);

        } catch (Exception ex1) {
            ex1.printStackTrace();
        }

    }

    public void reportPause(boolean phase) {
    }

    public void reportStop() {
    }

    public void reportSuccess(File film) {
        InputStream is = getClass().getResourceAsStream(
                "/RENDEREDOK.png");

        if (is == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "Erreur d'initialisation: pas correct!");
            System.exit(-1);
        }

        Image i = (Image) Image.getFromInputStream(is);

    }

    public boolean copyResources() {
        // TODO Parcourir les textures de la scène
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    public void addAudioFile(File audio) {
        this.audioTrack = audio;
    }

    public void initCompiler() {

       /* compiler.init(avif.getAbsolutePath()
                , resx, resy, fps, 0);*/
    }

    /**
     * Runs the animation rendering process.
     * <p>
     * This method initializes the rendering environment, generates images and models, saves them to files,
     * and performs post-rendering actions such as publishing and generating movies. It also handles pausing,
     * frame advancement, audio synchronization, and error reporting.
     *
     * @throws RuntimeException if an I/O error occurs while saving files
     */
    public void run() throws RuntimeException {
        if (!initialise)
            init();



        z = ZBufferFactory.newInstance(resx, resy);
        z.setIncrementOptimizer(new ZBufferImpl.IncrementOptimizer(ZBufferImpl.IncrementOptimizer.Strategy.NONE, Math.max(resx, resy)));
        //z.next();
        long timeStart = System.currentTimeMillis();

        long lastInfoEllapsedMillis = System.currentTimeMillis();
        if ((generate & GENERATE_OPENGL) > 0) {
        }
        if ((generate & GENERATE_MOVIE) > 0) {
            createMovie();

        }
        serid();

        this.biic = new one.empty3.library.ImageContainer();

        if (getPublish()) {
            if(str==null) {
                try {
                    str = strClass.newInstance();
                    publishResult();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }


        }

        if (getGenerate(GENERATE_SAVE_IMAGE)) {
            File zipf = new File(this.dir.getAbsolutePath() + File.separator
                    + sousdossier + File.separator + filename + ".ZIP");

            File dataf = new File(this.dir.getAbsolutePath() + File.separator
                    + filename + ".XML");
        }
        subDir = new File(dir.getAbsolutePath() + File.separator + getClass().getCanonicalName() + File.separator + getInitialDate());
        if(!subDir.exists()){
            if(!subDir.mkdirs()) {
                Logger.getLogger(getClass().getCanonicalName()).severe("Cannot create dir :" + subDir.getAbsolutePath());
            } else {
                Logger.getLogger(getClass().getCanonicalName()).info("Dir created :" + subDir.getAbsolutePath());
            }
        }

        if (LOG) {
            Logger.getAnonymousLogger().log(Level.INFO, (dir != null && dir.exists() ? dir.getAbsolutePath() : " No directory"));
            Logger.getAnonymousLogger().log(Level.INFO, "Generate (0 NOTHING  1 IMAGE  2 MODEL  4 OPENGL) {0}" + getGenerate());

            Logger.getAnonymousLogger().log(Level.INFO, "Starting movie  {0}" + runtimeInfoSucc());
        }
        ginit();
        z.scene(scene);


        setRunning(true);
        while ((nextFrame() || unterminable()) && !stop && isRunning()) {


            if (frame() > getMaxFrames())
                break;
/*
            pauseActive = true;
            while (isPause()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    reportException(e);
                }
            }
            pauseActive = false;
*/

            try {
                finit();
            } catch (Exception ex) {
                ex.printStackTrace();
                reportException(ex);
            }
            if ((generate & GENERATE_OPENGL) > 0) {
                if (LOG) {
                    Logger.getAnonymousLogger().log(Level.INFO, "No OpenGL");
                }
                //str.getTestObjetJoglDrawer().setScene(scene());
            } else {
                try {
                    timeStart = System.currentTimeMillis();
                    testScene();
                    lastInfoEllapsedMillis = System.currentTimeMillis() - timeStart;
                } catch (Exception e1) {
                    reportException(e1);
                    return;
                }
            }
            if (LOG) {
                Logger.getAnonymousLogger().log(Level.INFO, "Time for frame°" + frame() + " (scene configuration: " + lastInfoEllapsedMillis / 1000f);
            }
            //Logger.getAnonymousLogger().log(Level.INFO, z.scene());

            if ((generate & GENERATE_IMAGE) > 0 && !(((generate & GENERATE_OPENGL) > 0))) {
                try {
                    if (threadGLafter != null && !threadGLafterHasRun()) {
                        threadGLafter.start();
                        threadGLafterHasRun = true;
                        threadGLafter = null;
                    }
                    z.scene(scene);
                    if (scene() != null && scene().cameraActive() != null) {
                        scene().cameraActive().declareProperties();
                        z.camera(scene().cameraActive());
                    }


                    if (LOG) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Starts rendering");
                    }
                    z.draw(scene());
                    if (LOG) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Rendering Finished");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    reportException(ex);
                }
                if (getGenerate(TestObjet.GENERATE_IMAGE) && !(((generate & GENERATE_OPENGL) > 0))) {
                    try {
                        ri = z.image2();

                        if (getGenerate(TestObjet.GENERATE_SAVE_IMAGE)) {
                            boolean pass = false;
                            try {

                                if (!pass) {
                                    File f = new File(subDir , getFilenameWoExt()+ ".png");
                                    ri.saveToFile(f.getAbsolutePath());
                                    if(f.exists()) {
                                        Logger.getAnonymousLogger().log(Level.INFO, "File written : " + f.getAbsolutePath());
                                    } else {
                                        Logger.getAnonymousLogger().log(Level.INFO, "No file written : " + f.getAbsolutePath());

                                    }
                                }
                            } catch (RuntimeException ex) {
                                ex.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            afterRenderFrame();
                        }
                    } catch (RuntimeException ex) {
                        ex.printStackTrace();
                    }
                }
                if(getGenerate(GENERATE_MOVIE)) {
                    addFrame(ri);
                }
            }
            lastInfoEllapsedMillis = System.currentTimeMillis() - timeStart;
            if (LOG) {
                Logger.getAnonymousLogger().log(Level.INFO, "Time for frame°" + frame() + " (scene rendering: " + lastInfoEllapsedMillis / 1000f);
            }
            if ((getGenerate() & GENERATE_SAVE_XML) > 0) {
                try {
                    File fout = new File(this.dir.getAbsolutePath()
                            + File.separator + filename + ".bmo");
                    new Loader().saveTxt(fout, scene);
                    fout = new File(this.dir.getAbsolutePath()
                            + File.separator + filename + "-description.xml");
                    final Scene scene2 = scene;
                    synchronized (scene2) {
                        DataModel dataModel = new DataModel();
                        dataModel.setScene(scene2);
                        dataModel.save(fout.getAbsolutePath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    reportException(e);
                    throw new RuntimeException(e);
                }
            }

            if ((generate & GENERATE_MODEL) > 0) {
                try {
                    if (LOG) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Start generating model");
                    }
                    String filename = "export-" + frame;
                    exportFrame("export", filename);
                    //dataWriter.writeFrameData(frame(), "Export model: " + filename);
                    if (LOG) {
                        Logger.getAnonymousLogger().log(Level.INFO, "End generating model");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    reportException(ex);
                    Logger.getAnonymousLogger().log(Level.INFO, ex.getLocalizedMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    reportException(ex);
                    if (LOG) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Other exception in generating model" + ex);
                    }
                }

            }

            if (publish) {
                ImageContainer imageContainer = new ImageContainer();
                StructureMatrix<Image> objectStructureMatrix = new StructureMatrix<>(0, Image.class);// Fix: Added semicolon
                objectStructureMatrix.setElem(ri);
                biic.setImage(objectStructureMatrix);
                imageContainer.setImage(biic.getImage());

                str.setImageContainer(imageContainer);

                str.dessine();
            }

            z.idzpp();


            z.idzpp();

        }

        setRunning(false);




        if (img() == null) {
            ri = new Image(getResx(), getResy());
            for (int x = 0; x < getResx(); x++) {
                for (int y = 0; y < getResy(); y++) {
                    ri.setRgb(x, y, 0x00ff0000);
                }
            }

        } else {
            afterRender();

        }

        if(getGenerate(GENERATE_MOVIE)) {
            endMovie();
        }


        if (LOG) {
            Logger.getAnonymousLogger().log(Level.INFO, frame() + "\n" + runtimeInfoSucc());

            Logger.getAnonymousLogger().log(Level.INFO, "Fin de la création des image et/u des modèles" + "\n" + runtimeInfoSucc());
        }


        if (LOG) {
            Logger.getAnonymousLogger().log(Level.INFO, "End movie       " + runtimeInfoSucc());
            Logger.getAnonymousLogger().log(Level.INFO, "Quit run method " + runtimeInfoSucc());
        }
    }

    private void endMovie() {
        Process chperm;
        try {
            for (int i = 0; i < frames.size(); i++) {
                frames.get(i).saveToFile(String.format("/tmp/img%09d.jpg", i));
            }


            File f = new File(subDir, getFilenameWoExt() + ".png");


            if (isAndroid) {
                chperm = Runtime.getRuntime().exec("su");
                DataOutputStream os =
                        new DataOutputStream(chperm.getOutputStream());

                os.writeBytes("ffmpeg -f image2 -i /tmp/img%09d.jpg " + f.getAbsolutePath() + "\n");


                os.flush();

                chperm.waitFor();
            } else {
                chperm = Runtime.getRuntime().exec("ffmpeg -f image2 -i /tmp/img%09d.jpg " + f.getAbsolutePath() + "\n");


                chperm.waitFor();

            }


        } catch (IOException | InterruptedException e) {
            if (e.getMessage().contains("Cannot run program \"ffmpeg\"")) {
                System.out.println("choco install ffmpeg");
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void addFrame(Image ri) {
        frames.add(ri);
    }

    private void createMovie() {
        frames = new ArrayList<Image>();
    }

    private File getNewDirectory() {
        return dir;
/*        File dirFiles = new File(dir.getAbsolutePath() + File.separator +
                getClass().getCanonicalName() + File.separator + "frames_" + getInitialDate() + File.separator + getClass().getCanonicalName() + "__" + frame());
        if (!dirFiles.exists())
            dirFiles.getParentFile().mkdirs();*./
        return dirFiles;*/
    }
    private String getFilenameWoExt() {
        return "frame_" + String.format("%08d", frame());
   }

    private String getInitialDate() {
        if (date == null)
            date = dateForFilename(new Date());
        return date;
    }

    public void setRunning(boolean running) {
        this.running = running;
        numInstancesRunning += running ? 1 : 0;
    }

    private boolean threadGLafterHasRun() {
        return threadGLafterHasRun;
    }

    public void saveBMood(boolean b) {
        saveTxt = b;
    }

    /**
     * Returns the scene associated with this object.
     *
     * @return the scene associated with this object
     */
    public Scene scene() {
        return scene;
    }

    public void paintingAct(Representable representable, PaintingAct pa) {
        representable.setPaintingAct(getZ(), scene(), pa);
    }

    public void closeView() {

    }

    /***
     * Sets the scene associated with this object.
     *
     * @param load The scene to be set
     */
    public void scene(Scene load) {
        scene = load;
    }

    private int serid() {
        return 0;
    }

    public void set3D(boolean b3D) {
        this.D3 = b3D;

    }

    public void setCouleurFond(ITexture tColor) {
        this.couleurFond = tColor;
    }

    public boolean setDynParameter(TestInstance.Parameter parameter) {
        Iterator<TestInstance.Parameter> prms = dynParams.iterator();

        while (prms.hasNext()) {
            TestInstance.Parameter prm = prms.next();

            if (parameter.name.equals(prm.name)) {
                dynParams.remove(prm);
                dynParams.add(prm);
                return true;
            }
        }
        dynParams.add(parameter);
        return true;
    }

    public void setFileExtension(String ext) {
        this.fileExtension = ext;
    }

    public void stop() {
        stop = true;
        setGenerate(GENERATE_NOTHING);
        setRunning(false);
    }

    /**
     * Represents a test scene in the TestObjet class.
     *
     * <p>
     * This abstract method should be implemented by subclasses to define the specific test scene logic.
     * It is responsible for performing all the necessary steps required for the test,
     * such as initializing the object, setting up parameters, generating images or models,
     * and handling other specific operations as needed.
     * </p>
     *
     * <p>
     * This method can throw an Exception if any error occurs during the test scene execution.
     * </p>
     *
     * @throws Exception if an error occurs during the test scene execution.
     */
    public abstract void testScene() throws Exception;

    public void testScene(File f) throws Exception {

        if (f.getAbsolutePath().toLowerCase().endsWith("mood")
                || f.getAbsolutePath().toLowerCase().endsWith("moo")
                || f.getAbsolutePath().toLowerCase().endsWith("bmood")
                || f.getAbsolutePath().toLowerCase().endsWith("bmoo")) {
            try {
                new Loader().load(f, scene);

            } catch (VersionNonSupporteeException | ExtensionFichierIncorrecteException ex) {
                Logger.getAnonymousLogger().log(Level.INFO, ex.getLocalizedMessage());
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Erreur: extension incorrecte");
            System.exit(1);

        }
    }


    /**
     * Sets the unterminable flag of the object.
     *
     * @param b the boolean flag indicating if the object is unterminable
     */
    public void unterminable(boolean b) {
        unterminable = b;
    }

    /**
     * Get the ZBuffer implementation used by the class.
     *
     * @return The ZBuffer implementation used by the class
     */
    public ZBuffer getZ() {
        if (z == null)
            z = ZBufferFactory.instance(resx, resy, D3);
        return z;
    }

    public void onTextureEnds(ITexture texture, int texture_event) {
        texture.onTextureEnds = texture_event;
    }

    public void onMaxFrame(int maxFramesEvent) {
        this.onMaxFrameEvent = maxFramesEvent;
    }

    public TestObjet getInstance() throws ClassNotFoundException {
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new ClassNotFoundException("Impossible to initialize class");
    }

    public Resolution getDimension() {
        return dimension;
    }

    public void setDimension(Resolution dimension) {
        if (z() != null) {
            z = z();
            Scene scene1 = z().scene();
            if (scene1 == null) scene1 = scene();
            Camera camera = camera();
            z().scene(scene1);
            z().camera(camera);
            this.resx = dimension.x();
            this.resy = dimension.y();
            this.dimension = dimension;
            z().camera(camera);
            z().scene(scene1);
            setZ(new ZBufferImpl(resx, resy));
        } else {
            this.resx = dimension.x();
            this.resy = dimension.y();
            this.dimension = dimension;
            setZ(new ZBufferImpl(resx, resy));
            //z().camera(camera());
            z().scene(scene() != null ? scene() : new Scene());
            z().camera(camera() != null ? camera() : new Camera());
        }
        z.setIncrementOptimizer(new ZBufferImpl.IncrementOptimizer(ZBufferImpl.IncrementOptimizer.Strategy.NONE, 1000));
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color v2main() {
        return null;
    }

    public File getWrittenFile() {
        return file0;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setZ(ZBufferImpl z) {
        this.z = z;
    }

    public File getDir() {
        return dir;
    }

    public void setThreadGLafter(Thread thread) {
        this.threadGLafter = thread;
    }


    public Image getPicture() {
        return (Image) ri;
    }

    public boolean isRunning() {
        return running;
    }

}
