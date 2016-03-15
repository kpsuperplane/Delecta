package lib.localJade.tags;

import lib.localJade.LocalJade;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Tag {

    public ElementTag parent = null;
    public LocalJade root = null;
	public Map<String, Object> attrs = new HashMap<String, Object>(){{
        put("id", null);
    }};
	public java.util.List<Tag> children = new LinkedList<Tag>();

    public void setRoot(LocalJade root){
        this.root = root;
    }

    abstract public void update(Map<String, Object> attrs);

	public static Tag getTag(String name, Map<String, Object> attrs, LocalJade root){
		Tag temp = null;
		if(name.equals("rectangle")){
            temp = new Rectangle(attrs);
		}else if(name.equals("text")){
            temp = new Text(attrs);
        }else if(name.equals("include")){
            temp = new Include(attrs, root);
        }
        if(temp != null){
            temp.setRoot(root);
        }
        return temp;
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
