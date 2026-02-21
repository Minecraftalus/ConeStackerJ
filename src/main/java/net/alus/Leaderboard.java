package net.alus;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class Leaderboard implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private static Leaderboard instance;
  private final Map<String, Integer> scores;
  private String username;
  private static String filePath = "./leaderboard";
  private static boolean isServer = false;

  private Leaderboard() {
    scores = new HashMap<>();
    username = "Guest";
  }

  public static void init(boolean isServer) {
    if(instance==null) {
      if(isServer) {
        filePath = "./leaderboardSERVER";
        Leaderboard.isServer = true;
      }
      instance = loadOrCreateLeaderboard();
    }
  }

  public static Leaderboard getInstance() {
    return instance;
  }

  private static Leaderboard loadOrCreateLeaderboard() {
    try (FileInputStream fileIn = new FileInputStream(filePath);
      ObjectInputStream in = new ObjectInputStream(fileIn)) {
      return (Leaderboard) in.readObject();
    } catch (Exception ignore) {
      return new Leaderboard();
    }
  }

  public Map<String, Integer> getScores() {
    return new HashMap<>(scores);
  }

  public synchronized void saveScore(int score) {
    if(scores.getOrDefault(username, 0) < score) {
      scores.put(username, score);
      if(!isServer)
        NetworkHandler.sendScoreToServer(username, score);
      save();
    }
  }

  private void save() {
    try (FileOutputStream fileOut = new FileOutputStream(filePath);
         ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setUsername(String username) {
    this.username=username;
    save();
  }

  public String getUsername() {
      return username;
  }
}
