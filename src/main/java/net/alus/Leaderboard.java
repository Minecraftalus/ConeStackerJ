package net.alus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard implements Serializable {
  private static Leaderboard instance;
  private final List<Score> scores;
  private String username;
  private Leaderboard() {
    scores = new ArrayList<>();
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

  public List<Score> getScores() {
    return scores;
  }

  public void addScore(int score) {
    scores.add(new Score(username, score));
    save();
  }

  public void setUsername(String username) {
    this.username=username;
  }


  public record Score(String name, int score) implements Serializable {}
}
