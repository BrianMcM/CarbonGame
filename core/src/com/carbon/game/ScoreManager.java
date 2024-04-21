package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class ScoreManager {
    private final String SCORES_FILE_PATH = "Scores/scores.json";
    Gson gson = new Gson();

    public void saveScore(String name, String score) {
        String[] array = {name, score};

        String json = gson.toJson(array);

        FileHandle file = Gdx.files.local(SCORES_FILE_PATH);
        file.writeString(json, true);
    }

    public List<String[]> loadScores() {
        try (FileReader reader = new FileReader(SCORES_FILE_PATH)) {
            return gson.fromJson(reader, List.class);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
