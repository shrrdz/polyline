package shape;

import geometry.Point;
import javafx.util.Pair;

public class Triangle extends Shape
{
    public Point midpoint()
    {
        Point p0 = this.points.get(0);
        Point p1 = this.points.get(1);

        return new Point((p0.x + p1.x) / 2,  (p0.y + p1.y) / 2);
    }

    // calculates the x and y coordinates of the third point of the isosceles triangle
    public Pair<Double, Double> calculateIsosceles(int x, int y)
    {
        Point p0 = this.points.get(0);
        Point p1 = this.points.get(1);

        // base
        double k0 = (p1.y - p0.y) / (p1.x - p0.x);
        double q0 = y - k0 * x;

        // perpendicular line
        double k1 = -1 / k0;
        double q1 = midpoint().y - k1 * midpoint().x;

        // parallel line
        double q3 = y - k0 * x;

        double tx = (q3 - q1) / (k1 - k0);
        double ty = k0 * tx + q0;

        return new Pair<Double,Double>(tx, ty);
    }

    public void removePoint(int index)
    {
        points.remove(index);
    }
}