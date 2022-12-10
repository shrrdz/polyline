package geometry;

public class Edge
{
    private final Point start, end;

    public Edge(Point start, Point end)
    {
        this.start = start;
        this.end = end;
    }

    public Point intersection(Point p0, Point p1)
    {
        double denominator = ((p0.x - p1.x) * (start.y - end.y) - (p0.y - p1.y) * (start.x - end.x));

        double x = ((p0.x * p1.y - p0.y * p1.x) * (start.x - end.x) - 
                    (start.x * end.y - start.y * end.x) * (p0.x - p1.x)) / denominator;

        double y = ((p0.x * p1.y - p0.y * p1.x) * (start.y - end.y) - 
                    (start.x * end.y - start.y * end.x) * (p0.y - p1.y)) / denominator;

        return new Point(x, y);
    }

    public boolean isInside(Point point)
    {
        return ((end.y - start.y) * point.x - (end.x - start.x) * point.y + end.x * start.y - end.y * start.x > 0);
    }
}