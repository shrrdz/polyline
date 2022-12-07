import geometry.Point;
import rasterization.LineRasterizer;
import video.Window;
import shape.Polygon;

import java.awt.event.*;
import java.util.ArrayList;

public class App
{
    private enum Input { LEFT, RELEASED };

    private Input input;

    private LineRasterizer lineRasterizer;

    private final Polygon polygon = new Polygon();

    private final Window window = new Window(800, 600);
    
    private void start()
    {
        lineRasterizer = new LineRasterizer(window.image);

        initialize();
        update();
    }

    private void update()
    {
        window.clear(0x050505);

        rasterizeLine(polygon.getPoints(), 0x6097BA);

        window.update();
    }

    private void rasterizeLine(ArrayList<Point> points, int color)
    {
        if (points.size() > 1)
        {
            lineRasterizer.setColor(color);

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
                        case KeyEvent.VK_C: polygon.clearPoints(); break;

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
                }
            }

            @Override
            public void mouseReleased(MouseEvent event)
            {
                switch (event.getButton())
                {
                    case 1: polygon.addPoints(new Point(event.getX(), event.getY())); break;
                }

                input = Input.RELEASED;

                update();
            }
        };

        MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent event)
            {
                update();
            }
        };

        window.panel.addKeyListener(keyListener);
        window.panel.addMouseListener(mouseAdapter);
        window.panel.addMouseMotionListener(mouseMotionAdapter);
    }

    public static void main(String[] args)
    {
        new App().start();
    }
}