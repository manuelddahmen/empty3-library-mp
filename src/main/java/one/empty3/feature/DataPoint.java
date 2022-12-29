/*
 * Copyright (c) 2022. Manuel Daniel Dahmen
 */

package one.empty3.feature;

public interface DataPoint {

    public double distance(DataPoint datapoint);

    public void setCluster(int id);

    public int getCluster();

    public int getX();

    public int getY();
}
