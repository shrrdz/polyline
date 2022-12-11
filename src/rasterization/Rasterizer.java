package rasterization;

import java.awt.image.BufferedImage;

public abstract class Rasterizer
{
    public enum Pattern { FULL, DOTTED, DASHED, DASH_DOTTED };

    protected int color;

    public Pattern pattern;

    protected BufferedImage image;

    public void setColor(int color)
    {
        this.color = color;
    }

    public abstract void rasterize(double x0, double x1, double y0, double y1);
}