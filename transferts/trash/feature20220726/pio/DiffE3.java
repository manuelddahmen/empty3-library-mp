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
comparaipar cerlcle d intensite et
de lumierrs filtrers.
*/

package one.empty3.feature20220726.pio;

import java.awt.Color;
import one.empty3.feature20220726.PixM;
import one.empty3.io.ProcessFile;
import one.empty3.*;
import one.empty3.library.*;

import java.io.File;
import java.util.*;

import javaAnd.awt.image.imageio.ImageIO;

class Circle {

    double x, y, r, a;
    public PixM m;
    Color c;
    double i;

    public Circle(PixM m, double x, double a,
                  double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.a = a;
        this.m = m;
    }

    public void rotate(double angle) {
        a += angle;
    }

    public double get(double r, double g, double b) {
        return Math.sqrt(r * r + g * g + b * b);
    }


    public Point3D get(double a, double r) {
        Point3D n = new Point3D(r * Math.cos(2 * Math.PI * a),
                r * Math.sin(2 * Math.PI * a), 0.0);
        return n;
    }

    public void variate(double x, double y,
                        double r, double rotate) {
        this.x += x;
        this.y += y;
        this.r += r;
        this.a += rotate;
    }

    public double getIntensity(int i, int j) {
        double count = 0.0;
        double
                i0, j0;


        double iin = 0.0;

        double tin = 0.0;
        for (i0 = i - r; i0 < i + r; i0++) {
            for (j0 = i - r; j0 < i + r; j0++) {

                if (Math.sqrt((i0 - i) * (i0 - i)
                        + (j0 - j) * (j0 - j))
                        <= r) {
                    count++;
                    iin = m.getIntensity((int) i0, (int) j0);
                    tin += iin;
                }
            }
        }


        return tin / count;
    }

    public Circle getLevel() {
        Circle c = this;
        // I mean. Parcourir le cercle
        // mesurer I / numPoints
        // for(int i=Math.sqrt()
        //  return c;
        int count = 0;
        double intensity = 0.0;
        c.r = r;
        for (double i = c.x - c.r; i <= c.x + c.r; i++) {
            for (double j = c.y - c.r; j <= c.y + c.r; j++) {
                if (Math.sqrt((i - c.x) * (i - c.x) + (j - c.y) * (j - c.y)) <= c.r * c.r
                        && c.x - c.r >= 0 && c.y - c.r >= 0 && c.x + c.r < c.m.getColumns() && c.y + c.r < c.m.
                        getLines()) {
                    intensity += c.m.getIntensity((int) i, (int) j);
                    count++;

                }
            }
        }

        if (count > 0) {
            c.i = intensity / count;
        } else {
            c.i = 0.0;
            c.r = 0.0;
        }


        return c;

    }

    public double match(Circle b) {
        return getLevel().getIntensity((int) x, (int) y) - b.getLevel().getIntensity((int) b.x, (int) b.y);


    }
               /*
                     public double dist(Circle b) {
           
            double diff = 0.0;
              
            for(int i = (int)(-b.x+ b.r); i<b.y+b.r;i++)
                for(int j=(int)( -b.x+ b.r); j<b.y+b.r; j++)
               
                diff  = diff + m1.getRgb(i. j).norme()
                             - m1.getRgb(i, j).norme();
             
           
                  
              
                   diff =(P.n(b.x,b.oy, b.i)).moins(
                    p1.mean(b.x-b.r, b.y-r, b.x+b.i, b.y+b.i).norme();
                       
                       
                    
               
                
     
                 return diff; // attention red
                
        } 
                           
               
               */


}

public class DiffE3 extends ProcessFile {


    public DiffE3() {
    }
/*
private int sizeElement = 20, elementSize=20;
    class ColorTranform {
        
        public void rotate(){}
        public double compare(double[] rbga){}
        
    }
    
    
           
        this.x=x;this.y=y;this.r=r;
        
        public double get(Circle c, double r, double a1, double a2) {
            double pi = 0.0;
            for (double a = a1; a<a2; a+= 1/r/2/Math.PI) {
            
                pi+= c.getIntensity((int)(r*Math.cos(a)) ,(int)(r*Math.sin(a)));
            }
            return pi;
                
        
       }
  


    
   
   
 */


    public boolean process(File in, File out) {
        PixM moutA, moutB;
        try {

            if (!in.getName().endsWith(".jpg"))
                return false;
            PixM pi = new PixM(new one.empty3.libs.Image(in));
            moutA = pi;
            moutB = pi;
            // work on featutes
            for (double r = 2; r < 30.0; r *= 2)
                for (int i = 0; i < pi.getColumns(); i++)
                    for (int j = 0; j < pi.getLines(); j++)
                        for (int i1 = 0; i1 < pi.getColumns(); i1++)

                            for (int j1 = 0; j1 < pi.getLines(); j1++) {
                                moutA.set(i, j, moutA.get(i, j) + new Circle(pi, i, j, 0, r).match(new Circle(pi, i1, j1, 0, r)));

                                moutB.set(i1, j1, moutB.get(i1, j1) + new Circle(pi, i1, j1, 0, r).match(new Circle(pi, i, j, 0, r)));


                            }
            //new one.empty3.libs.Image(1,1,1.saveToFile(pi.getImage(), "JPEG", out);
            new one.empty3.libs.Image(1,1,1.saveToFile(moutA.normalize(0., 1.).getImage(), "JPEG", new File(out.getParent() + "a" + "jpg"));
            new one.empty3.libs.Image(1,1,1.saveToFile(moutB.normalize(0., 1.).getImage(), "JPEG", new File(out.getParent() + "b" + "jpg"));

            //new one.empty3.libs.Image(1,1,1.saveToFile(m2g, "JPEG", new File(out.getParent()+5+"jpg"));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }
}
     
            

