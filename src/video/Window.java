package video;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Window
{
    public final JFrame frame;
    public final JPanel panel;

    public BufferedImage image;

    public Window(int width, int height)
    {
        image = new BufferedImage(width, height, 1);

        panel = new JPanel()
        {
            @Override
            public void paintComponent(Graphics graphics)
            {
                super.paintComponent(graphics);

                graphics.drawImage(image, 0, 0, null);
            }
        };

        panel.setPreferredSize(new Dimension(width, height));

        frame = new JFrame();

        frame.add(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setTitle("polyline");
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // center the window

        panel.requestFocus();
        panel.requestFocusInWindow();
    }

    public void clear(int color)
    {
        Graphics graphics = image.getGraphics();

        graphics.setColor(new Color(color));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    public void update()
    {
        panel.repaint();
    }
    
    public void close()
    {
        frame.dispose();
    }
}