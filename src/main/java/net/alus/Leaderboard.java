package net.alus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leaderboard implements Serializable {
  private static Leaderboard instance;
  private final Map<String, Integer> scores;
  private String username;
  private Leaderboard() {
    scores = new HashMap<>();
    username = "Guest";
  }
  public static Leaderboard getInstance() {
    if(instance==null)
      instance = loadOrCreateLeaderboard();
    return instance;
  }

  private static Leaderboard loadOrCreateLeaderboard() {
    try (FileInputStream fileIn = new FileInputStream("./leaderboard");
      ObjectInputStream in = new ObjectInputStream(fileIn)) {
      return (Leaderboard) in.readObject();
    } catch (Exception ignore) {
      return new Leaderboard();
    }
  }

  private void save() {
    try (FileOutputStream fileOut = new FileOutputStream("./leaderboard");
         ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Map<String, Integer> getScores() {
    return new HashMap<>(scores);
  }

  public void saveScore(int score) {
    if(scores.getOrDefault(username, 0) < score) {
      scores.put(username, score);
      save();
    }
  }

  public void setUsername(String username) {
    this.username=username;
  }
}
