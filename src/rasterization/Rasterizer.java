package rasterization;

import java.awt.image.BufferedImage;

public abstract class Rasterizer
{
    protected int color;

    protected BufferedImage image;

    public void setColor(int color)
    {
        this.color = color;
    }

    public abstract void rasterize(double x0, double x1, double y0, double y1);
}