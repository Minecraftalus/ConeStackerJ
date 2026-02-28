package net.alus;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Leaderboard implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private static Leaderboard instance;
  private final Map<String, Integer> scores;
  private static String filePath = "./leaderboard";
  private static boolean isServer = false;

  private Leaderboard() {
    scores = new HashMap<>();
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

  public synchronized Map<String, Integer> getScores() {
    return new HashMap<>(scores);
  }

  public synchronized void saveScore(int score) {
    saveScore(score, Settings.getInstance().getUsername());
  }

  public synchronized void saveScore(int score, String username) {
    if(scores.getOrDefault(username, 0) < score) {
      scores.put(username, score);
      if(!isServer)
        NetworkHandler.sendScoreToServer(username, score);
      save();
    }
  }

  private synchronized void save() {
    try (FileOutputStream fileOut = new FileOutputStream(filePath);
         ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
      out.writeObject(this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized void deleteUser(String removedUser) {
    if(scores.remove(removedUser)==null) {
      System.out.println("Failed to remove score for !");
    } else {
      System.out.println("Removed '"+removedUser+"' successfully!");
      save();
    }
  }

  public synchronized void renameUser(String olderUser, String newUser) {
    if(scores.containsKey(newUser)) {
      System.out.println("User '"+newUser+"' already exists!");
    } else if(!scores.containsKey(olderUser)){
      System.out.println("User '"+olderUser+"' not found!");
    } else {
      scores.put(newUser, scores.remove(olderUser));
      System.out.println("Successfully renamed '" + olderUser + "' to '" + newUser + "'");
      save();
    }

  }
}
