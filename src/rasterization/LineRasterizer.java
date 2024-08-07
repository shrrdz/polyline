package rasterization;

import java.awt.image.BufferedImage;

public class LineRasterizer extends Rasterizer
{
    public LineRasterizer(BufferedImage image)
    {
        this.image = image;
    }

    private void pixel(int x, int y, int color)
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

        // if both endpoints have identical coordinates...
        if (x0 == x1 && y0 == y1)
        {
            // ... rasterize the pixel right away
            pixel((int) x0, (int) y0, color);

            return;
        }

        if (steep ? (y0 > y1) : (x0 > x1))
        {
            // swap both x and y endpoints
            x1 = x0 + x1 - (x0 = x1);
            y1 = y0 + y1 - (y0 = y1);
        }

        // slope
        double k = steep ? run / rise : rise / run;
        // y-intercept
        double q = steep ? (x0 - k * y0) : (y0 - k * x0);
        
        int i = 0;

        switch (pattern)
        {
            case FULL:
                for (double x = steep ? y0 : x0; steep ? x <= y1 : x <= x1; x++)
                {
                    double y = k * x + q;

                    pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color);
                }
            break;

            case DOTTED:
                for (double x = steep ? y0 : x0; steep ? x <= y1 : x <= x1; x += 4)
                {
                    double y = k * x + q;

                    pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color);
                }
            break;

            case DASHED:
                for (double x = steep ? y0 : x0; steep ? x <= y1 : x <= x1; x++)
                {
                    double y = k * x + q;

                    pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color);

                    if (i < 8)
                    {
                        i++;
                    }
                    else
                    {
                        x += 4;
                        i = 0;
                    }
                }
            break;

            case DASH_DOTTED:
                for (double x = steep ? y0 : x0; steep ? x <= y1 : x <= x1; x++)
                {
                    double y = k * x + q;

                    pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color);

                    if (i < 8)
                    {
                        i++;
                    }
                    else
                    {
                        x += 4;
                        y = k * x + q;

                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color);
                        
                        x += 4;
                        i = 0;
                    }
                }
            break;
        }
    }
}