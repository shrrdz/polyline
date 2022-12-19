package rasterization;

import java.awt.image.BufferedImage;

public class LineRasterizer extends Rasterizer
{
    public LineRasterizer(BufferedImage image)
    {
        this.image = image;
    }

    private void pixel(int x, int y, int color, double alpha)
    {
        if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight())
        {
            int backgroundColor = image.getRGB(x, y);

            image.setRGB(x, y, blendColor(backgroundColor, color, alpha));
        }
    }

    private int blendColor(int background, int foreground, double alpha)
    {
        int r0 = (background >> 16) & 0xFF;
        int g0 = (background >> 8) & 0xFF;
        int b0 = background & 0xFF;
        
        int r1 = (foreground >> 16) & 0xFF;
        int g1 = (foreground >> 8) & 0xFF;
        int b1 = foreground & 0xFF;
        
        int r = (int) (r0 * (1 - alpha) + r1 * alpha);
        int g = (int) (g0 * (1 - alpha) + g1 * alpha);
        int b = (int) (b0 * (1 - alpha) + b1 * alpha);
        
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public void rasterize(double x0, double y0, double x1, double y1, boolean antiAliased)
    {
        double run = x1 - x0;
        double rise = y1 - y0;

        boolean steep = Math.abs(rise) > Math.abs(run);

        // if both endpoints have identical coordinates...
        if (x0 == x1 && y0 == y1)
        {
            // ... rasterize the pixel right away
            pixel((int) x0, (int) y0, color, 1);

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
                for (double x = steep ? y0 : x0; steep ? (x <= y1) : (x <= x1); x++)
                {
                    double y = k * x + q;

                    if (antiAliased)
                    {
                        double fraction = y - (int) y;
            
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1 - fraction);
    
                        if (fraction > 0)
                        {
                            pixel(steep ? ((int) y + 1) : (int) x, steep ? (int) x : ((int) y + 1), color, fraction);
                        }
                    }
                    else
                    {
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1);
                    }
                }
            break;

            case DOTTED:
                for (double x = steep ? y0 : x0; steep ? (x <= y1) : (x <= x1); x += 4)
                {
                    double y = k * x + q;

                    if (antiAliased)
                    {
                        double fraction = y - (int) y;
            
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1 - fraction);
    
                        if (fraction > 0)
                        {
                            pixel(steep ? ((int) y + 1) : (int) x, steep ? (int) x : ((int) y + 1), color, fraction);
                        }
                    }
                    else
                    {
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1);
                    }
                }
            break;

            case DASHED:
                for (double x = steep ? y0 : x0; steep ? (x <= y1) : (x <= x1); x++)
                {
                    double y = k * x + q;

                    if (antiAliased)
                    {
                        double fraction = y - (int) y;
            
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1 - fraction);
    
                        if (fraction > 0)
                        {
                            pixel(steep ? ((int) y + 1) : (int) x, steep ? (int) x : ((int) y + 1), color, fraction);
                        }
                    }
                    else
                    {
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1);
                    }

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
                for (double x = steep ? y0 : x0; steep ? (x <= y1) : (x <= x1); x++)
                {
                    double y = k * x + q;

                    if (antiAliased)
                    {
                        double fraction = y - (int) y;
            
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1 - fraction);
    
                        if (fraction > 0)
                        {
                            pixel(steep ? ((int) y + 1) : (int) x, steep ? (int) x : ((int) y + 1), color, fraction);
                        }
                    }
                    else
                    {
                        pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1);
                    }
                    
                    if (i < 8)
                    {
                        i++;
                    }
                    else
                    {
                        x += 4;
                        y = k * x + q;

                        if (antiAliased)
                        {
                            double fraction = y - (int) y;
                
                            pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1 - fraction);
        
                            if (fraction > 0)
                            {
                                pixel(steep ? ((int) y + 1) : (int) x, steep ? (int) x : ((int) y + 1), color, fraction);
                            }
                        }
                        else
                        {
                            pixel(steep ? (int) y : (int) x, steep ? (int) x : (int) y, color, 1);
                        }
                                                
                        x += 4;
                        i = 0;
                    }
                }
            break;
        }
    }
}