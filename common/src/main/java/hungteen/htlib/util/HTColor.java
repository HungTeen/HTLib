package hungteen.htlib.util;

import hungteen.htlib.util.helper.ColorHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/30 10:28
 */
public record HTColor(int red, int green, int blue, int alpha, boolean hasAlpha) {

    public float get(int id){
        return ColorHelper.to(id == 0 ? red() :
                id == 1 ? green() :
                        id == 2 ? blue() : alpha());
    }

    public int rgb(){
        return ColorHelper.toRGB(red(), green(), blue());
    }

    public float[] rgba(){
        return new float[]{get(0), get(1), get(2), get(3)};
    }

}