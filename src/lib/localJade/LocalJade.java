package lib.localJade;

import framework.core.View;
import lib.localJade.tags.ElementTag;
import lib.localJade.tags.Tag;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class LocalJade{

	java.util.List<Tag> tags = new LinkedList<Tag>();
    public View view = null;
    private LocalJade root = this;
    public ElementTag parent = null;
    public Selector selector = new Selector(this);
    public Map<Tag, Map<String, Object>> hovered = new HashMap<Tag, Map<String, Object>>();
    public Map<String, Object> attrBk = new HashMap<String, Object>();
    public Map<String, String> vars = null;

    public Map<String, Tag> ids = new HashMap<String, Tag>();
    public Map<String, LinkedList<Tag>> classes = new HashMap<String, LinkedList<Tag>>();

    public Map<Tag, Map<String, Animation>> animationQueue = new HashMap<Tag, Map<String, Animation>>();

    public void paint(Graphics g) {
        Iterator it = animationQueue.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Map<String, Animation> curSet = ((Map<String, Animation>)pair.getValue());
            Iterator it2 = curSet.entrySet().iterator();
            while(it2.hasNext()){
                Map.Entry pair2 = (Map.Entry)it2.next();
                Animation cur = (Animation)pair2.getValue();
                cur.step();
                if(cur.isComplete()){
                    it2.remove();
                }
            }
            if(curSet.isEmpty()){
                it.remove();
            }
        }
        if(!animationQueue.isEmpty()){
            view.requiresRepaint = true;
        }
        for(Tag tag : tags){
        	tag.draw(g);
        }
    }
    public void restore(String type){
        Iterator it = hovered.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ((ElementTag)pair.getKey()).update((Map<String, Object>)pair.getValue());
            it.remove();
        }
    }
	public void loadView(String filename, Map<String, String> vars, View view) throws Exception{
        tags.clear();
        this.view = view;
        this.vars = vars;
        Stack<Tag> tagStack = new Stack<Tag>();
        tagStack.push(null);
		BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        while (line != null) {
            int tabCount = 0;
            for(; tabCount < line.length(); tabCount++) if(line.charAt(tabCount) != '\t') break; //Count the number of tabs
            while(tabCount < tagStack.size()){
                tagStack.pop();
            }
            line = line.substring(tabCount);
            if(!line.isEmpty()){ //make sure there is something on this line
                int startAttr = line.indexOf("(");
                int textStart = line.indexOf("|");
                if(textStart < line.lastIndexOf(")")){
                    textStart = -1;
                }
                String text = "";
                int tagEnd = startAttr != -1 ? startAttr : textStart != -1 ? textStart : line.length();
                String tag = line.substring(0, tagEnd);
                Map<String, Object> attrs = new HashMap<String, Object>();
                if(startAttr != -1){
                    int endAttr = line.lastIndexOf(")");
                    String attrFull[] = line.substring(startAttr+1, endAttr).split(",");
                    for(String attr: attrFull){
                        int equals = attr.indexOf("=");
                        String val = attr.substring(equals+1).trim();
                        Object insert = val;
                        if(val.length() > 1 && ((val.charAt(0) == '"' && val.charAt(val.length()-1) == '"') || (val.charAt(0) == '\'' && val.charAt(val.length()-1) == '\''))){
                            val = val.substring(1, val.length()-1);
                            insert = val;
                        }else if(val.length() > 0 && val.charAt(0) == '@'){
                            val = vars.get(val.substring(1));
                            insert = val;
                        }else{
                            insert = Integer.valueOf(val);
                        }
                        attrs.put(attr.substring(0, equals).trim(), insert);
                    }
                }
                if(textStart != -1){
                    text = line.substring(textStart+1);
                    int index = text.indexOf("@{");
                    while (index >= 0) {
                        int endIndex = text.indexOf("}", index+1);
                        text = text.substring(0, index) + vars.get(text.substring(index+2, endIndex)) + text.substring(endIndex+1);
                        index = text.indexOf("@", index + 1);
                    }
                    attrs.put("text", text);
                }
                Tag tmp = Tag.getTag(tag, attrs, root);
                if(tabCount == 0){
                    tmp.parent = parent;
                    tags.add(tmp);
                }else{
                    tmp.parent = (ElementTag)tagStack.peek();
                    tagStack.peek().children.add(tmp);
                }
                tagStack.push(tmp);
            }

            line = br.readLine();
        }
	}

    public java.util.List<Tag> loadViewTags(String filename, LocalJade root, ElementTag parent) throws Exception {
        this.parent = parent;
        this.root = root;
        loadView(filename, root.vars, root.view);
        return tags;
    }

    public void triggerMouseEvent(String type, MouseEvent event){
        int oldCount = hovered.size();
        restore("hover");
        hovered.clear();
        for (ListIterator iterator = tags.listIterator(tags.size()); iterator.hasPrevious();) {
            final Object child = iterator.previous();
            if(ElementTag.class.isInstance(child)){
                if(((ElementTag)child).triggerMouseEvent(type, event)){
                    view.repaint();
                    return;
                }
            }
        }
        if(hovered.size() != oldCount){
            view.repaint();
        }

    }

	public void loadView(String filename, View parent) throws Exception{
		loadView(filename, new HashMap<String, String>(), parent);
	}


}
