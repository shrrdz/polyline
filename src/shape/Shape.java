package shape;

import java.util.ArrayList;
import java.util.Collections;

import geometry.Point;

public abstract class Shape 
{
    protected final ArrayList<Point> points = new ArrayList<>();

    public void addPoints(Point... point)
    {
        Collections.addAll(points, point);
    }

    public void clearPoints()
    {
        points.clear();
    }
    
    public ArrayList<Point> getPoints()
    {
        return points;
    }
}
