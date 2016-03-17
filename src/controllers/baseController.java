package controllers;

import framework.core.View;
import lib.localJade.*;


/**
 * Created by Kevin on 3/11/2016.
 */
public class baseController {
    protected View View;
    protected Selector Selector;
    public void init(View View){
        this.View = View;
        this.Selector = new Selector((LocalJade)View.driver);
    }

}
