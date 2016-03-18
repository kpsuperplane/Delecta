package lib.localJade.tags;

import lib.localJade.Animation;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;


abstract public class ElementTag extends Tag{

    protected int top, left, bottom, right;

    private boolean hovered = false;

    public Map<String, Object> pre = new HashMap<String, Object>(){
        {
            put("top", 0);
            put("left", 0);
        }
    };
    ElementTag(){
        attrs.put("top", 0);
        attrs.put("left", 0);
        attrs.put("position", "relative");
    }

    abstract public int getHeight();
    abstract public int getWidth();

    public void updateBounds(int top, int left, int bottom, int right){
        this.top = Math.min(top, this.top);
        this.left = Math.min(left, this.left);
        this.bottom = Math.max(bottom, this.bottom);
        this.right = Math.max(right, this.right);
        if(this.parent != null){
            this.parent.updateBounds(top, left, bottom, right);
        }
    }

    public boolean contains(int x, int y){
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    public int getTop(Boolean rel){
        return (attrs.get("position").equals("relative")&&parent!=null?((ElementTag)parent).getTop(rel):0)+ (rel?(Integer)pre.get("top"):0) + (Integer)attrs.get("top");
    }
    public int getLeft(Boolean rel){
        return (attrs.get("position").equals("relative")&&parent!=null?((ElementTag)parent).getLeft(rel):0)+ (rel?(Integer)pre.get("left"):0) + (Integer)attrs.get("left");
    }
    public int getTop(){
        return (attrs.get("position").equals("relative")&&parent!=null?((ElementTag)parent).getTop(false):0)+(Integer)pre.get("top") + (Integer)attrs.get("top");
    }
    public int getLeft(){
        return (attrs.get("position").equals("relative")&&parent!=null?((ElementTag)parent).getLeft(false):0)+(Integer)pre.get("left") + (Integer)attrs.get("left");
    }

    public boolean triggerMouseEvent(String type, MouseEvent event){
        if(!contains(event.getX(), event.getY())) return false;
        int left = getLeft();
        int top = getTop();
        boolean toReturn = false;
        for (ListIterator iterator = children.listIterator(children.size()); iterator.hasPrevious();) {
            final Object child = iterator.previous();
            if(ElementTag.class.isInstance(child)){
                if(((ElementTag)child).triggerMouseEvent(type, event)){
                    toReturn = true;
                }
            }
        }
        if(event.getX() >= left && event.getX() <= left + getWidth() && event.getY() >= top && event.getY() <= top + getHeight()){
            if(type.equals("hover")){
                if(!hovered && this.attrs.containsKey("hover")){
                    hovered = true;
                    root.animationQueue.remove(this);
                    root.hovered.put(this, this.attrsBk);
                    Map<String, Object> newAttrs = parseAttr(this.attrs.get("hover").toString());
                    if(!transitions.isEmpty()){
                        Map<String, Animation> tmp = new HashMap<String, Animation>();
                        Iterator it = newAttrs.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            String key = pair.getKey().toString();
                            if(transitions.containsKey(key)){
                                Animation.Set tmpSet = (Animation.Set) transitions.get(key);
                                tmp.put(key, new Animation(this, key, attrs.get(key), newAttrs.get(key), tmpSet.duration, tmpSet.ease, root));
                                it.remove();
                            }
                        }
                        root.animationQueue.put(this, tmp);
                    }
                    root.view.requiresRepaint = true;
                    update(newAttrs);
                }
            }
            toReturn = true;
        }else{
            if(type.equals("hover")){
                if(hovered) {
                    hovered = false;
                    if(!transitions.isEmpty()){
                        root.animationQueue.remove(this);
                        Map<String, Object> newAttrs = new HashMap<String, Object>();
                        newAttrs.putAll(this.attrsBk);
                        Map<String, Animation> tmp = new HashMap<String, Animation>();
                        Iterator it = newAttrs.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            String key = pair.getKey().toString();
                            if(transitions.containsKey(key)){
                                Animation.Set tmpSet = (Animation.Set) transitions.get(key);
                                tmp.put(key, new Animation(this, key, this.attrs.get(key), newAttrs.get(key), tmpSet.duration, tmpSet.ease, root));
                                it.remove();
                            }
                        }
                        update(newAttrs);
                        root.animationQueue.put(this, tmp);
                    }
                    root.view.requiresRepaint = true;
                    root.restore("hover", this);
                }
            }
        }
        return toReturn;
    }

}
