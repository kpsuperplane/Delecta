package controllers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 3/11/2016.
 */
public class mainController extends baseController{

    public void main() throws Exception{
        Map<String, String> args = new HashMap<String, String>();
        args.put("name", "Frank");
        args.put("bg", "blue");
        View.loadView("main.lj", args);
        Thread.sleep(8000);
        Selector.find("#transparentBox").set("background", "yellow").find("#message").set("color", "black");
    }
}
