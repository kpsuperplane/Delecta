package lib.localJade.tags;

import lib.localJade.LocalJade;

import java.awt.*;
import java.util.*;

public class Rectangle extends ElementTag {

	public Rectangle(java.util.List<Tag> children, Map<String, Object> attrs, LocalJade root){
        /*-- Initialize Default Values --*/
        this.attrs.put("background", "transparent");
        this.attrs.put("width", 150);
        this.attrs.put("height", 100);
		this.attrs.put("click", null);
        this.pre.put("top", 31);
        this.pre.put("left", 8);

		this.root = root;
		this.children = children;

		update(attrs);
	}
	
	public Rectangle(Map<String, Object> attrs, LocalJade root){
		this(new LinkedList<Tag>(), attrs, root);
	}

	@Override
	public void update(Map<String, Object> attrs) {
        this.attrs.putAll(attrs);
        int top = getTop();
        int left = getLeft();
        super.updateBounds(top, left, top + getHeight(), left + getWidth());
		super.update();
	}

	public void draw(Graphics g) {
        try {
			g.setColor(getColor("background"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        g.fillRect(getLeft(), getTop(), (Integer)attrs.get("width"), (Integer)attrs.get("height"));
        super.draw(g);
	}

	@Override
	public int getHeight() {
		return (Integer)attrs.get("height");
	}

	@Override
	public int getWidth() {
		return (Integer)attrs.get("width");
	}
}
