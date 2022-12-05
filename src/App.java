import geometry.Point;
import rasterization.LineRasterizer;
import video.Window;

public class App
{
    private LineRasterizer lineRasterizer;

    private final Window window = new Window(800, 600);
    
    private void start()
    {
        lineRasterizer = new LineRasterizer(window.image);

        update();
    }

    private void update()
    {
        window.clear(0x050505);

        rasterizeLine(new Point(0, 0), new Point(799, 599), 0x6097BA);

        window.update();
    }

    private void rasterizeLine(Point p0, Point p1, int color)
    {
        lineRasterizer.setColor(color);

        lineRasterizer.rasterize(p0.x, p0.y, p1.x, p1.y);
    }

    public static void main(String[] args)
    {
        new App().start();
    }
}