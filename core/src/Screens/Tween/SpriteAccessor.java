package Screens.Tween;


import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class SpriteAccessor implements TweenAccessor<Sprite> {
    public static final int Alpha = 0;

    @Override
    public int getValues(Sprite sprite, int tweenType, float[] returnValues) {
        switch (tweenType){
            case Alpha:
                returnValues[0] = sprite.getColor().a;
                return 1;
            default:
                assert false;
                return -1;
        }
    }
    @Override
    public void setValues(Sprite sprite, int tweenType, float[] newValues) {
        switch (tweenType){
            case Alpha:
                sprite.setColor(sprite.getColor().r,sprite.getColor().g,sprite.getColor().b,newValues[0]);
                break;
            default:
                assert false;

        }
    }
}
