package rasterization;

import java.awt.image.BufferedImage;

public class LineRasterizer extends Rasterizer
{
    public LineRasterizer(BufferedImage image)
    {
        this.image = image;
    }

    private void drawPixel(int x, int y, int color)
    {
        if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight())
        {
            image.setRGB(x, y, color);
        }
    }

    @Override
    public void rasterize(double x0, double y0, double x1, double y1)
    {
        double run = x1 - x0;
        double rise = y1 - y0;

        boolean steep = Math.abs(rise) > Math.abs(run);

        if (steep ? (y0 > y1) : (x0 > x1))
        {
            // swap both x and y endpoints
            x1 = x0 + x1 - (x0 = x1);
            y1 = y0 + y1 - (y0 = y1);
        }

        // slope
        double k = steep ? (x1 - x0) / (y1 - y0) : (y1 - y0) / (x1 - x0);
        // y-intercept
        double q = steep ? (x0 - k * y0) : (y0 - k * x0);
        
        for (double x = steep ? y0 : x0; steep ? (x <= y1) : (x <= x1); x++)
        {
            double y = k * x + q;

            drawPixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color);
        }
    }
}