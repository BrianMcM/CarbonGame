package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DialogScreen extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    public DialogScreen() {
        super();
    }


    public static class ExitDialog extends Dialog{
        public ExitDialog(String title, Skin skin, String windowStyleName) {
            super(title, skin, windowStyleName);
        }
        public ExitDialog(String title, Skin skin) {
            super(title, skin);
        }
        public ExitDialog(String title, WindowStyle windowStyle) {
            super(title, windowStyle);
        }

        {
            text("Are you sure you want to exit?");
            button("Yes");
            button("No");
        }
        @Override
        protected void result(Object object) {
            super.result(object);
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage = new Stage());
        skin = new Skin(Gdx.files.internal("ui/inbetween.txt"));
        ExitDialog exitDialog = new ExitDialog("Confirm Exit",skin);
        exitDialog.show(stage);
    }

    @Override
    public void hide() {
        dispose();
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
        stage.dispose();
        skin.dispose();
    }
}
