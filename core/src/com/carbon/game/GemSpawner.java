package com.carbon.game;

import com.badlogic.gdx.utils.Timer;

import java.util.*;

public class GemSpawner {
    public final Map map;
    public final GameScreen screen;
    public final Timer timer = new Timer();
    private int valueIndex = 0;
    private final Random rng = new Random();
    private final ArrayList<int[]> inUseCells = new ArrayList<>();
    private final List<Integer> scores = new ArrayList<>(Arrays.asList(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 200, 200, 200, 200, 200, 300, 300, 300, 400, 500));
    public GemSpawner(Map map, GameScreen screen, int num) {
        this.map = map;
        this.screen = screen;
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run () {
                spawn();
            }
        }, 0, 10, 18);
        Collections.shuffle(scores);
        spawnAll(num);
    }

    public void spawnAll(int num) {
        for (int n = 0; n < num; n++) {
            spawn();
        }
    }

    public void spawn() {
        int[] spawnCoords;
        do {
            int randInt = rng.nextInt(map.walkableTiles.size());
            spawnCoords = map.walkableTiles.get(randInt);
        } while (inUseCells.contains(spawnCoords));
        screen.gemList.add(new Gem(scores.get(valueIndex), spawnCoords, this));
        inUseCells.add(spawnCoords);
        valueIndex++;
    }

    public void collected(int[] coords) {
        inUseCells.remove(coords);
    }
}
