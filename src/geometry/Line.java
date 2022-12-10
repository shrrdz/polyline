package geometry;

public class Line
{
    public double x0, y0;
    public double x1, y1;

    private final double k, q;

    public Line(double x0, double y0, double x1, double y1)
    {
        this.x0 = x0;
        this.y0 = y0;

        this.x1 = x1;
        this.y1 = y1;

        k = (x1 - x0) / (y1 - y0);
        q = x0 - k * y0;

        orientate();
    }

    private void orientate()
    {
        if (y0 > y1)
        {
            x0 = x0 - x1;
            x1 = x0 + x1;
            x0 = x1 - x0;

            y0 = y0 - y1;
            y1 = y0 + y1;
            y0 = y1 - y0;
        }
    }

    public boolean isIntersection(int y)
    {
        return (y >= y0 && y < y1);
    }

    public double getIntersection(int y)
    {
        return k * y + q;
    }
}