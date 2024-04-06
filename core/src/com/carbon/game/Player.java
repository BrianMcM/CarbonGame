package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.List;

public class Player extends GridLogic implements Moving{
    public GameScreen screen;
    //value variables
    public int carbon = 0;
    public final int cabCarbon = 10;
    public final int trainCarbon = 2;
    public final int busCarbon = 3;
    private int energy;
    public int gemScore = 0;
    public int cellX = 0;
    public int cellY = 0;

    //physical position for movement
    public Vector2 position = new Vector2(0,0);
    public Vector2 target = new Vector2(0,0);
    public Vector2 norm = new Vector2(0,0);
    private List<GridCell> path;

    public boolean hide = false;
    public int mode = 1; // 1-walking, 2-bike, 3-car
    public float exhausted = 1;
    //texture
    public Texture img = new Texture(Gdx.files.internal("testShapes/square.png"));
    public boolean move = false;
    public Transit transit = null;
    public Sound gemCollectSound = Gdx.audio.newSound(Gdx.files.internal("SFX/pickup.wav"));
    public Sound Exhaustedsound = Gdx.audio.newSound(Gdx.files.internal("SFX/Pacman-death-sound.mp3"));
    public boolean Exhausted_sound_played;
    public Sound Walk = Gdx.audio.newSound(Gdx.files.internal("SFX/Footstep.wav"));
    public Sound finishEarly = Gdx.audio.newSound(Gdx.files.internal("SFX/Stop.wav"));


    public Player(GameScreen screen, int e, int x, int y) {
        this.screen = screen;
        energy = e;
        setCell(x, y);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run () {
                changeEnergy(1);
            }
        }, 0, 1);
        Exhausted_sound_played = false;
    }

    public void collectGem(Gem gem) {
        gemScore += gem.value;
        gem.dispose();
        System.out.println("Gem Score +".concat(String.valueOf(gem.value)));
        gemCollectSound.play();
    }

    public void setCell(int cx, int cy) {
        cellX = cx;
        cellY = cy;
        position.set(v_cellToWorld(cx, cy));
        move = false;
    }

    public void transitCost(boolean train) {
        if (train) {
            carbon += trainCarbon;
        } else {
            carbon += busCarbon;
        }
    }

    //MOVEMENT FUNCTIONS
    public void arriveAtTarget() {
        if (mode == 3) {
            carbon += cabCarbon;
        } else {
            energy--;
        }
        cellX = pathFirst().getX();
        cellY = pathFirst().getY();
        position.set(target);
        move = false;
        //Footstep sounds
        //Walk.play();
    }

    public void finishEarly() {
        GridCell temp = pathFirst();
        clearPath();
        path.add(temp);
        finishEarly.play();
    }

    public void setPath(List<GridCell> list) {
        path = list;
        setTargets();
    }

    public void nextCell() {
        arriveAtTarget();
        path.remove(0);
        if (path.isEmpty()) {
            gemCheck();
            return;
        }
        setTargets();
    }

    public void setTargets() {
        target.set(v_cellToWorld(pathFirst().getX(), pathFirst().getY()));
        norm.x = Integer.compare(cellX, pathFirst().getX());
        norm.y = Integer.compare(cellY, pathFirst().getY());
        norm.nor();
        move = true;
    }

    public void clearPath() {
        path.clear();
    }

    public GridCell pathFirst() {
        return path.get(0);
    }

    public void exit() {
        screen.metroVision = false;
        screen.building = null;
        hide = false;
        transit = null;
    }

    public void gemCheck() {
        if (mode != 1) {
            return;
        }
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
        if (energy <= 0 && !Exhausted_sound_played) {
            exhausted = (float) 0.2;
            Exhaustedsound.play();
            Exhausted_sound_played = true;

        }
        if (exhausted < 1 && energy >= 10) {
            exhausted = 1;
            Exhausted_sound_played = false;
        }
    }

    public int getEnergy() {
        return energy;
    }

    public void dispose() {
        img.dispose();
        gemCollectSound.dispose();
        Exhaustedsound.dispose();
        Walk.dispose();
        finishEarly.dispose();
    }
}