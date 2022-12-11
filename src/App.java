import fill.Flood;
import fill.Scanline;
import geometry.Point;
import rasterization.*;
import rasterization.Rasterizer.*;
import video.Window;
import shape.Polygon;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class App
{
    private enum Input { LEFT, RELEASED };

    private Input input;

    private LineRasterizer lineRasterizer;
    private LineRasterizer previewRasterizer;

    private final LineClipper lineClipper = new LineClipper();

    private final Flood floodFill = new Flood();
    private final Scanline scanlineFill = new Scanline();

    private Point floodNode;
    private boolean flooded = false;

    private final Polygon polygon = new Polygon();
    private final Polygon clipPolygon = new Polygon();

    private final Window window = new Window(1024, 768);

    private boolean help = true;
    
    private void start()
    {
        lineRasterizer = new LineRasterizer(window.image);
        previewRasterizer = new LineRasterizer(window.image);

        floodFill.setImage(window.image);
        scanlineFill.setImage(window.image);

        clipPolygon.addPoints(new Point(40, 170), new Point(40, 690), new Point(560, 690), new Point(560, 170));

        initialize();
        update();
    }

    private void update()
    {
        window.clear(0x050505);

        ArrayList<Point> clippedPoints = lineClipper.clippedPoints(polygon.getPoints(), clipPolygon.getPoints());

        rasterizeLine(polygon.getPoints(), 0x6097BA);
        rasterizeLine(clipPolygon.getPoints(), 0x00FFFF);

        // outline the clipped lines
        rasterizeLine(clippedPoints, 0x00FFFF);

        scanlineFill.setPoints(clippedPoints, 0x6097BA);
        scanlineFill.fill();

        if (flooded)
        {
            floodFill.setNode((int) floodNode.x, (int) floodNode.y, 0x00FFFF);
            floodFill.fill();
        }

        Graphics text = window.image.getGraphics();

        text.setColor(new Color(0xFFFFFF));

        text.drawString("Help: [H]", 10, window.image.getHeight() - 30);
        text.drawString("Clear canvas: [C]", 10, window.image.getHeight() - 10);
        text.drawString("Close: [Escape]", window.image.getWidth() - 100, window.image.getHeight() - 10);

        if (help)
        {
            Rectangle rectangle = new Rectangle(window.image.getWidth() / 2, 20, 900, 140);

            int rx = rectangle.x - (rectangle.width / 2);

            text.setColor(new Color(0x323232));
            text.fillRect(rx, rectangle.y, rectangle.width, rectangle.height);
            text.setColor(new Color(0xEEEEEE));
            text.fillRect(rx + 2, rectangle.y + 2, rectangle.width - 4, rectangle.height - 4);

            text.setColor(new Color(0x00000));
            text.setFont(new Font("SansSerif", Font.BOLD, 12));
            text.drawString("Line Rasterization", rx + 30, 50);
            text.drawString("Flood Fill & Clip", rx + 700,50);

            text.setFont(new Font("SansSerif", Font.PLAIN, 12));

            text.drawString("Flood fill: [RMB]",  rx + 720, 80);

            text.drawString("Line: Full", rx + 50, 140);
            text.drawString("Flooded: " + flooded, rx + 720, 140);
        }

        window.update();
    }

    private void previewLine(double x, double y, int color)
    {
        update();

        previewRasterizer.setColor(color);
        previewRasterizer.pattern = Pattern.DASHED;

        if (input == Input.LEFT && !polygon.getPoints().isEmpty())
        {
            int size = polygon.getPoints().size();

            previewRasterizer.rasterize(polygon.getPoints().get(size - 1).x, polygon.getPoints().get(size - 1).y, x, y);
            previewRasterizer.rasterize(polygon.getPoints().get(0).x, polygon.getPoints().get(0).y, x, y);
        }
    }

    private void rasterizeLine(ArrayList<Point> points, int color)
    {
        if (points.size() > 1)
        {
            lineRasterizer.setColor(color);
            lineRasterizer.pattern = Pattern.FULL;

            for (int i = 0; i < points.size(); i++)
            {
                lineRasterizer.rasterize(points.get(i).x, points.get(i).y,
                    points.get((i + 1) % points.size()).x, points.get((i + 1) % points.size()).y);
            }
        }
    }

    private void initialize()
    {
        KeyListener keyListener = new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent event)
            {
                if (input != Input.LEFT)
                {
                    switch (event.getKeyCode())
                    {
                        case KeyEvent.VK_C: 
                            polygon.clearPoints();

                            flooded = false;
                        break;

                        case KeyEvent.VK_H: help = !help; break;

                        case KeyEvent.VK_ESCAPE: window.close();
                    }

                    update();
                }
            }
        };

        MouseAdapter mouseAdapter = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                if (event.getButton() == 1)
                {
                    input = Input.LEFT;

                    previewLine(event.getX(), event.getY(), 0x00FFFF);

                    if (polygon.getPoints().isEmpty())
                    {
                        polygon.addPoints(new Point(event.getX(), event.getY()));
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent event)
            {
                switch (event.getButton())
                {
                    case 1: polygon.addPoints(new Point(event.getX(), event.getY())); break;
                    
                    case 3:
                        flooded = true;
                        floodNode = new Point(event.getX(), event.getY());
                    break;
                }

                input = Input.RELEASED;

                update();
            }
        };

        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent event)
            {
                previewLine(event.getX(), event.getY(), 0x00FFFF);
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                update();
            }
        };

        ComponentAdapter componentAdapter = new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent event)
            {
                window.image = new BufferedImage(window.panel.getWidth(), window.panel.getHeight(), 1);

                lineRasterizer= new LineRasterizer(window.image);
                previewRasterizer = new LineRasterizer(window.image);

                floodFill.setImage(window.image);
                scanlineFill.setImage(window.image);

                update();
            }
        };

        window.panel.addKeyListener(keyListener);
        window.panel.addMouseListener(mouseAdapter);
        window.panel.addMouseMotionListener(mouseMotionAdapter);
        window.panel.addComponentListener(componentAdapter);
    }

    public static void main(String[] args)
    {
        new App().start();
    }
}