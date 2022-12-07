package fill;

import java.awt.image.BufferedImage;

public abstract class Fill
{
    protected int fillColor;

    protected BufferedImage image;

    public void setImage(BufferedImage image)
    {
        this.image = image;
    }

    public abstract void fill();
}