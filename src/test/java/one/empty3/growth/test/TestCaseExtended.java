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

package one.empty3.growth.test;

import junit.framework.TestCase;
import one.empty3.library.Point3D;
import one.empty3.library.ZBuffer;

import javax.imageio.ImageIO;

import one.empty3.libs.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestCaseExtended extends TestCase {
    private static boolean isInitialized = false;
    private static TestCaseExtended caseExtended;
    private int serId;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        if (!isInitialized) {
            caseExtended = new TestCaseExtended();
            caseExtended.loadSaveNewSerId();
            caseExtended.emptyTestResultsDirectory();
            isInitialized = true;
        }
    }

    protected void assertEqualsNaNPoint3D(Point3D x) {
        assertEquals(x.get(0) + x.get(1) + x.get(2), Double.NaN);
    }

    private int loadSaveNewSerId() {
        File file = new File("configTest.properties");
        if (!file.exists()) {
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));

            String property = properties.getProperty(getClass().getCanonicalName());
            if (property != null) {
                setSerId(Integer.parseInt(property) + 1);
            } else {
                createNewId(null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        properties.put(getClass().getCanonicalName(), ((Integer) getSerId()).toString());
        try {
            properties.store(new FileOutputStream(file), "Saved again");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return getSerId();
    }

    private void createNewId(Class myClass) {
        int nextInt = (int) (Math.random() * 10000000);
        setSerId(nextInt);
    }

    private void setSerId(int serId) {
        this.serId = serId;
    }

    protected int getSerId() {
        return serId;
    }

    protected File getUniqueFilenameForProduction(String directory, String baseFilename, String extension) throws IOException {
        File file;
        int lastId = loadSaveNewSerId();
        do {
            file = new File(
                    (directory == null ? "" : directory + File.separator) +
                            baseFilename + "-" + (lastId + 1) +
                            (extension == null ? "" : "_." + extension));
            Logger.getAnonymousLogger().log(Level.INFO, file.getCanonicalPath().toString());
            setSerId(++lastId);
            loadSaveNewSerId();
        }
        while (file.exists());

        return file;
    }

    protected void assertEqualsPoint3D(Point3D x, Point3D y, double delta) {
        for (int i = 0; i < 3; i++) {
            assertEquals(y.get(i), x.get(i), delta);
        }
    }

    protected void writeImage(Image image) {
        try {
            File imageFile = getUniqueFilenameForProduction("testResults", getClass().getCanonicalName() + "___" + getClass().getEnclosingMethod(), "jpg");
            ImageIO.write(image, "jpg", imageFile);
            Logger.getAnonymousLogger().log(Level.INFO, imageFile + " written");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void writeImage(ZBuffer z) {
        z.draw();
        writeImage(z.image());

    }

    private void emptyTestResultsDirectory() {
        /*String[] testResults = new File("testResults").list();
        if(testResults!=null)
        for (String f : testResults) {
            new File("testResults" + File.separator + f).delete();
        }*/
    }

    public void testVoid() {
        assertTrue(((true == true) != false) | false);
    }
}
