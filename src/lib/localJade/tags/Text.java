package lib.localJade.tags;

import lib.localJade.LocalJade;

import java.awt.*;
import java.util.LinkedList;
import java.util.Map;

public class Text extends ElementTag {

    private int height = 0, width = 0;

    private boolean updated = false;

    Font font = null;

	public Text(java.util.List<Tag> children, Map<String, Object> attrs, LocalJade root){
        /*-- Initialize Default Values --*/
        this.attrs.put("size", 12);
        this.attrs.put("font", "Arial");
        this.attrs.put("color", new Color(0,0,0,0));
        this.attrs.put("style", "PLAIN");
        this.attrs.put("text", "");
        this.pre.put("top", 40);
        this.pre.put("left", 7);

        this.root = root;
		this.children = children;

        update(attrs, true);

	}

	public Text(Map<String, Object> attrs, LocalJade root){
		this(new LinkedList<Tag>(), attrs, root);
	}

    public void update(Map<String, Object> attrs, boolean permanent) {
        this.attrs.putAll(attrs);
        if(permanent){
            this.attrsBk.putAll(this.attrs);
        }
        try {
            font = new Font((String)this.attrs.get("font"), Font.class.getField((String)this.attrs.get("style")).getInt(null), (Integer)this.attrs.get("size"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        updated = false;
        super.update();
    }

    public void draw(Graphics g) {

        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if(!updated){
            FontMetrics metrics = g.getFontMetrics(font);
            height = metrics.getHeight();
            width = metrics.stringWidth((String)attrs.get("text"));
            updated = true;
            int top = getTop();
            int left = getLeft();
            super.updateBounds(top, left, top + getHeight(), left + getWidth());
        }

        try {
            g.setColor((Color)attrs.get("color"));
            g.setFont(font);
        } catch (Exception e) {
            e.printStackTrace();
        }
        g.drawString((String)attrs.get("text"), getLeft(), getTop());
        super.draw(g);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }
}
