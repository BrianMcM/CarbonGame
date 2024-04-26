package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DialogPopup extends ScreenAdapter {
public static final Stage stage = new Stage();
public static Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));;

    public static class PopupDialog extends Dialog {
        public PopupDialog(String title, Skin skin) {
            super(title, skin);
        }

        public PopupDialog(String title, Skin skin, String windowStyleName) {
            super(title, skin, windowStyleName);
        }
        public PopupDialog(String title, WindowStyle windowStyle) {
            super(title, windowStyle);
        }

        {
            text("Are you sure you want to exit?");
            button("Yes",true);
            button("No",false);
        }
        @Override
        protected void result(Object object) {
            if((boolean) object) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);


        PopupDialog popup = new PopupDialog("Educational Popup", skin);
        popup.show(stage);
        Label label = new Label("adsfasdfdsfa",skin);
        stage.addActor(label);
    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        skin.dispose();

    }
}
