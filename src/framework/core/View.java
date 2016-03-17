package framework.core;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kevin on 3/11/2016.
 */
public class View {

    private Frame frame;
    private Graphics bufferGraphics;
    private Image offscreen;
    private Core core;
    public Object driver;
    private int width, height;
    public View(Core core) throws Exception {
        this.core = core;
        this.width =  Integer.valueOf(((HashMap<String, Object>) core.config.get("views")).get("width").toString());
        this.height =  Integer.valueOf(((HashMap<String, Object>) core.config.get("views")).get("height").toString());
        loadGUI();
    }
    private void loadGUI() throws Exception {
        offscreen = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        bufferGraphics = offscreen.getGraphics();
        Constructor<?> constructor = core.viewDriver.getConstructor();
        driver = constructor.newInstance();
        frame = new FrameworkWindow(core.config.get("appName").toString());
        frame.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent evt) {
                ((FrameworkWindow) frame).triggerMouseEvent("hover", evt);
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        frame.setVisible(true);
        Timer timer = new Timer();
        timer.schedule(new Refresh(), 0, 33);
    }

    public String getPath(){
        return ((HashMap<String, Object>) core.config.get("views")).get("directory").toString()+"/";
    }

    public void repaint(){
        try {
            if(bufferGraphics != null){
                bufferGraphics.setColor(Color.white);
                bufferGraphics.fillRect(0,0, this.width, this.height);
                bufferGraphics.setColor(Color.white);
                bufferGraphics.fillRect(0,0, this.width, this.height);
                Method method = core.viewDriver.getDeclaredMethod("paint", new Class[]{Graphics.class});
                method.invoke(driver, bufferGraphics);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadView(String view, Map<String, String> vars) throws Exception{
        Method method = core.viewDriver.getDeclaredMethod("loadView", new Class[]{String.class, Map.class, View.class});
        method.invoke(driver, getPath()+view, vars, this);
        repaint();
    }

    public void loadView(String view) throws Exception{
        loadView(view, new HashMap<String, String>());
    }
    class Refresh extends TimerTask {
        public void run() {
            frame.repaint();
        }
    }

    class FrameworkWindow extends Frame{
        public FrameworkWindow(String title) {
            super(title);
            setSize(width, height);
        }
        public void triggerMouseEvent(String type, MouseEvent event){
            Method method = null;
            try {
                method = core.viewDriver.getDeclaredMethod("triggerMouseEvent", new Class[]{String.class, MouseEvent.class});
                method.invoke(driver, type, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void update(Graphics g){
            g.drawImage(offscreen,0,0,this);
        }
    }
}
