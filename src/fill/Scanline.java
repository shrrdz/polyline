package fill;

import geometry.Point;
import geometry.Line;
import java.util.ArrayList;
import java.util.Collections;

public class Scanline extends Fill
{
    private ArrayList<Point> vertices;

    public void setPoints(ArrayList<Point> vertices, int fillColor)
    {
        this.vertices = vertices;
        this.fillColor = fillColor;
    }

    public boolean pattern(Point point)
    {
        double patternX = point.x % 10;
        double patternY = point.y % 10;

        return (patternX == patternY);
    }

    @Override
    public void fill()
    {
        ArrayList<Line> lines = new ArrayList<>();
        ArrayList<Line> slopedLines = new ArrayList<>();

        ArrayList<Double> intersections = new ArrayList<>();

        if (vertices.size() < 2)
        {
            return;
        }

        for (int i = 0; i < vertices.size(); i++)
        {
            double x0 = (vertices.get(i)).x;
            double y0 = (vertices.get(i)).y;

            double x1 = (vertices.get((i + 1) % vertices.size())).x;
            double y1 = (vertices.get((i + 1) % vertices.size())).y;

            Line line = new Line(x0, y0, x1, y1);

            lines.add(line);
        }

        double yMin = (lines.get(0)).y0;
        double yMax = (lines.get(0)).y1;

        for (Line line : lines)
        {
            // if the line is sloped
            if (line.y0 != line.y1)
            {
                Line slopedLine = new Line(line.x0, line.y0, line.x1, line.y1);

                slopedLines.add(slopedLine);

                if (yMin > slopedLine.y0)
                {
                    yMin = slopedLine.y0;
                }

                if (yMax < slopedLine.y1)
                {
                    yMax = slopedLine.y1;
                }
            }
        }

        for (int y = (int) yMin; y < yMax; y++)
        {
            intersections.clear();

            for (Line intersectedLine : slopedLines)
            {
                if (intersectedLine.isIntersection(y))
                {
                    intersections.add(intersectedLine.getIntersection(y));
                }
            }

            Collections.sort(intersections);

            int clips = intersections.size() - 1;

            while (clips > 0)
            {
                for (int x = (intersections.get(clips - 1)).intValue(); x <= (intersections.get(clips)).intValue(); x++)
                {
                    if (pattern(new Point(x, y)))
                    {
                        image.setRGB(x, y, fillColor);
                    }
                }

                // update/delete previous clip fill
                clips -= 2;
            }
        }
    }
}