import fill.Flood;
import fill.Scanline;
import geometry.Point;
import rasterization.*;
import rasterization.Rasterizer.*;
import video.Window;
import shape.Polygon;
import shape.Triangle;

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
    private final Scanline triangleScanline = new Scanline();

    private Point floodNode;
    private boolean flooded;

    private final Polygon polygon = new Polygon();
    private final Triangle triangle = new Triangle();
    private final Polygon clipPolygon = new Polygon();

    private final Window window = new Window(1024, 768);

    private int linePattern;

    private int px, py;
    private double tx, ty;

    private boolean help = true;
    private boolean darkMode = true;
    private boolean triangleMode;
    private boolean baseNormal;
    
    private void start()
    {
        lineRasterizer = new LineRasterizer(window.image);
        previewRasterizer = new LineRasterizer(window.image);

        floodFill.setImage(window.image);
        scanlineFill.setImage(window.image);
        triangleScanline.setImage(window.image);

        clipPolygon.addPoints(new Point(40, 170), new Point(40, 690), new Point(560, 690), new Point(560, 170));

        initialize();
        update();
    }

    private void update()
    {
        window.clear(darkMode ? 0x050505 : 0xEEEEEE);

        ArrayList<Point> clippedPoints = lineClipper.clippedPoints(polygon.getPoints(), clipPolygon.getPoints());
        ArrayList<Point> clippedTrianglePoints = lineClipper.clippedPoints(triangle.getPoints(), clipPolygon.getPoints());

        rasterizeLine(polygon.getPoints(), 0x6097BA);
        rasterizeLine(triangle.getPoints(), 0xFF6347);
        rasterizeLine(clipPolygon.getPoints(), 0x00FFFF);

        // outline the clipped lines
        rasterizeLine(clippedPoints, 0x00FFFF);
        rasterizeLine(clippedTrianglePoints, 0xFF0000);

        scanlineFill.setPoints(clippedPoints, 0x6097BA);
        triangleScanline.setPoints(clippedTrianglePoints, 0xE6655C);

        scanlineFill.fill();
        triangleScanline.fill();

        if (flooded)
        {
            floodFill.setNode((int) floodNode.x, (int) floodNode.y, 0x00FFFF);
            floodFill.fill();
        }

        Graphics text = window.image.getGraphics();

        text.setColor(darkMode ? new Color(0xFFFFFF) : new Color(0x000000));

        text.drawString("x - [" + px + "]", 10, 20);
        text.drawString("y - [" + py + "]", 10, 40);

        text.drawString("Help: [H]", 10, window.image.getHeight() - 50);
        text.drawString("Background: [B]", 10, window.image.getHeight() - 30);
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
            text.drawString("Isosceles Triangle", rx + 280, 50);
            text.drawString("Anti-aliasing", rx + 530,50);
            text.drawString("Flood Fill & Clip", rx + 700,50);

            text.setFont(new Font("SansSerif", Font.PLAIN, 12));
            text.drawString("Change pattern: [D]", rx + 50, 80);

            text.drawString("Toggle triangle: [T]", rx + 300, 80);
            text.drawString("Draw base normal: [N]", rx + 300, 100);
            
            text.drawString("Toggle AA: [  ]", rx + 550, 80);

            text.drawString("Triangle mode: " + (triangleMode ? (baseNormal ? "Base normal" : "ON") : "OFF"), rx + 300, 140);
            text.drawString("Flood fill: [RMB]",  rx + 720, 80);

            switch (lineRasterizer.pattern)
            {
                case FULL:        text.drawString("Line: Full", rx + 50, 140);        break;
                case DOTTED:      text.drawString("Line: Dotted", rx + 50, 140);      break;
                case DASHED:      text.drawString("Line: Dashed", rx + 50, 140);      break;
                case DASH_DOTTED: text.drawString("Line: Dash-Dotted", rx + 50, 140); break;
            }

            text.drawString("Anti-aliasing: OFF", rx + 550, 140);
            text.drawString("Flooded: " + flooded, rx + 720, 140);
        }

        window.update();
    }

    private void previewLine(double x, double y, int color)
    {
        update();

        previewRasterizer.setColor(darkMode ? color : 0x000000);
        previewRasterizer.pattern = Pattern.DASHED;

        if (input == Input.LEFT && !polygon.getPoints().isEmpty() && !triangleMode)
        {
            int size = polygon.getPoints().size();

            previewRasterizer.rasterize(polygon.getPoint(size - 1).x, polygon.getPoint(size - 1).y, x, y);
            previewRasterizer.rasterize(polygon.getPoint(0).x, polygon.getPoint(0).y, x, y);
        }

        if (input == Input.LEFT  && triangleMode)
        {
            int size = triangle.getPoints().size();

            if (triangle.getPoints().size() == 1)
            {
                previewRasterizer.rasterize(triangle.getPoint(0).x, triangle.getPoint(0).y, x, y);
                previewRasterizer.rasterize(triangle.getPoint(0).x, triangle.getPoint(0).y, x, y);
            }
            else
            {
                previewRasterizer.rasterize(triangle.getPoint(size - 1).x, triangle.getPoint(size - 1).y, tx, ty);
                previewRasterizer.rasterize(triangle.getPoint(0).x, triangle.getPoint(0).y, tx, ty);

                if (baseNormal)
                {
                    previewRasterizer.rasterize((triangle.getPoint(0).x + triangle.getPoint(1).x) / 2,
                            (triangle.getPoint(0).y + triangle.getPoint(1).y) / 2, tx, ty);
                }
            }
        }
    }

    private void rasterizeLine(ArrayList<Point> points, int color)
    {
        if (points.size() > 1)
        {
            lineRasterizer.setColor(darkMode ? color : 0x000000);

            switch (linePattern)
            {
                case 0: lineRasterizer.pattern = Pattern.FULL;        break;
                case 1: lineRasterizer.pattern = Pattern.DOTTED;      break;
                case 2: lineRasterizer.pattern = Pattern.DASHED;      break;
                case 3: lineRasterizer.pattern = Pattern.DASH_DOTTED; break;
            }

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
                        case KeyEvent.VK_D: 
                            linePattern++;
                            if (linePattern > 3) linePattern = 0;
                        break;

                        case KeyEvent.VK_T: triangleMode = !triangleMode; break;

                        case KeyEvent.VK_N: baseNormal = !baseNormal; break;


                        case KeyEvent.VK_C: 
                            polygon.clearPoints();
                            triangle.clearPoints();

                            flooded = false;
                        break;

                        case KeyEvent.VK_H: help = !help; break;

                        case KeyEvent.VK_B: darkMode = !darkMode; break;

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

                    if (!triangleMode)
                    {
                        previewLine(event.getX(), event.getY(), 0x00FFFF);

                        if (polygon.getPoints().isEmpty())
                        {
                            polygon.addPoints(new Point(event.getX(), event.getY()));
                        }
                    }
                    else if (triangle.getPoints().isEmpty())
                    {
                        triangle.addPoints(new Point(event.getX(), event.getY()));
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent event)
            {
                switch (event.getButton())
                {
                    case 1: 
                        if (!triangleMode)
                        {
                            polygon.addPoints(new Point(event.getX(), event.getY())); break;
                        }
                        else
                        {
                            if (triangle.getPoints().size() < 2)
                            {
                                triangle.addPoints(new Point(event.getX(), event.getY()));

                                if (triangle.getPoint(0).x == triangle.getPoint(1).x && 
                                    triangle.getPoint(0).y == triangle.getPoint(1).y)
                                {
                                    triangle.removePoint(1);
                                }
                            }
                            else
                            {
                                if (triangle.getPoints().size() < 3)
                                {
                                    tx = triangle.calculateIsosceles(event.getX(), event.getY()).getKey();
                                    ty = triangle.calculateIsosceles(event.getX(), event.getY()).getValue();

                                    triangle.addPoints(new Point(tx, ty));

                                    if (baseNormal)
                                    {
                                        triangle.addPoints(new Point(triangle.midpoint().x, triangle.midpoint().y), new Point(tx, ty));
                                    }
                                }
                            }
                        }
                    break;

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
                if (!triangleMode)
                {
                    previewLine(event.getX(), event.getY(), 0x00FFFF);
                }
                else
                {
                    if (triangle.getPoints().size() == 2)
                    {
                        tx = triangle.calculateIsosceles(event.getX(), event.getY()).getKey();
                        ty = triangle.calculateIsosceles(event.getX(), event.getY()).getValue();
                    }

                    if (triangle.getPoints().size() < 3)
                    {
                        previewLine(event.getX(), event.getY(), 0xFF2700);
                    }
                }

                px = event.getX();
                py = event.getY();
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                px = event.getX();
                py = event.getY();

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
                triangleScanline.setImage(window.image);

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