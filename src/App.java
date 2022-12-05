import video.Window;

public class App
{
    private final Window window = new Window(800, 600);
    
    public static void main(String[] args)
    {
        App app = new App();

        app.window.clear(0x050505);
    }
}