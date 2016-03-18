package lib.localJade;

import lib.localJade.tags.Tag;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 3/17/2016.
 */
public class Animation {
    private Tag tag = null;
    private double factor = 1.0;
    private double percent = 0;
    private double add = 1;
    private String property;
    private Object start;
    private Object end;
    int type = 0;
    private Map<String, Double> eases = new HashMap<String, Double>(){{
        put("linear", 1.0);
        put("quad", 2.0);
        put("cubic", 3.0);
        put("quart", 4.0);
        put("quint", 5.0);
    }};
    public Animation(Tag tag, String property, Object start, Object end, int duration, String ease, LocalJade localJade){
        factor = eases.get(ease);
        add = localJade.view.frameRate/((double)duration);
        this.property = property;
        this.start = start;
        this.end = end;
        this.tag = tag;
        if(Integer.class.isInstance(start)){
            type = 1;
        }else if(Color.class.isInstance(start)){
            type = 2;
        }
    }
    public boolean isComplete(){
        return percent >= 1;
    }
    public double ease(double percent){
        double pow = Math.pow(percent, factor);
        return pow/(pow+Math.pow(1-percent, factor));
    }
    public void step(){
        percent += add;
        Object newProp = null;
        if(type == 1){
            int start = (Integer)this.start;
            int end = (Integer)this.end;
            newProp = (int)Math.round(start + (end-start) * ease(percent));
        }else if(type == 2){
            Color startCol = (Color)this.start;
            Color endCol = (Color)this.end;
            int start[] = {startCol.getRed(), startCol.getBlue(), startCol.getGreen(), startCol.getAlpha()};
            int end[] = {endCol.getRed(), endCol.getBlue(), endCol.getGreen(), endCol.getAlpha()};
            newProp = new Color(Math.max(Math.min((int)Math.round(start[0] + (end[0]-start[0]) * ease(percent)),255),0),Math.max(Math.min((int)Math.round(start[1] + (end[1]-start[1]) * ease(percent)),255),0),Math.max(Math.min((int)Math.round(start[2] + (end[2]-start[2]) * ease(percent)),255),0),Math.max(Math.min((int)Math.round(start[3] + (end[3]-start[3]) * ease(percent)),255),0));
        }
        if(type != 0) tag.update(property, newProp, false);
    }
    public static class Set{
        public int duration;
        public String ease;
        public Set(int duration, String ease){
            this.duration = duration;
            this.ease = ease;
        }
    }
}
