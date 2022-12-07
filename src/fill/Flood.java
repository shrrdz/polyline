package fill;

public class Flood extends Fill
{
    private int x, y;
    
    private int backgroundColor;

    public void setNode(int x, int y, int fillColor)
    {
        this.x = x;
        this.y = y;

        this.fillColor = fillColor;
    }

    @Override
    public void fill()
    {
        this.backgroundColor = image.getRGB(x, y);

        flood(x, y);
    }

    private void flood(int x, int y)
    {
        if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight())
        {
            int pixelColor = image.getRGB(x, y);

            if (pixelColor == backgroundColor && pixelColor != fillColor)
            {
                image.setRGB(x, y, fillColor);

                flood(x + 1, y);
                flood(x - 1, y);
                flood(x, y + 1);
                flood(x, y - 1);
            }
        }
    }
}