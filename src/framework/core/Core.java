package framework.core;

import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Kevin on 3/11/2016.
 */
public class Core {
    HashMap<String, Object> config = null;
    Class<?> viewDriver;
    View view = null;
    public void loadController(String controller) throws Exception{

        /* Load Class */
        Class<?> controllerClass = Class.forName("controllers."+controller);
        Constructor<?> constructor = controllerClass.getConstructor();
        Object instance = constructor.newInstance();

        /* Init Class */
        Method method = controllerClass.getMethod("init", new Class[]{View.class});
        method.invoke(instance, view);

        /* Run `main()` */
        Method mainMethod = controllerClass.getMethod("main");
        mainMethod.invoke(instance);
    }
    public void bootstrap(){
        try{
            config = (HashMap<String, Object>)new JSONParser().parse(new FileReader("config/app.json"));
            viewDriver = Class.forName(((HashMap<String, Object>)config.get("views")).get("driver").toString());
            view = new View(this);
            loadController(((HashMap<String, Object>)config.get("controllers")).get("init").toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
