package net.alus;

import conestacker.ScoreOuterClass;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NetworkHandler {
  private static  final String serverURI = "https://conestackerbackend.share.zrok.io/";
  //private static final String serverURI = "http://localhost:8080/";
  private static final HttpClient CLIENT = HttpClient.newHttpClient();

  public static void sendScoreToServer(String username, int score) {
    byte[] data = ScoreOuterClass.Score.newBuilder().setUser(username).setScore(score).build().toByteArray();

    HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(serverURI+"savescore"))
              .POST(HttpRequest.BodyPublishers.ofByteArray(data))
              .header("Content-Type", "application/x-protobuf")
              .build();

    CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .exceptionally(ex -> {
              System.err.println("Failed to send score: " + ex.getMessage());
              ex.printStackTrace();
              return null;
            });

  }

  public static CompletableFuture<HttpResponse<String>> sendScoreListToServer(Map<String, Integer> scores) {
    ScoreOuterClass.ScoreList.Builder listBuilder = ScoreOuterClass.ScoreList.newBuilder();
    for(Map.Entry<String, Integer> score : scores.entrySet()) {
      listBuilder.addScores(ScoreOuterClass.Score.newBuilder().setUser(score.getKey()).setScore(score.getValue()).build());
    }
    byte[] data = listBuilder.build().toByteArray();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(serverURI+"savescorelist"))
        .POST(HttpRequest.BodyPublishers.ofByteArray(data))
        .header("Content-Type", "application/x-protobuf")
        .build();

    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .exceptionally(ex -> {
          System.err.println("Failed to send score: " + ex.getMessage());
          ex.printStackTrace();
          return null;
        });
  }

  public static CompletableFuture<List<ScoreOuterClass.Score>> getTopTen() {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(serverURI + "topten"))
            .GET()
            .build();

    return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
            .thenApply(HttpResponse::body)
            .thenApply(bytes -> {
              try {
                ScoreOuterClass.ScoreList scoreList = ScoreOuterClass.ScoreList.parseFrom(bytes);
                return scoreList.getScoresList();
              } catch (Exception e) {
                  e.printStackTrace();
                return null;
              }
            })
            .exceptionally((e)-> null);
  }
}
