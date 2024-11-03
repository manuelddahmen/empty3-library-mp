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

package one.empty3.feature.jviolajones;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DetectorView extends JFrame {

    public static void main(String[] args) throws IOException {
        File imageFile = null;
        String xmlFileString = null;
        if (args.length == 0) {
            imageFile = new File("samples/img/manu.jpg");
            xmlFileString = "resources/xmlopencv/haarcascade_frontalface_alt2.xml";
        } else if (args.length == 2) {
            imageFile = new File(args[0]);
            xmlFileString = args[1];
        }
        new DetectorView(imageFile, xmlFileString).setVisible(true);
    }

    public DetectorView(File img, String XMLFile) throws FileNotFoundException, IOException {


        Image image = null;
        try {
            image = ImageIO.read(img);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert image != null;
        Dessin d = new Dessin(image);
        Detector detector = new Detector(XMLFile);
        List<Rectangle> res = detector.getFaces(img.getAbsolutePath(), 1, 1.25f, 0.1f, 1, true);

        Logger.getAnonymousLogger().log(Level.INFO, res.size() + " faces found!");

        for (Rectangle rect : res) {
            Logger.getAnonymousLogger().log(Level.INFO, "----");
            Logger.getAnonymousLogger().log(Level.INFO, "width " + rect.getWidth());
            Logger.getAnonymousLogger().log(Level.INFO, "height " + rect.getHeight());
            Logger.getAnonymousLogger().log(Level.INFO, "x " + rect.getX());
            Logger.getAnonymousLogger().log(Level.INFO, "y " + rect.getY());
        }

        d.setRects(res);
        setContentPane(d);
        this.setExtendedState(MAXIMIZED_BOTH);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                // Exit the application
                System.exit(0);
            }
        });
    }
}

class Dessin extends JPanel {
    protected Image img;
    int img_width, img_height;
    List<Rectangle> res;

    public Dessin(Image img) {
        super();
        this.img = img;
        img_width = img.getWidth(null);
        img_height = img.getHeight(null);
        res = null;
    }

    public void paint(Graphics g) {
        Graphics2D g1 = (Graphics2D) g;
        g1.setColor(Color.red);
        g1.setStroke(new BasicStroke(2f));
        if (img == null)
            return;
        Dimension dim = getSize();
        //Logger.getAnonymousLogger().log(Level.INFO, "véridique");
        g1.clearRect(0, 0, dim.width, dim.height);
        double scale_x = dim.width * 1.f / img_width;
        double scale_y = dim.height * 1.f / img_height;
        double scale = Math.min(scale_x, scale_y);
        int x_img = (dim.width - (int) (img_width * scale)) / 2;
        int y_img = (dim.height - (int) (img_height * scale)) / 2;
        g1.drawImage(img, x_img, y_img, (int) (img_width * scale), (int) (img_height * scale), null);
        if (res == null) return;

        for (Rectangle rect : res) {
            int w = (int) (rect.width * scale);
            int h = (int) (rect.height * scale);
            int x = (int) (rect.x * scale) + x_img;
            int y = (int) (rect.y * scale) + y_img;
            g1.drawRect(x, y, w, h);
        }

    }

    public void setRects(List<Rectangle> list) {
        this.res = list;
        repaint();
    }

}

