  package net.alus;

  import com.filter.textcorrector.TextFilter;
  import com.filter.textcorrector.spellchecking.Language;
  import com.jme3.app.Application;
  import com.jme3.app.state.BaseAppState;
  import com.jme3.math.ColorRGBA;
  import com.jme3.math.Vector3f;
  import com.jme3.post.FilterPostProcessor;
  import com.jme3.post.filters.DepthOfFieldFilter;
  import com.jme3.scene.Spatial;
  import com.simsilica.lemur.*;
  import com.simsilica.lemur.component.IconComponent;
  import com.simsilica.lemur.component.QuadBackgroundComponent;
  import com.simsilica.lemur.core.GuiControl;
  import com.simsilica.lemur.core.VersionedReference;
  import com.simsilica.lemur.focus.FocusChangeEvent;
  import com.simsilica.lemur.focus.FocusChangeListener;
  import com.simsilica.lemur.text.DocumentModel;
  import conestacker.ScoreOuterClass;
  import org.apache.logging.log4j.LogManager;
  import org.apache.logging.log4j.Logger;

  import java.util.List;
  import java.util.Map;
  import java.util.Set;

  public class UiAppState extends BaseAppState {
      private static final ColorRGBA gray = new ColorRGBA(170/255f, 170/255f, 170/255f, 1f);
      private static final Logger log = LogManager.getLogger(UiAppState.class);
      private Label scoreLabel;
      private Button playButton, settingsButton, leaderboardButton, backButton, syncButton, graphicsModeButton;
      private ListBox<String> localLeaderboard, globalLeaderboard;
      private Panel titlePanel;
      private TextField usernameField;
      VersionedReference<DocumentModel> usernameRef;
      private FilterPostProcessor fpp;
      private DepthOfFieldFilter blurFilter;
      private ConeStackerJ app;
      int appHeight, appWidth, prevAppHeight, prevAppWidth;
      IconComponent titleIcon;
      TextFilter textFilter = new TextFilter(Language.ENGLISH);

      @Override
      protected void initialize(Application app) {
          this.app = (ConeStackerJ) app;

          appHeight = app.getContext().getSettings().getHeight();
          appWidth = app.getContext().getSettings().getWidth();

          GuiGlobals.getInstance().getStyles().getSelector("glass").set("font", this.app.getAssetManager().loadFont("Fonts/ray.fnt"));

          scoreLabel = new Label("Cones: 0");
          scoreLabel.setFontSize(30);
          scoreLabel.setColor(ColorRGBA.Black);

          playButton = new Button("Play");
          playButton.setBackground(new QuadBackgroundComponent(gray, 10, -5));
          playButton.addClickCommands(this::handleStartPress);
          addObject(playButton);

          leaderboardButton = new Button("");
          leaderboardButton.setPreferredSize(new Vector3f(80, 80, 0));
          leaderboardButton.setIcon(new IconComponent("Textures/leaderboard.png", leaderboardButton.getPreferredSize().x / 128, 0f, 0, 0f, false));
          leaderboardButton.setBackground(new QuadBackgroundComponent(gray, 0, 0));
          leaderboardButton.addClickCommands(this::handleLeaderboardPress);
          addObject(leaderboardButton);

          settingsButton = new Button("");
          settingsButton.setPreferredSize(new Vector3f(80, 80, 0));
          settingsButton.setIcon(new IconComponent("Textures/settings.png", settingsButton.getPreferredSize().x / 128, 0f, 0, 0f, false));
          settingsButton.setBackground(new QuadBackgroundComponent(gray, 0, 0));
          settingsButton.addClickCommands(this::handleSettingsPress);
          addObject(settingsButton);

          backButton = new Button("");
          backButton.setPreferredSize(new Vector3f(60, 60, 0));
          backButton.setIcon(new IconComponent("Textures/back.png", backButton.getPreferredSize().x / 128, 0f, 0, 0f, false));
          backButton.setBackground(new QuadBackgroundComponent(gray, 0, 0));
          backButton.addClickCommands(this::handleBackPress);

          syncButton = new Button("");
          syncButton.setPreferredSize(new Vector3f(60, 60, 0));
          syncButton.setIcon(new IconComponent("Textures/sync.png", syncButton.getPreferredSize().x / 128, 0f, 0, 0f, false));
          syncButton.setBackground(new QuadBackgroundComponent(gray, 0, 0));
          syncButton.addClickCommands(this::handleSyncPress);

          graphicsModeButton = new Button("Graphics mode: "+Settings.getInstance().getGraphicsMode().toString());
          graphicsModeButton.setBackground(new QuadBackgroundComponent(gray, 0, 0));
          graphicsModeButton.addClickCommands(this::changeGraphicsMode);

          titleIcon = new IconComponent("Textures/title.png");
          titleIcon.setIconScale(0.25f);
          titlePanel = new Panel();
          titlePanel.setBackground(titleIcon);
          addObject(titlePanel);

          GuiGlobals.getInstance().getStyles().getSelector("list.item", "glass").set("fontSize", 28f);

          localLeaderboard = new ListBox<>();
          globalLeaderboard = new ListBox<>();
          globalLeaderboard.getModel().add("Loading...");

          usernameField = new TextField("XXXXXXXXXXXX");
          usernameField.setBackground(new QuadBackgroundComponent(gray, 0, 0));
          usernameField.getControl(GuiControl.class).addFocusChangeListener(new FocusChangeListener() {
              @Override
              public void focusGained(FocusChangeEvent event) {}
              @Override
              public void focusLost(FocusChangeEvent event) {
                  if(usernameField.getText().isEmpty() || textFilter.isProfane(usernameField.getText()))
                      usernameField.setText("Guest");
                  Settings.getInstance().setUsername(usernameField.getText());
              }
          });
          usernameRef = usernameField.getDocumentModel().createReference();

          fpp = new FilterPostProcessor(app.getAssetManager());
          blurFilter = new DepthOfFieldFilter();
          fpp.addFilter(blurFilter);
          blurFilter.setEnabled(true);
          blurFilter.setBlurScale(1.5f);
          app.getViewPort().addProcessor(fpp);
      }

      private void handleStartPress(Button button) {
          getState(StackerAppState.class).setStackingEnabled(true);
          removeObject(playButton);
          removeObject(titlePanel);
          removeObject(settingsButton);
          removeObject(leaderboardButton);
          addObject(scoreLabel);
          blurFilter.setEnabled(false);
      }

      private void handleSettingsPress(Button button) {
          removeObject(playButton);
          removeObject(titlePanel);
          removeObject(settingsButton);
          removeObject(leaderboardButton);
          addObject(backButton);
          addObject(usernameField);
          addObject(graphicsModeButton);
          usernameField.setText(Settings.getInstance().getUsername());
      }

      private void handleLeaderboardPress(Button button) {
          removeObject(playButton);
          removeObject(titlePanel);
          removeObject(settingsButton);
          removeObject(leaderboardButton);
          addObject(localLeaderboard);
          addObject(globalLeaderboard);
          addObject(backButton);
          addObject(usernameField);
          addObject(syncButton);
          updateGlobalLeaderboard();
          updateLocalLeaderboard();
          usernameField.setText(Settings.getInstance().getUsername());
      }

      private void handleBackPress(Button button) {
          removeObject(backButton);
          removeObject(localLeaderboard);
          removeObject(globalLeaderboard);
          removeObject(usernameField);
          removeObject(syncButton);
          removeObject(graphicsModeButton);
          addObject(playButton);
          addObject(titlePanel);
          addObject(settingsButton);
          addObject(leaderboardButton);
      }

      private void handleSyncPress(Button button) {
          globalLeaderboard.getModel().clear();
          globalLeaderboard.getModel().add("Loading...");
          NetworkHandler.sendScoreListToServer(Leaderboard.getInstance().getScores()).thenRun(
                  ()->app.enqueue(this::updateGlobalLeaderboard));
      }

      private void changeGraphicsMode(Button button) {
          switch(Settings.getInstance().getGraphicsMode()) {
              case legacy -> {
                  Settings.getInstance().setGraphicsMode(StackerAppState.GraphicsMode.standard);
                  graphicsModeButton.setText("Graphics mode: "+Settings.getInstance().getGraphicsMode().toString());
                  getState(StackerAppState.class).updateGraphicsMode(Settings.getInstance().getGraphicsMode());
              }
              case standard -> {
                  Settings.getInstance().setGraphicsMode(StackerAppState.GraphicsMode.legacy);
                  graphicsModeButton.setText("Graphics mode: "+Settings.getInstance().getGraphicsMode().toString());
                  getState(StackerAppState.class).updateGraphicsMode(Settings.getInstance().getGraphicsMode());
              }
          }
          graphicsModeButton.setLocalTranslation(appWidth/2f-graphicsModeButton.getPreferredSize().x/2f, appHeight*0.7f, 0);
      }

      public void handleGameOver() {
          getState(StackerAppState.class).setStackingEnabled(false);
          addObject(playButton);
          addObject(titlePanel);
          addObject(settingsButton);
          addObject(leaderboardButton);
          removeObject(scoreLabel);
          blurFilter.setEnabled(true);
      }

      public void updateScore(int score) {
          scoreLabel.setText("Cones: " + score);
      }

      private void updateGlobalLeaderboard() {
        NetworkHandler.getTopTen().thenAccept(scores -> app.enqueue(() -> {
          if (scores!=null) {
            globalLeaderboard.getModel().clear();
            for (int i = 0; i < scores.size(); i++) {
              ScoreOuterClass.Score score = scores.get(i);
              String text = (i + 1) + " | " + score.getUser() + " | " + score.getScore();
              globalLeaderboard.getModel().add(text);
            }
          } else {
              globalLeaderboard.getModel().clear();
              globalLeaderboard.getModel().add("Failed to get scores");
          }
          return null;
        }));
      }

      private void updateLocalLeaderboard() {
          localLeaderboard.getModel().clear();
          List<Map.Entry<String, Integer>> scores = Leaderboard.getInstance().getScores()
                  .entrySet()
                  .stream()
                  .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                  .toList();

          for (int i = 0; i < scores.size(); i++) {
              Map.Entry<String, Integer> score = scores.get(i);
              String text = (i + 1) + " | " + score.getKey() + " | " + score.getValue();
              localLeaderboard.getModel().add(text);
          }
      }

      @Override
      protected void cleanup(Application app) {
          scoreLabel.removeFromParent();
      }

      @Override
      public void update(float tpf) {
          appWidth = app.getContext().getSettings().getWidth();
          appHeight = app.getContext().getSettings().getHeight();
          if (prevAppHeight != appHeight || prevAppWidth != appWidth) {
              float ratio = 0.25f / 640f;
              titlePanel.setBackground(null);
              titleIcon.setIconScale(ratio * appWidth);
              titlePanel.setBackground(titleIcon);
              titlePanel.setLocalTranslation(appWidth / 2f - titlePanel.getPreferredSize().x / 2, appHeight / 1.2f, 0);

              settingsButton.setLocalTranslation(appWidth / 2f + 5, appHeight / 2f - settingsButton.getPreferredSize().y, 0);
              leaderboardButton.setLocalTranslation(appWidth / 2f - leaderboardButton.getPreferredSize().x - 5, appHeight / 2f - leaderboardButton.getPreferredSize().y, 0);
              backButton.setLocalTranslation(5, appHeight - 5, 0);
              syncButton.setLocalTranslation(appWidth/2f, appHeight*0.7f+syncButton.getPreferredSize().y, 0);
              playButton.setLocalTranslation(appWidth / 2f - playButton.getPreferredSize().x / 2, appHeight / 2f, 0);
              graphicsModeButton.setLocalTranslation(appWidth/2f-graphicsModeButton.getPreferredSize().x/2f, appHeight*0.7f, 0);
              scoreLabel.setLocalTranslation(20, appHeight - 20, 0);


              float itemHeight = 64f;
              float leaderboardWidth = Math.max(35f, appWidth * (7 / 16f)); // app crashes if less than this amount
              float leaderboardHeight = Math.max(170f, appHeight * 0.7f); // app crashes if less than this amount

              localLeaderboard.setLocalTranslation(20, leaderboardHeight, 0);
              localLeaderboard.setPreferredSize(new Vector3f(leaderboardWidth, leaderboardHeight, 0));
              localLeaderboard.setVisibleItems((int) (leaderboardHeight / itemHeight));

              globalLeaderboard.setLocalTranslation(appWidth / 2f, leaderboardHeight, 0);
              globalLeaderboard.setPreferredSize(new Vector3f(leaderboardWidth, leaderboardHeight, 0));
              globalLeaderboard.setVisibleItems((int) (leaderboardHeight / itemHeight));

              usernameField.setPreferredSize(usernameField.getPreferredSize());
              usernameField.setLocalTranslation(appWidth/2f-usernameField.getPreferredSize().x/2, appHeight*0.9f, 0);


              prevAppWidth = appWidth;
              prevAppHeight = appHeight;
          }
          if(usernameRef.update()) {
              String input = usernameField.getText();

              String cleanInput = input.replaceAll("[^a-zA-Z0-9_-]", "");
              if(cleanInput.length() > 12) {
                  cleanInput = cleanInput.substring(0, 12);
              }

              usernameField.setText(cleanInput);
          }
      }

      private void addObject(Spatial object) {
          app.getGuiNode().attachChild(object);
      }

      private void removeObject(Spatial object) {
          app.getGuiNode().detachChild(object);
      }

      @Override
      protected void onEnable() {
      }

      @Override
      protected void onDisable() {
      }
  }