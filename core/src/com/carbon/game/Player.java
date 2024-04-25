package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;

public class Player extends FreeRoam {
    public final GameScreen screen;
    //constants
    public static final int TRAIN_CARBON = 2;
    public static final int BUS_CARBON = 3;

    public static int carbon = 0;
    public static int energy;
    public static int score = 0;
    public boolean hide = false;
    public int mode = 1; // 1-walking, 2-bike, 3-car
    public float exhausted = 1;

    public boolean xFlip = true;
    //texture
    private final Texture idle_0 = new Texture(Gdx.files.internal("testShapes/idle_0.png"));
    private final Texture idle_1 = new Texture(Gdx.files.internal("testShapes/idle_1.png"));
    private final Texture idle_2 = new Texture(Gdx.files.internal("testShapes/idle_2.png"));
    private final Texture running_0 = new Texture(Gdx.files.internal("testShapes/running_0.png"));
    private final Texture running_1 = new Texture(Gdx.files.internal("testShapes/running_1.png"));
    private final Texture running_2 = new Texture(Gdx.files.internal("testShapes/running_2.png"));
    private final Texture running_3 = new Texture(Gdx.files.internal("testShapes/running_3.png"));
    private final Texture bikeSprite = new Texture(Gdx.files.internal("testShapes/player_bike.png"));
    public Texture img = idle_0;
    public Transit transit = null;
    public Car car = null;
    public boolean carCalled = false;

    public Sound gemCollectSound = Gdx.audio.newSound(Gdx.files.internal("SFX/gem_pickup.mp3"));
    public Sound Exhaustedsound = Gdx.audio.newSound(Gdx.files.internal("SFX/Tired_breath_final.wav"));
    public boolean Exhausted_sound_played = false;

    public Player(GameScreen screen, int e, int x, int y) {
        super(x, y);
        this.screen = screen;
        energy = e;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run () {
                changeEnergy(1);
            }
        }, 0, 1);

        Timer animTimer = new Timer();
        animTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run () {
                changeFrame();
            }
        }, 0, (float) 0.125);
    }

    public void collectGem(Gem gem) {
        score += gem.value;
        gemCollectSound.play();
        gem.dispose();
        System.out.println("Gem Score +".concat(String.valueOf(gem.value)));
    }

    public void transitCost(boolean train) {
        if (train) {
            carbon += TRAIN_CARBON;
        } else {
            carbon += BUS_CARBON;
        }
    }

    //MOVEMENT FUNCTIONS
    public void arriveAtTarget() {
        if (energy > 0 && mode != 3) {
            energy--;
        }
        super.arriveAtTarget();
    }

    public void setTargets() {
        super.setTargets();
        if (norm.x > 0) {
            xFlip = false;
        } else {
            xFlip = true;
        }
    }

    protected void arrived() {
        if (mode == 1) {
            gemCheck();
        }
        if (mode == 3) {
            mode = 1;
            car.dropOff();
            img = idle_0;
            screen.allowClick();
        }
    }

    public void exit() {
        screen.metroVision = false;
        screen.stationInside = null;
        hide = false;
        transit = null;
    }

    private void gemCheck() {
        int i = 0;
        for (Gem gem : screen.gemList) {
            if (cellX == gem.cellCoords[0] && cellY == gem.cellCoords[1]) {
                screen.gemList.remove(i);
                collectGem(gem);
                return;
            }
            i++;
        }
    }

    public void changeEnergy(int num) {
        if (energy < 100) {
            energy += num;
        }
        if (energy <= 10) {
            exhausted = (float) 0.2;
            if (!Exhausted_sound_played) {
                Exhaustedsound.play();
                Exhausted_sound_played = true;
            }
        }
        if (exhausted < 1 && energy >= 10) {
            exhausted = 1;
            Exhausted_sound_played = false;
        }
    }

    public void onBike() {
        mode = 2;
        img = bikeSprite;
    }

    public void offBike() {
        mode = 1;
        img = idle_0;
    }

    //My own personal animation player. I recognize that this is possibly the
    //dumbest way to do it but ive had a really long day.
    private void changeFrame() {
        //only if walking
        if (mode != 1) {
            return;
        }
        if (super.move) {
            if (img == running_0) {
                img = running_1;
            } else if (img == running_1){
                img = running_2;
            } else if (img == running_2){
                img = running_3;
            } else {
                img = running_0;
            }
        } else {
            if (img == idle_0) {
                img = idle_1;
            } else if (img == idle_1){
                img = idle_2;
            } else {
                img = idle_0;
            }
        }
    }

    public void dispose() {
        img.dispose();
        gemCollectSound.dispose();
        Exhaustedsound.dispose();
    }
}