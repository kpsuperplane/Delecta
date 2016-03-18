package lib.localJade;

import java.awt.*;

/**
 * Created by Kevin on 3/18/2016.
 */
public class ColorHelper {
    public static boolean isColor(String color){
        if(color.length() < 4) return false;
        String front = color.substring(0, 4);
        if(front.equals("rgba") || front.equals("rgb[") || front.equals("col[")){
            return true;
        }
        return false;
    }
    public static Color getColor(String color) {
        try{
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
                color = color.substring(4, color.length()-1);
                if(color.equals("transparent")){
                    return new java.awt.Color(0,0,0,0);
                }else{
                    return (java.awt.Color) Color.class.getField(color).get(null);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return Color.black;
        }
    }
}
