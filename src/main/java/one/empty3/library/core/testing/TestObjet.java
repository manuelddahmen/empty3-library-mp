/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

/*
 *

/*
 * 2013-2020 Manuel Dahmen
 */
package one.empty3.library.core.testing;

import one.empty3.gui.DataModel;
import one.empty3.library.*;
import one.empty3.library.core.export.ObjExport;
import one.empty3.library.core.export.STLExport;
import one.empty3.library.core.script.ExtensionFichierIncorrecteException;
import one.empty3.library.core.script.Loader;
import one.empty3.library.core.script.VersionNonSupporteeException;
import one.empty3.libs.Image;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
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
    private static Logger logger = Logger.getLogger(TestObjet.class.getName());
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
    AWTSequenceEncoder encoder;
    Properties properties = new Properties();
    one.empty3.library.core.testing2.ShowTestResult str;
    private File avif;
    //private AVIWriter aw;
    private boolean aviOpen = false;
    public String filmName;
    private int idxFilm;
    private boolean unterminable = false;
    private long timeStart;
    private long lastInfoEllapsedMillis;
    private int generate = GENERATE_IMAGE | GENERATE_MOVIE |
            GENERATE_SAVE_IMAGE | GENERATE_SAVE_OBJ |
            GENERATE_SAVE_STL | GENERATE_SAVE_ZIP;
    private int version = 1;
    private String template = "";
    private String type = "JPEG";
    private String filenameZIP = "tests";
    private String fileextZIP = "diapo";
    private File file = null;
    private int resx = 640;
    private int resy = 480;
    private File dir = null;
    private ECImage ri;
    private String filename = "frame";
    private String fileExtension = "JPG";
    private boolean publish = true;
    private boolean isometrique = false;
    private boolean loop = true;
    private int maxFrames = 5000;
    private String text = "scene";
    private File fileScene;
    private boolean saveTxt = true;
    private String binaryExtension = "mood";
    private int serie = 0;
    private File serid = null;
    private boolean initialise;
    private boolean structure = false;
    private boolean noZoom;
    public String sousdossier;
    private boolean D3 = false;
    private ImageContainer biic;
    private ECImage riG;
    private ECImage riD;
    private File fileG;
    private File fileD;
    private boolean pause = false;
    private boolean pauseActive = false;
    private File directory;
    private ZipWriter zip;
    private boolean stop = false;
    private RegisterOutput o = new RegisterOutput();
    private int onTextureEnds = ON_TEXTURE_ENDS_STOP;
    private int onMaxFrameEvent = ON_MAX_FRAMES_STOP;
    private ExportAnimationData dataWriter;
    private File audioTrack;
    private boolean isAudioDone;
    private AudioInputStream audioIn;
    private int audioTrackNo;
    private int videoTrackNo;
    private int fps = 25;
    //private Buffer buf;
    //private ManualVideoCompile compiler;
    private boolean isVBR;
    private AudioFormat audioFormat;
    private Resolution dimension = HD1080;
    private String name;
    private FileChannelWrapper out;
    private File file0;
    private Thread threadGLafter;
    private boolean threadGLafterHasRun = false;
    private boolean LOG = false;
    private boolean running = false;
    static int numInstancesRunning = 0;

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
            z = ZBufferFactory.instance(resx, resy, D3);

        } else {
        }
    }

    /**
     * Get the ZBuffer implementation used by the class.
     *
     * @return The ZBuffer implementation used by the class
     */
    protected ZBufferImpl z() {
        return z;
    }

    public void setProperties(Properties p) {
        this.getClass();
    }

    public ExportAnimationData getDataWriter() {
        return dataWriter;
    }

    public int getIdxFilm() {
        return idxFilm;
    }

    public File getSubfolder() {
        return directory;
    }

    public void setResolution(int x, int y) {
        setResx(x);
        setResy(y);
        dimension = new Resolution(x, y);
    }

    public Image img() {
        return ri;
    }

    public void startNewMovie() {
        idxFilm++;
        avif = new File(this.dir.getAbsolutePath() + File.separator
                + sousdossier + this.getClass().getName() + "__" + filmName + idxFilm + ".AVI");

        out = null;
        encoder = null;
        try {
            out = NIOUtils.writableFileChannel(avif.getAbsolutePath());
            // for Android use: AndroidSequenceEncoder
            encoder = new AWTSequenceEncoder(out, Rational.R(getFps(), 1));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            NIOUtils.closeQuietly(out);
        }
        aviOpen = true;
    }

    private boolean unterminable() {
        return unterminable;
    }

    public boolean isAviOpen() {
        return (aviOpen && encoder != null);
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

    public RegisterOutput getO() {
        return o;
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
        scene().cameraActive(c);
        z().camera(c);
    }

    public boolean D3() {
        return D3;
    }

    public void description(String d) {
        description = d;
    }

    public File directory() {
        return directory;
    }

    protected void ecrireImage(RenderedImage ri, String type, File fichier) {
        if (fichier == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "Erreur OBJET FICHIER (java.io.File) est NULL");
            System.exit(1);
        }

        Graphics g = ((BufferedImage)((Image) ri)).getGraphics();
        g.setColor(Color.black);
        g.drawString(description, 0, 1100);

        if ((getGenerate() | GENERATE_SAVE_IMAGE) > 0) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(ri, type, baos);

                baos.flush(); // Is this necessary??
                byte[] resultImageAsRawBytes = baos.toByteArray();
                baos.close(); // Not sure how important this is...

                OutputStream out = new FileOutputStream(fichier);
                out.write(resultImageAsRawBytes);
                out.close();

                zip.addFile(fichier.getName(), resultImageAsRawBytes);

                Logger.getAnonymousLogger().log(Level.INFO, fichier.getAbsolutePath());
            } catch (Exception ex) {
            }
        }
    }

    public void exportFrame(String format, String filename) throws IOException {

        STLExport.save(
                file0 = new File(directory.getAbsolutePath() + File.separator + "stlExportFormatTXT" + filename + ".stl"),
                scene(),
                false);
        ObjExport.save(
                /*file0=*/new File(directory.getAbsolutePath() + File.separator + "objExportFormatTXT" + filename + ".obj"),
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
        o.addOutput(System.out);

        o.addOutput(Logger.getLogger(getClass().getCanonicalName()));

        if (initialise) {
            return;
        }
        c = new Camera(new Point3D(0d, 0d, -10d), Point3D.O0);

        File dir1 = null;


        Properties config = new Properties();
        try {
            config.load(new FileInputStream(System.getProperty("user.home")
                    + File.separator + "empty3.config"));


        } catch (FileNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.INFO, "userHome/empty3.config not found use default");
            config = new Properties();
            config.put("folderoutput",
                    System.getProperty("user.home")
                            + File.separator + "EmptyCanvasTest");
            try {
                config.store(new FileOutputStream(new File(System.getProperty("user.home")
                                + File.separator + "empty3.config")),
                        "Config file for empty3.one library");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (Exception ex) {
            config.setProperty("folderoutput", "./EmptyCanvasTests");
            Logger.getAnonymousLogger().log(Level.INFO, "userHome/empty3.config not found use default");
        }
        //          try {
        if (config.getProperty("folderoutput") != null) {
            dir1 = new File(config.getProperty("folderoutput"));
        } else {
            dir1 = new File("./"
                    + File.separator + "EmptyCanvas");
        }
        // } catch (IOException ex) {
        //   Logger.getAnonymousLogger().log(Level.INFO,ex.getLocalizedMessage());
        //  }
        dir1.mkdirs();

        this.dir = new File(dir1.getAbsolutePath() + File.separator
                + this.getClass().getName());
        if (!this.dir.exists()) {
            this.dir.mkdirs();
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Repertoire cree avec SUCCES");
            // System.exit(1);
        }
        serid = new File(this.dir.getAbsolutePath() + File.separator
                + "__SERID");

        sousdossier = "FICHIERS_" + dateForFilename(new Date());

        directory = new File(this.dir.getAbsolutePath() + File.separator
                + sousdossier);
        directory.mkdirs();
//        new File(directory.getAbsolutePath() + File.separator + "GAUCHE").mkdir();
//        new File(directory.getAbsolutePath() + File.separator + "DROITE").mkdir();

        ///setResolution(dimension.x(), dimension.y());
        initialise = true;
        ///publishResult(false);
        ///setMaxFrames(100);
        loop(true);

        //compiler = new ManualVideoCompile();
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
                file = new File(this.dir.getAbsolutePath() + File.separator
                        + sousdossier + File.separator + "__SERID_" + (serie)
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

    public void PAUSE() {

        pause = !pause;

    }

    /**
     * Publishes the test results if the publish flag is set to true.
     * <p>
     * This method creates a new thread to publish the test results by calling the {@link one.empty3.library.core.testing2.ShowTestResult} class,
     * passing in the necessary parameters. The publishing process includes setting the image container, the test object, and starting the thread for execution.
     * </p>
     * <p>
     * This method does not return any value.
     * </p>
     * <p>
     * <b>Note:</b> The publish flag needs to be set to true in order for the test results to be published.
     * </p>
     */
    public void publishResult() {
        if (getPublish()) {

            str = new one.empty3.library.core.testing2.ShowTestResult(ri);
            str.setImageContainer(biic);
            str.setTestObjet(this);
            new Thread(str).start();
        }
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

            RenderedImage i = ImageIO.read(is);
            Image bi = (Image) i;

            ECImage eci = new ECImage(bi);
            //biic.setImage(eci);
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }

        if (str != null)
            str.setMessage("ERROR EXCEPTION");
    }

    public void reportPause(boolean phase) {
    }

    public void reportStop() {
    }

    public void reportSuccess(File film) {
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/RENDEREDOK.png");

            if (is == null) {
                Logger.getAnonymousLogger().log(Level.INFO, "Erreur d'initialisation: pas correct!");
                System.exit(-1);
            }

            RenderedImage i = ImageIO.read(is);
            Image bi = (Image) i;

            ECImage eci = new ECImage(bi);
            //biic.setImage(eci);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if (file.exists()) {
                Desktop dt = Desktop.getDesktop();
                dt.open(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        z.scene(scene);
        //z.next();
        long timeStart = System.currentTimeMillis();

        long lastInfoEllapsedMillis = System.currentTimeMillis();
        if ((generate & GENERATE_OPENGL) > 0) {
        }
        if ((generate & GENERATE_MOVIE) > 0) {
            startNewMovie();
        }
        serid();

        this.biic = new ImageContainer();

        if (getPublish()) {
            publishResult();

        }

        File zipf = new File(this.dir.getAbsolutePath() + File.separator
                + sousdossier + File.separator + filename + ".ZIP");
        zip = new ZipWriter();

        File dataf = new File(this.dir.getAbsolutePath() + File.separator
                + filename + ".XML");

        try {
            dataWriter = new ExportAnimationData(dataf, this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            zip.init(zipf);
        } catch (FileNotFoundException e1) {
            reportException(e1);
            e1.printStackTrace();
            return;
        }

        if (LOG) {
            Logger.getAnonymousLogger().log(Level.INFO, directory().getAbsolutePath());
            Logger.getAnonymousLogger().log(Level.INFO, "Generate (0 NOTHING  1 IMAGE  2 MODEL  4 OPENGL) {0}" + getGenerate());

            Logger.getAnonymousLogger().log(Level.INFO, "Starting movie  {0}" + runtimeInfoSucc());
        }
        ginit();


        setRunning(true);
        while ((nextFrame() || unterminable()) && !stop && isRunning()) {


            byte[] audioBuffer = null;
            // Advance audio to movie time + 1 second (audio must be ahead of video by 1 second)
            while (audioTrack != null && !isAudioDone /*&& aw.getDuration(audioTrackNo).doubleValue() < 1.0
             *frame() / fps*/) {
                // => variable bit rate: format can change at any time
                audioFormat = audioIn.getFormat();
                if (audioFormat == null) {
                    break;
                }
                int asSize = audioFormat.getFrameSize();
                int asDuration = (int) (audioFormat.getSampleRate() / audioFormat.getFrameRate());
                if (audioBuffer == null || audioBuffer.length < asSize) {
                    audioBuffer = new byte[asSize];
                }
                int len = 0;
                try {
                    len = audioIn.read(audioBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (len == -1) {
                    isAudioDone = true;
                } else {

                }
            }


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
                    z.idzpp();


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
                        afterRenderFrame();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                    // ri.getGraphics().drawString(description, 0, 0);

                    if ((generate & GENERATE_MOVIE) > 0 && encoder != null && !(((generate & GENERATE_OPENGL) > 0))) {

                        try {
                            encoder.encodeImage((Image) ri);
                        } catch (IOException e) {
                            e.printStackTrace();
                            reportException(e);
                        } catch (RuntimeException e1) {
                            e1.printStackTrace();
                            reportException(e1);

                        }
                    } else {
                        if (LOG) {
                            Logger.getAnonymousLogger().log(Level.INFO,
                                    "No file open for avi writing");
                        }
                    }
                    if (ri != null && (getGenerate() & GENERATE_SAVE_IMAGE) > 0) {
                        ecrireImage(ri, type, file);

                        biic.setImage(ri != null ? ri : (frame % 2 == 0 ? riG : riD));
                        biic.setStr("" + frame);
                    }
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
                    dataWriter.writeFrameData(frame(), "Save text file: " + fout.getAbsolutePath());
                    fout = new File(this.dir.getAbsolutePath()
                            + File.separator + filename + "-description.xml");
                    final Scene scene2 = scene;
                    synchronized (scene2) {
                        DataModel dataModel = new DataModel();
                        dataModel.setScene(scene2);
                        dataModel.save(fout.getAbsolutePath());
                    }
                    dataWriter.writeFrameData(frame(), "Save bin: " + fout.getAbsolutePath());
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
                    dataWriter.writeFrameData(frame(), "Export model: " + filename);
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
                biic.setImage(ri);
                imageContainer.setImage(biic.getImage());

                str.setImageContainer(imageContainer);

                str.dessine();
            }

            z.idzpp();

        }

        setRunning(false);

        if (img() == null) {
            ri = new ECImage(getResx(), getResy(), Image.TYPE_INT_ARGB);
        } else {
            afterRender();

        }
        if (LOG) {
            Logger.getAnonymousLogger().log(Level.INFO, "" + frame() + "\n" + runtimeInfoSucc());

            Logger.getAnonymousLogger().log(Level.INFO, "Fin de la création des image et/u des modèles" + "\n" + runtimeInfoSucc());
        }
        if (zip != null) {
            try {
                zip.end();
            } catch (IOException e) {
                //reportException(e);
            }
        }
        if ((generate & GENERATE_MOVIE) > 0 && encoder != null) {
            try {
                encoder.finish();
            } catch (IOException e) {
                e.printStackTrace();
                reportException(e);

            } finally {
                NIOUtils.closeQuietly(out);
            }
        }
        String cmd;

        if (loop() && avif != null) {
            try {
                cmd = avif.getCanonicalPath();
                Runtime runtime = Runtime.getRuntime();
                if (runtime != null) {
                    runtime.exec("explorer \"" + cmd + "\"");
                    OutputStream outputStream = runtime.exec(cmd).getOutputStream();
                    Logger.getAnonymousLogger().log(Level.INFO, outputStream.toString());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                reportException(ex);
                Logger.getAnonymousLogger().log(Level.INFO, ex.getLocalizedMessage());
            }
        } else if (file.exists()) {
            try {
                cmd = file.getCanonicalPath();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("start \"" + cmd + "\"");
                OutputStream outputStream = runtime.exec(cmd).getOutputStream();
                System.out.print(outputStream);
            } catch (IOException ex) {
                ex.printStackTrace();
                reportException(ex);
                Logger.getAnonymousLogger().log(Level.INFO, ex.getLocalizedMessage());
            }
        }

        dataWriter.end();


        if (LOG) {
            Logger.getAnonymousLogger().log(Level.INFO, "End movie       " + runtimeInfoSucc());
            Logger.getAnonymousLogger().log(Level.INFO, "Quit run method " + runtimeInfoSucc());
        }
    }

    private void setRunning(boolean running) {
        this.running = true;
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

        if (str != null) {
            try {
                str.dispose();
                str.stopThreads();
                str = null;
            } catch (NullPointerException ex) {
                Logger.getAnonymousLogger().log(Level.INFO, "Can't stop thread");

            }
        }
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {

        }
        if (isAviOpen()) {
            //   try {
            // aw.finish();
            // //    aw.close();
            //   aw = null;
            aviOpen = false;
            //   } catch (IOException e) {
            //   Logger.getAnonymousLogger().log(Level.INFO,"Can't close or flush movie" + runtimeInfoSucc());
            //  }
        }

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

    public void writeOnPictureAfterZ(Image bi) {
    }

    public void writeOnPictureBeforeZ(Image bi) {
    }

    public String getFolder() {
        return dir.getAbsolutePath();
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
        this.resx = dimension.x();
        this.resy = dimension.y();
        this.dimension = dimension;
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

    public JPanel getPanelDraw() {
        return str != null ? str.getJPanel1() : null;
    }

    public ECImage getPicture() {
        return ri;
    }

    public boolean isRunning() {
        return running;
    }

}