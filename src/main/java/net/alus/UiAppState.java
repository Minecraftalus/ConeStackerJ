package net.alus;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.RadialBlurFilter;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;

public class UiAppState extends BaseAppState {
    private Label scoreLabel;
    Button playButton;
    Panel titlePanel = new Panel();
    private FilterPostProcessor fpp;
    private DepthOfFieldFilter blurFilter;
    private ConeStackerJ app;
    int appHeight;
    int appWidth;
    int prevAppHeight;
    int prevAppWidth;
    IconComponent titleIcon;

    @Override
    protected void initialize(Application app) {
        this.app = (ConeStackerJ) app;

        appHeight=app.getContext().getSettings().getHeight();
        appWidth=app.getContext().getSettings().getWidth();

        GuiGlobals.getInstance().getStyles().getSelector("glass").set("font", this.app.getAssetManager().loadFont("Fonts/ray.fnt"));

        scoreLabel = new Label("Cones: 0");
        scoreLabel.setFontSize(30);
        scoreLabel.setColor(ColorRGBA.Black);
        scoreLabel.setLocalTranslation(20, appHeight - 20, 0);

        playButton = new Button("Play");
        playButton.setBackground(new QuadBackgroundComponent(ColorRGBA.LightGray, 10, -5));
        playButton.setLocalTranslation(appWidth/2 - playButton.getPreferredSize().x/2, appHeight/2, 0);
        playButton.addClickCommands(this::handleStartPress);

        titleIcon = new IconComponent("Textures/title.png");
        titleIcon.setIconScale(0.25f);
        titlePanel.setBackground(titleIcon);
        titlePanel.setLocalTranslation(appWidth/2 - titlePanel.getPreferredSize().x/2, appHeight/1.2f, 0);

        this.app.getGuiNode().attachChild(playButton);
        this.app.getGuiNode().attachChild(titlePanel);

        fpp = new FilterPostProcessor(app.getAssetManager());
        blurFilter = new DepthOfFieldFilter();
        fpp.addFilter(blurFilter);
        blurFilter.setEnabled(true);
        app.getViewPort().addProcessor(fpp);
    }

    private void handleStartPress(Button button) {
        getState(StackerAppState.class).setStackingEnabled(true);
        app.getGuiNode().detachChild(playButton);
        app.getGuiNode().attachChild(scoreLabel);
        app.getGuiNode().detachChild(titlePanel);
        blurFilter.setEnabled(false);
    }

    public void handleGameOver() {
        getState(StackerAppState.class).setStackingEnabled(false);
        app.getGuiNode().attachChild(playButton);
        app.getGuiNode().detachChild(scoreLabel);
        app.getGuiNode().attachChild(titlePanel);
        blurFilter.setEnabled(true);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Cones: " + score);
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
            titlePanel.setLocalTranslation(appWidth / 2 - titlePanel.getPreferredSize().x / 2, appHeight / 1.2f, 0);
            prevAppWidth=appWidth;
            prevAppHeight=appHeight;
        }
        playButton.setLocalTranslation(appWidth/2 - playButton.getPreferredSize().x/2, appHeight/2, 0);
        scoreLabel.setLocalTranslation(20, appHeight - 20, 0);
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}