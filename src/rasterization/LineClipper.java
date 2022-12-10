package rasterization;

import geometry.Edge;
import geometry.Point;

import java.util.ArrayList;

public class LineClipper
{
    public ArrayList<Point> clippedPoints(ArrayList<Point> points, ArrayList<Point> clipPoints)
    {
        if (clipPoints.size() < 2)
        {
            return points;
        }

        ArrayList<Point> newPoints = points;

        Point latestPoint = clipPoints.get(clipPoints.size() - 1);

        for (Point clipPoint : clipPoints)
        {
            newPoints = clippedEdges(points, new Edge(latestPoint, clipPoint));

            points = newPoints;
            latestPoint = clipPoint;
        }

        return newPoints;
    }

    private ArrayList<Point> clippedEdges(ArrayList<Point> points, Edge edge)
    {
        if (points.size() < 2)
        {
            return points;
        }

        ArrayList<Point> newPoints = new ArrayList<>();

        Point latestPoint = points.get(points.size() - 1);

        for (Point point : points)
        {
            if (edge.isInside(point))
            {
                if (!edge.isInside(latestPoint))
                {
                    newPoints.add(edge.intersection(latestPoint, point));
                }

                newPoints.add(point);
            }
            else if (edge.isInside(latestPoint))
            {
                newPoints.add(edge.intersection(latestPoint, point));
            }

            latestPoint = point;
        }

        return newPoints;
    }
}