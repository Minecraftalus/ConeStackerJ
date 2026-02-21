package net.alus;

import com.jme3.system.AppSettings;

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

    app.setSettings(settings);
    app.setShowSettings(false); //Settings dialog not supported on mac
    app.start();
  }
}
