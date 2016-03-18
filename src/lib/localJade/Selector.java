package lib.localJade;

import lib.localJade.tags.Tag;

import java.util.LinkedList;

/**
 * Created by Kevin on 3/17/2016.
 */
public class Selector{
    LinkedList<Tag> elements = new LinkedList<Tag>();
    private LocalJade instance = null;
    public Selector(LocalJade instance){
        this.instance = instance;
    }
    public Selector(Selector cpy){
        elements.addAll(cpy.elements);
        instance = cpy.instance;
    }
    public Selector add(String selector, Selector instance){
        if(instance!=null){
            if(selector.charAt(0) == '#'){
                elements.push(instance.instance.ids.get(selector.substring(1)));
            }else if(selector.charAt(0) == '.'){
                elements.addAll(instance.instance.classes.get(selector.substring(1)));
            }
        }
        return instance;
    }
    public Selector add(String selector){
        add(selector, new Selector(this));
        return this;
    }
    public Selector find(String selector){
        Selector tmp = new Selector(this);
        tmp.elements.clear();
        add(selector, tmp);
        return this;
    }
    public Selector set(String key, String val){
        for(Tag element : elements){
            element.update(key, val);
        }
        instance.view.repaint();
        return this;
    }
    public String toString(){
        return this.elements.toString();
    }
    public Tag get(int idx){
        return elements.get(idx);
    }
}
