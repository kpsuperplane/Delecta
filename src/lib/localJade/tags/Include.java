package lib.localJade.tags;

import lib.localJade.LocalJade;

import java.awt.*;
import java.util.LinkedList;
import java.util.Map;

public class Include extends ElementTag {

    public Include(java.util.List<Tag> children, Map<String, Object> attrs, LocalJade root){
        /*-- Initialize Default Values --*/
        this.attrs.put("file", null);
        this.attrs.put("top", 0);
        this.attrs.put("left", 0);

        this.children = children;
        this.attrs.putAll(attrs);
        this.root = root;
        update(attrs, true);
    }

    public Include(Map<String, Object> attrs, LocalJade root){
        this(new LinkedList<Tag>(), attrs, root);
    }

    public void update(Map<String, Object> attrs, boolean permanent) {
        this.attrs.putAll(attrs);
        if(permanent){
            this.attrsBk.putAll(this.attrs);
        }
        if(this.attrs.get("file") != null){
            try {
                this.children = new LocalJade().loadViewTags(root.view.getPath()+this.attrs.get("file").toString(), root, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int top = getTop();
        int left = getLeft();
        super.updateBounds(top, left, top + getHeight(), left + getWidth());
        super.update();
    }

    public void draw(Graphics g) {
        super.draw(g);
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }
}
