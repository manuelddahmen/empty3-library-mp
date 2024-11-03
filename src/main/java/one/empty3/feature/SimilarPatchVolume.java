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


import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;

import one.empty3.libs.Image;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimilarPatchVolume {
    private final File directory = new File("outputFiles/_" + "__" +

            Time.from(Instant.now()).toString().replace(' ', '_').replace('|', '_')
                    .replace('\\', '_').replace('/', '_').replace(':', '_')
            + "/");
    //private Image image2;
    private Image image1;

    /*
    public static void makeGoodOutput(File original, File folderOutput) {
        try {
            Path source = FileSystems.getDefault().getPath(original.getAbsolutePath());
            Path newDir = FileSystems.getDefault().getPath(folderOutput.getAbsolutePath());
            Files.copy(source, newDir.resolve(source.getFileName()));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/
    public static void work(File dir, Image imageToWrite, String outputFilename) throws IOException {
        File dir1 = new File(dir.getAbsolutePath() + "/" + outputFilename.substring(0,
                outputFilename.lastIndexOf("/")));
        File file = new File(dir.getAbsolutePath() + "/" + outputFilename);

        if (file.exists()) {
            Logger.getAnonymousLogger().log(Level.INFO, "File exists, quit" + file.getAbsolutePath());
            System.exit(-1);
        }

        if (dir1.mkdirs())
            Logger.getAnonymousLogger().log(Level.INFO, dir.getAbsolutePath() + " created");
        System.out.print("\n(width, height) = " + imageToWrite.getWidth() +
                ", " + imageToWrite.getHeight() + ")");
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("JPG");
        ImageWriter imageWriter = imageWriters.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
        imageWriter.setOutput(ios);
        imageWriter.write(imageToWrite);
        /*if (!ImageIO.write(imageToWrite, "image/jpg", file)) {
            Logger.getAnonymousLogger().log(Level.INFO, "Error inappropriate writer or not found " + "jpg");
            System.exit(-2);
        }*/
    }

    public static void main(String[] args) {
        new SimilarPatchVolume().exec();
    }

    public Image getImageFromDir(String filename1) {
        String s0 = filename1.substring(filename1.lastIndexOf(".") + 1);
        if ((Arrays.asList(ImageIO.getReaderMIMETypes()).contains(s0))) {
            Logger.getAnonymousLogger().log(Level.INFO, "No ImageReader for " + s0 + " from file" + filename1);
            return null;
        }

        if (Arrays.asList(ImageIO.getWriterFormatNames()).contains(s0)) {
            try {
                if (directory.mkdirs())
                    Logger.getAnonymousLogger().log(Level.INFO, "Directory created" + directory.getAbsolutePath());
                Logger.getAnonymousLogger().log(Level.INFO, "format name image1 " + s0 + " found");

                image1 = hideAlpha(new File("resources/" + filename1));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Format non found" + s0);
        }
        return image1;
    }

    public void exec() {


        //int img = 0;
        for (String filename1 : Objects.requireNonNull(new File("resources").list())) {
            //for (String filename2 : Objects.requireNonNull(new File("resources").list())) {
            try {
                image1 = getImageFromDir(filename1);
                if (image1 == null)
                    continue;

//                    image2 = getImageFromDir(filename2);
                //   GradientFilter gradientMask = new GradientFilter(image1.getWidth(), image1.getHeight());
                PixM pixMOriginal = PixM.getPixM(image1, 500);
                image1 = null;
                GradientFilter gradientMask = new GradientFilter(pixMOriginal.columns, pixMOriginal.lines);
                M3 imgForGrad = new M3(pixMOriginal,
                        2, 2);
                M3 filter = gradientMask.filter(imgForGrad);
                PixM[][] imagesMatrix = filter.getImagesMatrix();//.normalize(0, 1);


//                    image1 = null;

                // Zero. +++Zero orientation variation.
                Linear linear = new Linear(imagesMatrix[1][0], imagesMatrix[0][0],
                        new PixM(pixMOriginal.columns, pixMOriginal.lines));
                linear.op2d2d(new char[]{'*'}, new int[][]{{1, 0}}, new int[]{2});
                PixM smoothedGrad = linear.getImages()[2];
                int iteratesAngleGrad = 12;
                M3 filter3 = new AfterGradientBeforeExtremum(iteratesAngleGrad).filter(new M3(smoothedGrad, 1, 1));
                PixM[][] afterGradientAngular = filter3.getImagesMatrix();
                /* try {
                 work(directory, image1, filename1 + "/original.jpg");
                 } catch (IOException e) {
                 e.printStackTrace();
                 }*/

                for (int angleIncr = 0; angleIncr < iteratesAngleGrad; angleIncr++) {
                    double angle = 2 * Math.PI * angleIncr / iteratesAngleGrad;
                    for (double sigma = 0.8; sigma < 2.0; sigma += 0.2) {
                        PixM pixM = afterGradientAngular[angleIncr][0].applyFilter(new GaussFilterPixM(afterGradientAngular[angleIncr][0], 4, sigma));


                        for (int size = 1; size <= 16; size *= 2) {
                            //
                            M3 smoothedGradM3 = new M3(pixM.subSampling(size), 1, 1);
                            // Search local maximum
                            LocalExtrema localExtrema = new LocalExtrema(smoothedGradM3.columns, smoothedGradM3.lines, 3, 0);
                            M3 filter2 = localExtrema.filter(smoothedGradM3);
                            PixM filter1 = filter2.getImagesMatrix()[0][0];
                            Logger.getAnonymousLogger().log(Level.INFO, "Original read image1");
                            work(directory, imagesMatrix[0][0].getImage(), filename1 + "/1/angle-" + angle + "/sigma" + sigma + "/size" + size + "gradient.jpg");
                            Logger.getAnonymousLogger().log(Level.INFO, "oriented grad extremum search (max==1.0) ");
                            AtomicInteger i = new AtomicInteger();
                            int finalSize = size;
                            Arrays.stream(filter.getImagesMatrix()).forEach(pixMS -> Arrays.stream(pixMS).forEach(pixM1 ->
                            {
                                try {
                                    work(directory, pixM1.getImage(), filename1 + "/2-smoothed_grad/angle-" + angle + "-arrayCase" + (i.incrementAndGet()) + "size" +
                                            +finalSize + ".jpg");
                                } catch (IOException exception) {
                                    exception.printStackTrace();
                                }
                            }));
                            work(directory, filter1.normalize(0, 1).getImage(), filename1 + "/3/extremum_search" + sigma + "/size" + size + ".jpg");
                            stream(smoothedGradM3, angle, sigma, size, filename1);
                            System.gc();
                        }
                    }

                }
                Logger.getAnonymousLogger().log(Level.INFO, "Thread terminated without exception");

            } catch (IOException exception) {
                exception.printStackTrace();
            }

            //}
        }
    }

    private void stream(M3 smoothedGradM3, double angle, double sigma, int size, String prefixDir) {
        //int[] i = {0};
        Arrays.stream(smoothedGradM3.getImagesMatrix()).forEach(pixMS -> Arrays.stream(pixMS).forEach(pixM1 -> {
                    LocalExtrema localExtrema1 = new LocalExtrema(smoothedGradM3.columns, smoothedGradM3.lines, 3, 0);
                    M3 extremaOrientedGrad = localExtrema1.filter(new M3(pixM1, 1, 1));
                    try {
                        Logger.getAnonymousLogger().log(Level.INFO, "Gradient (gx,gy).(nx,ny)");
                        work(directory, pixM1.normalize(0, 1).getImage(), prefixDir + "/4/OrientedGradExtremum_1_sigma" + sigma + "angle" + angle + "size" + size + ".jpg");
                        System.gc();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Logger.getAnonymousLogger().log(Level.INFO, "oriented grad extremum search (max==1.0) ");
                 /*   Arrays.stream(extremaOrientedGrad.getImagesMatrix()).forEach(pixMS1 -> Arrays.stream(pixMS1).forEach(pixM -> {
                        for (double min = 0.40; min < 1.0; min += 0.2)
                            Histogram2.testCircleSelect(pixM.getImage(),
                                    new File(directory.getAbsolutePath() + "/" + prefixDir + "/5/histogram_sigma" + sigma + "angle" + angle + "size" + size + "_" + min + ".jpg"),
                                    20, min, 20.0);
                        //i[0]++;
                        
                        System.gc();

                    }));*/

                })
        );
    }

    /*
        public double distance(PixM image1, PixM image2, int x1, int y1, int s1, int x2, int y2, int s2) {
            // Prendre 2 patches 2 à 2.
            // Les faire tourner,
            // Prendre x tailles dans 1, 2 à partir de 3x3
            // 1) Brute "force".


            return 0.0;

        }
    */
    public static Image hideAlpha(File input) throws IOException {
        // Read input
        Image inputImage = new one.empty3.libs.Image(ImageIO.read(input));

        // Make any transparent parts white
        if (inputImage.getTransparency() == Transparency.TRANSLUCENT) {
            // NOTE: For BITMASK images, the color model is likely IndexColorModel,
            // and this model will contain the "real" color of the transparent parts
            // which is likely a better fit than unconditionally setting it to white.

            // Fill background  with white
            Graphics2D graphics = inputImage.createGraphics();
            try {
                graphics.setComposite(AlphaComposite.DstOver); // Set composite rules to paint "behind"
                graphics.setPaint(Color.WHITE);
                graphics.fillRect(0, 0, inputImage.getWidth(), inputImage.getHeight());
            } finally {
                graphics.dispose();
            }
        }

        return inputImage;

    }

}
