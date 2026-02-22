package net.alus;

import com.jme3.system.AppSettings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    if (args.length==1 && (args[0].equals("--server") || args[0].equals("-s"))) {
      startServer();
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
    settings.setGammaCorrection(false);
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
