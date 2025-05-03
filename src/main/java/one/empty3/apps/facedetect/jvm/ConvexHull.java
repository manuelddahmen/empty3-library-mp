/***
 * Gemini AI code generation 2025
 */
package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Lumiere;
import one.empty3.library.Point3D;


import one.empty3.libs.Color;
import one.empty3.libs.Image;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvexHull {
    private final List<Point3D> list;
    private final Image mask;
    List<Point3D> p = new ArrayList<>();

    public ConvexHull(List<Point3D> list, Dimension dimension) {
        List<Point3D> list1 = new ArrayList<>();

        mask = new Image((int) dimension.getWidth(), (int) dimension.getHeight());

        for (Point3D point3D : list) {
            list1.add(point3D.multDot(new Point3D(dimension.getWidth(), dimension.getHeight(), 0.0)));
        }
        this.list = list1;

        createConvexHull();
    }


    public List<Point3D> computeHull() {
        List<Point3D> hull = new ArrayList<>();
        if (list.size() < 3) return new ArrayList<>(list); // Handle cases with < 3 points

        // Find the leftmost point (starting point)
        Point3D onHull = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getX() < onHull.getX()) {
                onHull = list.get(i);
            }
        }

        Point3D endpoint;
        do {
            hull.add(onHull);
            endpoint = list.get(0); // Initial guess for the next point on the hull
            for (int j = 1; j < list.size(); j++) {
                Point3D p = list.get(j);
                if (endpoint == onHull || isLeftTurn(onHull, endpoint, p)) {
                    endpoint = p;
                }
            }
            onHull = endpoint;
        } while (endpoint != hull.get(0)); // Stop when we wrap back to the start



        return hull;
    }


    private boolean isLeftTurn(Point3D p1, Point3D p2, Point3D p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) -
                (p2.getY() - p1.getY()) * (p3.getX() - p1.getX()) > 0;
    }


    public void createConvexHull() {
        List<Point3D> hull = computeHull();

        p = hull;

        int [] xPoints = new int[p.size()];
        int [] yPoints = new int[p.size()];
        for(int i=0; i<p.size(); i++) {
            xPoints[i] = (int) p.get(i).getX();
            yPoints[i] = (int) p.get(i).getY();
        }



        fillPolyMp(mask, xPoints, yPoints, p.size());

/*
        try {
            File file = new File(".\\storage\\");
            if(!file.exists())
                file.mkdir();
            if(file.exists()) {
                File file1 = new File(".\\storage\\convexHull.jpg");
                if(file1.exists())
                    file1.delete();
                if (ImageIO.write(mask, "jpg", file1)) {
                    Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull done");
                } else
                    Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull failed "+mask.getWidth()+"/"+mask.getHeight());
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull failed "+mask.getWidth()+"/"+mask.getHeight());
            e.printStackTrace();
        }
*/
        Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull done " + p.size()+"/"+list.size());
   }

    private void fillPolyMp(Image mask, int[] xPoints, int[] yPoints, int size) {
        for (int i = 0; i < mask.getWidth(); i++) {
            for (int j = 0; j < mask.getHeight(); j++) {
                if(testIfIn(i,j ))
                    mask.setRgb(i, j, Color.newCol(1f,1f,1f).getRGB());

            }
        }
    }

    public boolean testIfIn(int x, int y) {
        double[] rgb1 = Lumiere.getDoubles(mask.getRgb(x,y));
        double[] rgb2 = Lumiere.getDoubles(Color.newCol(1f,1f,1f).getRGB());
        if(x>=0&&x<mask.getWidth()&&y>=0&&y<mask.getHeight()) {
            return rgb1[0] == rgb2[0] && rgb1[1] == rgb2[1] && rgb1[2] == rgb2[2] && rgb1[0] >= 0.9
                    && rgb1[1] >= 0.9 && rgb1[2] >= 0.9 && rgb2[0] >= 0.9 && rgb2[1] >= 0.9 && rgb2[2] >= 0.9;
        }
        return false;
    }

    public List<Point3D> getList() {
        return list;
    }

    public Image getMask() {
        return mask;
    }

    public List<Point3D> getP() {
        return p;
    }

    public void setP(List<Point3D> p) {
        this.p = p;
    }
}
