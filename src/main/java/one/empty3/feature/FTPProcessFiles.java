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

package one.empty3.feature;

import one.empty3.FFMpeg;
import one.empty3.feature.DiffEnergy;
import one.empty3.feature.ProcessBean;
import one.empty3.io.ProcessFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FTPProcessFiles {
    public static String classnames, classname;
    public static String directory = "./images/m";
    static String[] classes;
    static String settingsPropertiesPath = System.getProperty("user.home") + File.separator + "EmptyCanvasTest" + File.separator + "setsEffects.properties";
    static String currentDirout;
    static String[] currentDirin;
    static ProcessFile processInstance;
    static String directoryOut;
    static FTPClient ftpClient;
    private static int maxRes;
    private static int maxFilesInDir;
    private static String[] initialDirectories;
    private static HashMap<File, List<File>> listsFiles = new HashMap<>();
    private static Properties appSettings;
    private static File tempDir;
    private static File ffmpegExe;
    private static ArrayList<File> movies = new ArrayList<>();
    private static int stepIndex;
    private static int inDirectoryIndex;

    public static String getDirname(String s) {
        if (s.contains("/"))
            return s.substring(0, s.lastIndexOf("/"));
        return s;
    }

    public static Properties settings() {
        appSettings = new Properties();
        try {
            appSettings.load(
                    new FileInputStream(
                            settingsPropertiesPath));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return appSettings;
    }

    public static Properties setSettings(String settingsDir) {
        Properties settings = new Properties();
        try {
            settings.load(
                    new FileInputStream(
                            settingsDir + File.separator + "settings.properties"));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Configuration file = " + settingsDir + " doessn't content Settings.properties");
            System.exit(1);
        }
        return settings;
    }

    public static void loadArgsProps(String propFile) {
    }

    /*
        try {
            Method m = processInstance.class.getDeclaredMethod(argCl, argValue);
        Object rv = m.invoke(processInstance);
        Logger.getAnonymousLogger().log(Level.INFO, rv);
            }
        catch (NoSuchMethodException,
            InvocationTargetException, IllegalAccessException
            }*/

    public static void main(String[] args) {
        int j = 0;

        appSettings = settings();

        if (appSettings.getProperty("sets").equals("*")) {
            Logger.getAnonymousLogger().log(Level.INFO, "All default tests");
            settingsPropertiesPath = appSettings.getProperty("working") + File.separator + "sets";
        } else {
            settingsPropertiesPath = "./sets";
        }
        String[] sets = new File(settingsPropertiesPath)
                .list();
        while (j < Objects.requireNonNull(sets).length) {
            String settingsPropertiesPath1 = settingsPropertiesPath + File.separator + sets[j];


            try {
                Logger.getAnonymousLogger().log(Level.INFO, "Settings of effects path " + settingsPropertiesPath);
                Logger.getAnonymousLogger().log(Level.INFO, "Settings of output" + settingsPropertiesPath);
                defaultProcess(new File(settingsPropertiesPath1));
                Logger.getAnonymousLogger().log(Level.INFO, "Process %s ran without error");
            } catch (Exception ex) {
                Logger.getAnonymousLogger().log(Level.INFO, "Process %s ran with exception");
                ex.printStackTrace();
            }
            j++;
        }

    }

    public static void parseAndSet(ProcessFile processInstance, List<Object> argCl) {
        if (argCl.size() % 3 == 0) {
            for (int j = 0; j < argCl.size(); j += 3) {
                //Class param = Class.forName(argCl.get(j));
                String param = (String) argCl.get(j);
                String propertyName = (String) argCl.get(j + 1);
                String argValue = (String) argCl.get(j + 2);
                try {
                    Method m = processInstance.getClass().getMethod("set" + propertyName, argValue.getClass());
                    m.invoke(processInstance, "set" + propertyName, argValue);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public static void defaultProcess(File settingsDirectory) {
        Logger.getAnonymousLogger().log(Level.INFO, "arg 0 : dir0 or ftp1 dir path");
        Logger.getAnonymousLogger().log(Level.INFO, "arg 1 : one.empty3.io.ProcessFile class");
        Logger.getAnonymousLogger().log(Level.INFO, "arg 2 : dir0 or ftp1 dir output");

        try {
            DiffEnergy.pw = new PrintWriter("." + File.separator + "energies.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // if(path==null) {
        //      System.exit(-1);
        //  }

        // Properties set = defProcess(args[0]);

        Properties settings = setSettings(settingsDirectory.getAbsolutePath());


        String server;
        int port;
        String username;
        String password;


        currentDirin = settings.getProperty("in.directory").split(",");
        switch (settings.getProperty("in.device")) {
            case "local":
                server = "file";
                port = 0;
                username = "";
                password = "";
                break;
            default:
                server = (String) settings.getProperty("host");
                port = Integer.parseInt(settings.getProperty("port"));
                username = (String) settings.getProperty("username");
                password = (String) settings.getProperty("password");
                break;
        }

        String maxFilesInDir0 = settings.getProperty("maxFilesInDir");
        FTPProcessFiles.maxFilesInDir = Integer.parseInt(maxFilesInDir0 == null ? "10000" : maxFilesInDir0);
        String maxRes = settings.getProperty("maxRes");
        if (maxRes != null)
            FTPProcessFiles.maxRes = Integer.parseInt(maxRes);
        else
            FTPProcessFiles.maxRes = 0;
        /* String*/
        classnames = (String) settings.getProperty("classname");
        String class0 = (String) settings.getProperty("class0");
        directoryOut = (String) settings.getProperty("out.directory");

        Properties globals = null;
        String tempDir;

        try {
            globals = new Properties();
            globals.load(new FileInputStream("settings.properties"));
            tempDir = globals.getProperty("tempDir");
        } catch (IOException e) {
            tempDir = "temp/";
            e.printStackTrace();
        }

        if (tempDir == null)
            tempDir = "temp/" + directoryOut;


        new File(tempDir).mkdirs();

        //directoryOut = tempDir;
        String sep = "";
        stepIndex = 0;
        //currentDirin = "";
        if (class0 == null || class0.equals("")) {
            sep = "";
        } else sep = ",";
/*
        classnames = (classnames != null ?
                classnames + sep : "")

                + (class0 == null ? "" : "," + classnames);
*/
        String[] classnamesArr = classnames.split(",");

//        for(String inputDir : currentDirin) {
        inDirectoryIndex = 0;
        for (String classname2 : classnamesArr) {
            try {
                classname = classname2;
                if (stepIndex > 0)
                    currentDirin[inDirectoryIndex] = currentDirout;


                currentDirout = "" + directoryOut + "-" + stepIndex + "-" + classname + "/";
                Logger.getLogger(FTPProcessFiles.class.getName()).info("Process class name read " + classname);
                Logger.getLogger(FTPProcessFiles.class.getName()).info("Directory out: " + currentDirout);
                Class clazz = Class.forName(classname);


                Logger.getLogger(FTPProcessFiles.class.getName()).info("Process Dir" + classname2);

                Object o = clazz.newInstance();

                if (o instanceof ProcessFile)

                    processInstance = (ProcessFile) o;

                String arg = null;

                List<Object> argCl = new ArrayList();

                if ((arg = (String) (settings.getProperty(classname))) != null) {

                    String[] ar = arg.split(":");

                    argCl.addAll(Arrays.asList(ar));

                }

                parseAndSet(processInstance, argCl);

                processInstance.setProperty(settings);

                processInstance.setOutputDirectory(new File(directoryOut + File.separator + "outputFiles"));

                if (stepIndex == 0) {


                    if (server.startsWith("ftp")) {


                        ftpClient = new FTPClient();
                        ftpClient.connect(server, port);
                        showServerReply(ftpClient);

                        int replyCode = ftpClient.getReplyCode();
                        if (!FTPReply.isPositiveCompletion(replyCode)) {
                            Logger.getAnonymousLogger().log(Level.INFO, "Connect failed");
                            return;
                        }
                        // reads settings.xml or prompts user/pass
                        boolean success = ftpClient.login(username, password);
                        showServerReply(ftpClient);

                        if (!success) {
                            Logger.getAnonymousLogger().log(Level.INFO, "Could not login to the server");
                            return;
                        }

                        ftpClient.enterLocalPassiveMode();


                        // Lists files and directories
                        ftpClient.changeWorkingDirectory(directory);
                        showServerReply(ftpClient);

                        FTPFile[] files1 = ftpClient.listFiles(directory);
                        showServerReply(ftpClient);

                        List<ProcessBean> processBeans = ProcessBean.processBeanList(files1);

                        printFileDetails(processBeans, directory);
                    } else if (server.startsWith("http")) {
                        URL oracle = new URL(server);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(oracle.openStream()));

                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                            ;
                        in.close();


                    } else {
                        // local path
                        initialDirectories = currentDirin;
                        Logger.getAnonymousLogger().log(Level.INFO, "Settings of images");
                        Arrays.stream(initialDirectories).forEach(System.out::println);
                        for (int d = 0; d < initialDirectories.length; d++) {
                            File directory = new File(initialDirectories[d]);
                            if (directory.exists()) {
                                printFileDetails(Objects.requireNonNull(directory.list()), initialDirectories[d]);

                            }
                        }

                    }

                } else {

                    Logger.getAnonymousLogger().log(Level.INFO, "effect" + processInstance.toString());

                    Logger.getAnonymousLogger().log(Level.INFO, "I>0 classes de traitement\nClasse : " + clazz.toString() + " : " + currentDirin[inDirectoryIndex]);

                    String currentDirin1 = currentDirin[0];
                    File file = new File(currentDirin1);
                    if (file.exists() && file.isDirectory())
                        printFileDetails(Objects.requireNonNull(file.list()), currentDirin1);

                }


                stepIndex++;
                //                  index++;
 /*
            // uses simpler methods
            String[] files2 = ftpClient.listNames(directory);
            printNames(files2);
 */


            } catch (Exception ex) {
                Logger.getAnonymousLogger().log(Level.INFO, "Oops! Something wrong happened");
                ex.printStackTrace();
            } finally {
                // logs out and disconnects from server
                if (ftpClient != null) {
                    try {
                        if (ftpClient.isConnected()) {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
//            }
        }

        for (File movie : movies) {
            FFMpeg ffMpeg = new FFMpeg("", movie);
            File[] outputFolderList = ffMpeg.extraireImagesJpg();
            System.out.printf("outputFolderList[] length = %d\n", outputFolderList.length);
            FFMpeg ffMpeg2 = null;
            for (File file : outputFolderList) {
                ffMpeg2 = new FFMpeg("", file);
                for (stepIndex = 0; stepIndex < classnamesArr.length; stepIndex++) {
                    try {
                        processInstance = (ProcessFile) Class.forName(classnamesArr[stepIndex]).newInstance();
                        assert maxRes != null;
                        processInstance.setMaxRes(Integer.parseInt(maxRes));
                        System.out.printf("%s Process STARTS\n", processInstance.getClass().getName());
                        ffMpeg2.process(processInstance);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (ffMpeg2 != null)
                ffMpeg.encoderImagePngMpg();//ffMpeg2.getOutputFolder()
        }
    }

    public static void process(FTPFile object, String remote) {
        if (object.isFile()) {
            try {


                File fi = new File(currentDirout + "/" + object.getName());
                File fo = new File(currentDirout + "/" + object.getName());


                new File(getDirname(fi.getAbsolutePath())).getParentFile().mkdirs();
                new File(getDirname(fo.getAbsolutePath())).getParentFile().mkdirs();

                Logger.getLogger(FTPProcessFiles.class.getName()).info("fi" + fi.getAbsolutePath());
                Logger.getLogger(FTPProcessFiles.class.getName()).info("fo" + fo.getAbsolutePath());
                fi.createNewFile();
                fo.createNewFile();

                FileOutputStream fos =
                        new FileOutputStream(fi.getAbsolutePath());

                ftpClient.retrieveFile(remote, fos);

                Logger.getLogger(FTPProcessFiles.class.getName()).info("file  in : " + fi.getAbsolutePath());
                Logger.getLogger(FTPProcessFiles.class.getName()).info("file out : " + fo.getAbsolutePath());
                Logger.getLogger(FTPProcessFiles.class.getName()).info("process ftpfile  : " + processInstance.getClass().getName());

                //Thread thread = new Thread(() -> {
                processInstance.process(fi, fo);
                processInstance.bean.setImage(fo);
                processInstance.addSource(fo);
                energy(fo);
                //});
                //new TimerKillThread(thread);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void energy(File image) {
       /* try {
            DiffEnergy diffEnergy = new DiffEnergy(ImageIO.read(image), pw);
            for(int i=0; i<diffEnergy.columns; i++)
                for(int j=0; j<diffEnergy.lines; j++) {
                    diffEnergy.filter(i, j);
                }
            diffEnergy.end(image.getAbsolutePath());
        
*/
    }

    public static void process(File object) {

        if (object.isFile()) {
            if (object.getName().endsWith(".mpg")
                    || object.getName().endsWith(".mp4")
                    || object.getName().endsWith(".avi")) {
                movies.add(object);
                return;
            }


            File fi = object;
            File fo = new File(currentDirout + "/" + stepIndex + "-" + inDirectoryIndex + "-" + object.getName());


            new File(getDirname(fi.getAbsolutePath())).getParentFile().mkdirs();
            new File(getDirname(fo.getAbsolutePath())).getParentFile().mkdirs();
            try {
                fi.createNewFile();
                fo.createNewFile();
                Logger.getLogger(FTPProcessFiles.class.getName()).info("file  in : " + fi.getAbsolutePath());
                Logger.getLogger(FTPProcessFiles.class.getName()).info("file out : " + fo.getAbsolutePath());
                processInstance.setMaxRes(FTPProcessFiles.maxRes);
                Logger.getLogger(FTPProcessFiles.class.getName()).info("process file  : " + processInstance.getClass().getName());

                // Thread thread = new Thread(() -> {
                if (!fi.exists()) {
                    System.err.println("Error file IN doesn't exist");
                    System.err.println(fi.getAbsolutePath());
                    System.exit(1);
                }
                processInstance.process(fi, fo);
                processInstance.addSource(fo);
                energy(fo);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private static void printFileDetails(List<ProcessBean> files, String directory) {
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int it = 0;
        for (ProcessBean bean : files) {
            if (it++ > maxFilesInDir)
                return;

            if (bean.ftp) {
                FTPFile file = bean.ftpFile;
                if (file.isFile() && !file.getName().equals(".")
                        && !file.getName().equals("..")
                ) {
                    String filePath = "";
                    String remoteFile = directory + "/" + file.getName();
                    //Logger.getLogger(getClass()).info(file.getName());
                    //Logger.getAnonymousLogger().log(Level.INFO, file.getName() + " "+ remote);


                    process(file, remoteFile);
                }
            }
        }
    }

    private static void printFileDetails(String[] files, String directory) {
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int it = 0;
        for (String f : files) {
            if (it > maxFilesInDir)
                return;
            File file = new File(directory + File.separator + f);
            if (file.isFile() && !file.getName().equals(".")
                    && !file.getName().equals("..")
            ) {
                String filePath = directory + "/" + classname + "/" + file.getName();

                //Logger.getLogger(getClass()).info(file.getName());

                List<File> files1 = searchFile(file);

                Logger.getAnonymousLogger().log(Level.INFO, file.getName());

                if (file.exists() && (file.getName().endsWith("mpg") || file.getName().endsWith("mp4")
                        || file.getName().endsWith("avi"))) {
                    movies.add(file);

                }

                if (files1 == null) {
                    listsFiles.put(file, new ArrayList<>());
                    files1 = listsFiles.get(file);
                    files1.add(file);
                    processInstance.setStack(files1);
                } else {
                    files1.add(file);
                    processInstance.setStack(files1);
                }

                process(file);
            }/* else {
                Logger.getAnonymousLogger().log(Level.INFO, "error file in not found");
                System.exit(-1);
            }*/

            it++;
        }

    }

    private static List<File> searchFile(File file1) {
        final List<File>[] files1 = new List[]{null};
        listsFiles.forEach((file, files) -> {
            if (file.getName().equals(file1.getName()))
                files1[0] = files;
        });
        return files1[0];
    }

    private static void printFileDetailsProcessOnce(File mpeg) {
        FFMpeg ffMpeg = new FFMpeg("", mpeg);

        ffMpeg.extraireImagesJpg();

        for (File frame : Objects.requireNonNull(tempDir.listFiles())) {
            Logger.getAnonymousLogger().log(Level.INFO, "process file "
                    + frame.getAbsolutePath());

            ffMpeg.process(processInstance);
        }
        ffMpeg.encoderImagePngMpg();
        /*
        TextureMov t = new TextureMov(mpeg.getAbsolutePath());
        List<File> list = new ArrayList<>();
        // extraire les images
        int findex = 0;
        Image image = null;
        int i = 0;

        t.timeNext();

        do {


            File ftmp = new File(mpeg.getAbsolutePath() + "---" + (findex++) + ".jpg");
            image = t.getImage();
            if (image == null) {
                Logger.getAnonymousLogger().log(Level.INFO, "Movie frame == null");
                continue;
            }
            try {
                ImageIO.write(image, "JPEG", ftmp);
            } catch (Exception ex) {
                Logger.getAnonymousLogger().log(Level.INFO, "error writing movie frame");
                ex.printStackTrace();

            }
            //list.add(ftmp);

            Logger.getAnonymousLogger().log(Level.INFO, "frame no" + (i++));
        } while (t.nextFrame());

        //  File[] files = new File[list.size()];

        for (File file : list) {
            Logger.getAnonymousLogger().log(Level.INFO, "process file "
                    + file.getAbsolutePath());
            process(file);
        }
*/
    }

    private static void printNames(String files[]) {
        if (files != null && files.length > 0) {
            int it = 0;
            for (String aFile : files) {
                if (it++ > maxFilesInDir)
                    return;
                Logger.getAnonymousLogger().log(Level.INFO, aFile);
            }
        }
    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                Logger.getAnonymousLogger().log(Level.INFO, "SERVER: " + aReply);
            }
        }
    }

    public String[] split(String array) {
        String[] split = array.split(",");
        return split;
    }

}
