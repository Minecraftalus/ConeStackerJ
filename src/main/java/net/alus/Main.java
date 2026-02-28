package net.alus;

import com.jme3.system.AppSettings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    if (args.length>=1 && (args[0].equalsIgnoreCase("--server") || args[0].equalsIgnoreCase("-s"))) {
      if(args.length>=2 && (args[1].equalsIgnoreCase("--delete-user") || args[1].equalsIgnoreCase("-du"))) {
        if(args.length>=3) {
          Leaderboard.init(true);
          Leaderboard.getInstance().deleteUser(args[2]);
        } else {
          System.out.println("Format '--server --delete-user <user>'");
        }
      } else if(args.length>=2 && (args[1].equalsIgnoreCase("--rename-user") || args[1].equalsIgnoreCase("-ru"))) {
        if(args.length>=4) {
          Leaderboard.init(true);
          Leaderboard.getInstance().renameUser(args[2], args[3]);
        } else {
          System.out.println("Format '--server --rename-user <oldUser> <newUser>'");
        }
      } else {
        startServer();
      }
    } else {
      startClient();
    }
  }
  private static void startServer() {
    ConeStackerJServer.startServer();
  }
  private static void startClient() {
    ConeStackerJ app = new ConeStackerJ();
    AppSettings settings = new AppSettings(true);
    settings.setResizable(true);
    settings.setTitle("Cone Stacker");

    try {
        settings.setIcons(new BufferedImage[]{
                ImageIO.read(Main.class.getResource("/Icons/icon64.png"))
        });
    } catch (IOException e) {
        e.printStackTrace();
    }

    app.setSettings(settings);
    app.setShowSettings(false);
    app.start();
  }
}
