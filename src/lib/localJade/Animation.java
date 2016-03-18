package lib.localJade;

import lib.localJade.tags.Tag;

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
    int type = 1;
    private Map<String, Double> eases = new HashMap<String, Double>(){{
        put("linear", 1.0);
        put("quad", 2.0);
    }};
    public Animation(Tag tag, String property, Object start, Object end, int duration, String ease){
        factor = eases.get(ease);
        add = duration/30*0.001;
        this.property = property;
        this.start = start;
        this.end = end;
        this.tag = tag;
        if(Integer.class.isInstance(start)){
            type = 1;
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
        if(type == 1){
            int start = (Integer)this.start;
            int end = (Integer)this.end;
            int newPos = (int)Math.round(start + (end-start) * percent);
            tag.update(property, newPos, false);
        }
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
