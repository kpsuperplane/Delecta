package lib.localJade.tags;

import lib.localJade.Animation;
import lib.localJade.LocalJade;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public abstract class Tag {

    public ElementTag parent = null;
    public LocalJade root = null;
	public Map<String, Object> attrs = new HashMap<String, Object>(){{
        put("id", null);
        put("class", null);
    }};
    public Map<String, Object> attrsBk = new HashMap<String, Object>();
    public Map<String, Animation.Set> transitions = new HashMap<String, Animation.Set>();

    public java.util.List<Tag> children = new LinkedList<Tag>();

    public void setRoot(LocalJade root){
        this.root = root;
    }

    public abstract void update(Map<String, Object> attrs, boolean permanent);
    public void update(Map<String, Object> attrs){
        update(attrs, false);
    }

    public void update(String key, Object val, boolean permanent){
        attrs.put(key, val);
        if(permanent) attrsBk.put(key, val);
    }
    public void update(String key, Object val){
        update(key, val, true);
    }

    public void update(){
        if(attrs.get("id") != null){
            root.ids.put(attrs.get("id").toString(), this);
        }
        if(attrs.get("class") != null){
            if(!root.classes.containsKey(attrs.get("class"))){
                root.classes.put(attrs.get("class").toString(), new LinkedList<Tag>());
            }
            root.classes.get(attrs.get("class").toString()).push(this);
        }
        if(attrs.get("transition") != null){
            Map<String, Object> tmp = parseAttr(attrs.get("transition").toString());
            Iterator it = tmp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String easing = "linear", attr = pair.getKey().toString();
                int splitter = attr.indexOf(":");
                if (splitter != -1) {
                    easing = attr.substring(splitter+1);
                    attr = attr.substring(0, splitter);
                }
                transitions.put(attr, new Animation.Set(Integer.valueOf(pair.getValue().toString()), easing));
            }
        }
    }

    public ElementTag toElement(){
        return (ElementTag)this;
    }

	public static Tag getTag(String name, Map<String, Object> attrs, LocalJade root){
		Tag temp = null;
		if(name.equals("rectangle")){
            temp = new Rectangle(attrs, root);
		}else if(name.equals("text")){
            temp = new Text(attrs, root);
        }else if(name.equals("include")){
            temp = new Include(attrs, root);
        }
        return temp;
	}


    protected Map<String, Object> parseAttr(String line){
        int startAttr = line.indexOf("[");
        Map<String, Object> subAttrs = new HashMap<String, Object>();
        if(startAttr != -1){
            int endAttr = line.lastIndexOf("]");
            String attrFull[] = line.substring(startAttr+1, endAttr).split("\\|");
            for(String attr: attrFull){
                try{
                    String parts[] = attr.split("=");
                    String val = parts[1].trim();
                    subAttrs.put(parts[0].trim(), root.parseTag(val));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return subAttrs;
    }

    public void draw(Graphics g){
        for(Tag child : children){
            child.draw(g);
        }
    }
}
