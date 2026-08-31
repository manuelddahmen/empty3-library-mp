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

package one.empty3.library;


import one.empty3.libs.Image;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {

    public static final int DIR_TMP = 0;
    public static final int DIR_TEST_OUTPUT = 1;
    public static final int DIR_MODELS = 2;
    public static final int DIR_TEXTURES = 3;
    private File defaultFileOutput;
    private File configFile;
    private File tmpDir;
    private File testOutputDir;
    private File modelsDir;
    private File texturesDir;
    protected Map<String, String> map = new HashMap<String, String>();
    private static String androidRootPath = null;


    public Config() {
        initIfEmpty();
    }

    public static boolean isAndroid() {
        return "Dalvik".equals(System.getProperty("java.vm.name"));
    }

    public static void setAndroidRootPath(String path) {
        if (isAndroid()) {
            androidRootPath = path;
        }
    }

    public String getUserHome() {
        if (isAndroid()) {
            if(androidRootPath != null) {
                return androidRootPath;
            }
            return "/storage/0/Pictures";
        }
        return System.getProperty("user.home");
    }

    /**
     * Initializes config by loading or creating file; populates map
     */
    public boolean initIfEmpty() {
        Properties p = new Properties();
        Reader reader = null;
        String userHome = getUserHome();
        configFile = new File(userHome + File.separator + "empty3.config");
        Logger.getAnonymousLogger().log(Level.INFO, "Config file: " + configFile.getAbsolutePath());
        // Loads config properties into map; sets default output path
        if (!configFile.exists()) {
            createConfig();
        }
        try {
            reader = new FileReader(configFile);
            p.load(reader);

            for (Object key : p.keySet()) {
                String value = p.get(key).toString();

                map.put((String) key, value);

                // if is file reference..
                if (key.equals("path")) {
                    defaultFileOutput = new File(value);
                    File path = defaultFileOutput.getParentFile();
                    //if (path.exists() || path.mkdir()) {
                    //} else {
                    //    System.err.println("Failed to make/find directory for " + path);
                    //    return false;
                    //}
                }
            }

        } catch (IOException exp) {
            return false;
        }
        return false;
            // Creates config file with paths if absent; logs errors
                // Ensures parent directories exist before file creation
    }

    public boolean createConfig() {
        if (!configFile.exists()) {
            try {
                if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                configFile.createNewFile();
                PrintWriter pw = new PrintWriter(configFile);
                String userHome = getUserHome();
                String path = userHome + File.separator + "EmptyCanvasTest";
                
                pw.println("folderoutput=" + path.replace("\\", "\\\\"));
                pw.println("path=" + path.replace("\\", "\\\\"));
                pw.close();
                return true;
            } catch (IOException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Error creating config file", ex);
                return false;
            }
        } else return true;

    }

    public String allDefaultStrings() {
        StringBuilder sb = new StringBuilder();
        sb.append("folder.samples=");
        return sb.toString();
    }

    public File[] dirs(int type) {
        return new File[0];
    }

    public File getDefaultFileOutput() {
        return defaultFileOutput;
    }

    public void setDefaultFileOutput(File defaultFileOutput) {
        this.defaultFileOutput = defaultFileOutput;
    }

    public String getDefaultCodeVecMesh() {

        String s = getDefaultFileOutput() + File.separator + "defaultCode.calcmath";
        Logger.getAnonymousLogger().log(Level.INFO, s);
        return s;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public boolean save() {
        if (!configFile.exists()) {
            try {
                if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                configFile.createNewFile();
            } catch (IOException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Config.save() error", e);
                return false;
            }
        }
        try {
            PrintWriter pw = new PrintWriter(configFile);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                pw.println(entry.getKey() + "=" + entry.getValue().replace("\\", "\\\\"));
            }
            pw.close();
            return true;
        } catch (IOException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error saving config file");
            return false;
        }
    }
}
