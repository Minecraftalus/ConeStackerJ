package net.alus;

import conestacker.ScoreOuterClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@SpringBootApplication
public class ConeStackerJServer {
    public static void startServer() {
        Leaderboard.init(true);
        SpringApplication.run(ConeStackerJServer.class);
    }

    @GetMapping(path = "/topten", produces = "application/x-protobuf")
    public ScoreOuterClass.ScoreList handleTopTen() {
        Map<String, Integer> scores = Leaderboard.getInstance().getScores();

        List<Map.Entry<String, Integer>> topTenList = scores.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .toList();

        ScoreOuterClass.ScoreList.Builder builder = ScoreOuterClass.ScoreList.newBuilder();

        for (Map.Entry<String, Integer> score : topTenList) {
            builder.addScores(ScoreOuterClass.Score.newBuilder().setUser(score.getKey()).setScore(score.getValue()).build());
        }
        return builder.build();
    }

    @PostMapping(path = "/savescore", consumes = "application/x-protobuf")
    public void handleSaveScore(@RequestBody ScoreOuterClass.Score score) {
        Leaderboard.getInstance().saveScore(score.getScore(), score.getUser());
    }

    @PostMapping(path = "/savescorelist", consumes = "application/x-protobuf")
    public void handleSaveScoreList(@RequestBody ScoreOuterClass.ScoreList scoreList) {
        for(ScoreOuterClass.Score score : scoreList.getScoresList()) {
            Leaderboard.getInstance().saveScore(score.getScore(), score.getUser());
        }
    }


    @GetMapping(path = "/toptenjson", produces = "application/json")
    public ScoreOuterClass.ScoreList handleTopTenJson() {
        Map<String, Integer> scores = Leaderboard.getInstance().getScores();

        List<Map.Entry<String, Integer>> topTenList = scores.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .toList();

        ScoreOuterClass.ScoreList.Builder builder = ScoreOuterClass.ScoreList.newBuilder();

        for (Map.Entry<String, Integer> score : topTenList) {
            builder.addScores(ScoreOuterClass.Score.newBuilder().setUser(score.getKey()).setScore(score.getValue()).build());
        }
        return builder.build();
    }

    @PostMapping(path = "/savescorejson", consumes = "application/json")
    public void handleSaveScoreJson(@RequestBody ScoreOuterClass.Score score) {
        Leaderboard.getInstance().saveScore(score.getScore(), score.getUser());
    }

    @PostMapping(path = "/savescorelistjson", consumes = "application/json")
    public void handleSaveScoreListJson(@RequestBody ScoreOuterClass.ScoreList scoreList) {
        for(ScoreOuterClass.Score score : scoreList.getScoresList()) {
            Leaderboard.getInstance().saveScore(score.getScore(), score.getUser());
        }
    }

    @Configuration
    public class ProtoConfig {
        @Bean
        public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
            return new ProtobufHttpMessageConverter();
        }
    }
}
