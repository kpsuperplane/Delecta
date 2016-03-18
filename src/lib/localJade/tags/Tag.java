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
                transitions.put(pair.getKey().toString(), new Animation.Set(Integer.valueOf(pair.getValue().toString()), "quad"));
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

    private String getVar(String var){
        return root.vars.getOrDefault(var, null);
    }

    protected Map<String, Object> parseAttr(String line){
        int startAttr = line.indexOf("[");
        Map<String, Object> subAttrs = new HashMap<String, Object>();
        if(startAttr != -1){
            int endAttr = line.lastIndexOf("]");
            String attrFull[] = line.substring(startAttr+1, endAttr).split("\\|");
            for(String attr: attrFull){
                String parts[] = attr.split("=");
                String val = parts[1].trim();
                Object insert = val;
                if(val.length() > 1 && ((val.charAt(0) == '"' && val.charAt(val.length()-1) == '"') || (val.charAt(0) == '\'' && val.charAt(val.length()-1) == '\''))){
                    val = val.substring(1, val.length()-1);
                    insert = val;
                }else if(val.length() > 0 && val.charAt(0) == '@'){
                    val = getVar(val.substring(1));
                    insert = val;
                }else{
                    insert = Integer.valueOf(val);
                }
                subAttrs.put(parts[0].trim(), insert);
            }
        }
        return subAttrs;
    }

    public Color getColor(String field) throws Exception {
        String color = attrs.get(field).toString();
        if(color.length() > 6 && color.substring(0, 5).equals("rgba[") && color.substring(color.length()-1).equals("]")){
            String colorstr = color.substring(5, color.length()-1);
            return new Color(
                    Integer.valueOf( colorstr.substring( 0, 2 ), 16 ),
                    Integer.valueOf( colorstr.substring( 2, 4 ), 16 ),
                    Integer.valueOf( colorstr.substring( 4, 6 ), 16 ),
                    Integer.valueOf( colorstr.substring( 6, 8 ), 16 ) );

        }else if(color.length() > 5 && color.substring(0, 4).equals("rgb[") && color.substring(color.length()-1).equals("]")){
            String colorstr = color.substring(4, color.length()-1);
            return new Color(
                    Integer.valueOf( colorstr.substring( 0, 2 ), 16 ),
                    Integer.valueOf( colorstr.substring( 2, 4 ), 16 ),
                    Integer.valueOf( colorstr.substring( 4, 6 ), 16 ));

        }else{
            if(color.equals("transparent")){
                return new Color(0,0,0,0);
            }else{
                return (Color)Color.class.getField(color).get(null);
            }
        }
    }
    public void draw(Graphics g){
        for(Tag child : children){
            child.draw(g);
        }
    }
}
