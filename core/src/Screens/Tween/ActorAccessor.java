package Screens.Tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.lang.annotation.Target;

public class ActorAccessor implements TweenAccessor<Actor> {
    public static final int Y = 2, RGB = 0, Alpha = 1;

    @Override
    public int getValues(Actor actor, int tweenType, float[] returnValues) {
        switch (tweenType){
            case Y:
                returnValues[0] = actor.getY();
                return 1;
            case RGB:
                returnValues[0] = actor.getColor().r;
                returnValues[1] = actor.getColor().g;
                returnValues[2] = actor.getColor().b;
                return 3;

            case Alpha:
                returnValues[0] = actor.getColor().a;
                return 1;
            default:
                assert false;
                return -1;
        }
    }
    @Override
    public void setValues(Actor actor, int tweenType, float[] newValues) {
        switch (tweenType){
            case Y:
                actor.setY(newValues[0]);
            case RGB:
                actor.setColor(newValues[0],newValues[1],newValues[2],actor.getColor().a);
                break;

            case Alpha:
                actor.setColor(actor.getColor().r,actor.getColor().g,actor.getColor().b,newValues[0]);
                break;
            default:
                assert false;

        }
    }
}