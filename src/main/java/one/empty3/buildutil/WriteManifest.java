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

package one.empty3.buildutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

/*__
 * Created by manue on 12-08-19.
 */
public class WriteManifest {

    public static void main(String [] args)
    {
        String dir;
        String mainClass;
        try {
            if(args.length>0)
            {
                dir = args[0];
            }
            else {
                dir = "./lib";
            }
            if(args.length>1)
            {
                mainClass = args[1];
            }
            else {
                mainClass=null;
            }
            File manifest = new File("META-INF/MANIFEST.MF");
            if(manifest.exists())
            {
                manifest.renameTo(new File(manifest.getAbsolutePath()+".tmp"+System.nanoTime()));
            }
            else {
                new File("META-INF").mkdir();
            }
            //File launch = new File("LAUNCH.BAT");
            FileOutputStream fileOutputStream = new FileOutputStream(manifest);
            PrintWriter pw = new PrintWriter(fileOutputStream);
            pw.println("Manifest-Version: 1.0");
            if(mainClass!=null)
                pw.println("Main-Class: "+mainClass);
            StringBuilder stringBuilder = new StringBuilder("Class-Path: ");
            recurse(dir, stringBuilder);
            pw.println(stringBuilder.toString());
            //Main-Class: be.manudahmen.empty3.app.opad.PanelGraphics
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void recurse(String s, StringBuilder stringBuilder) {
        File file = new File(s);
        if(file.exists())
        {
            if(file.isDirectory())
            {
                String[] list = file.list();
                for(String fn : list) {
                    recurse(s + "/" + fn, stringBuilder);
                }
            } else
            {
                if(file.getName().endsWith(".jar"))
                {
                    stringBuilder.append(s + " ");
                }
            }
        }
        else
        {
            Logger.getAnonymousLogger().info("Error file not exists = " + s);
        }

    }
}
