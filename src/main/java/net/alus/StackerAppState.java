package net.alus;

import com.jme3.app.Application;
import com.jme3.app.LegacyApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.Random;

public class StackerAppState extends BaseAppState implements ActionListener {
    private ConeStackerJ app;
    private AudioNode coneFallSound;
    private AudioNode coneDropSound;
    private Spatial basicTrafficCone, legacyTrafficCone, standardTrafficCone;
    private Spatial floatingCone;
    private float coneSpeed;
    public float coneX;
    private ConeDirection coneDirection = ConeDirection.left;
    private float heightOffset = 0;
    private ArrayList<Spatial> cones = new ArrayList<>();
    private int score = 0;
    private boolean stackingEnabled = false;
    private final float initialConeSpeed = 4.55f;
    private final float maxConeSpeed = 12f;
    private final float speedChangePerCone = 0.9f;
    private float coneWidth;
    private final Random random = new Random();
    private DirectionalLight directionalLight;
    private AmbientLight ambientLight;
    private GraphicsMode graphicsMode;
    private float coneOffsetPerStack;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("Place") && isPressed && stackingEnabled) {
            if(coneX>-coneWidth/2 && coneX<coneWidth/2) {
                heightOffset += coneOffsetPerStack;
                score++;
                spawnCone();
                getState(UiAppState.class).updateScore(score);
                coneX=0;
                if (coneSpeed <= maxConeSpeed) {
                    coneSpeed += speedChangePerCone;
                }
                AudioNode sound = coneFallSound.clone();
                sound.setPitch(coneFallSound.getPitch()+random.nextFloat(-0.1f, 0.1f));
                app.getAudioRenderer().playSource(sound);
            } else {
                Leaderboard.getInstance().saveScore(score);
                getState(UiAppState.class).updateScore(score);
                getState(UiAppState.class).handleGameOver();
                resetGame(true);
                app.getAudioRenderer().playSource(coneDropSound.clone());
            }
        }
    }

    @Override
    protected void initialize(Application app) {
        this.app = (ConeStackerJ) app;

        standardTrafficCone = app.getAssetManager().loadModel("Models/StandardCone.obj");
        legacyTrafficCone = app.getAssetManager().loadModel("Models/LegacyCone.obj");

        basicTrafficCone=standardTrafficCone; // default before settings are loaded

        coneOffsetPerStack=0.25f;

        directionalLight = new DirectionalLight(new Vector3f(-0.5f, -0.2f, -0.3f).normalizeLocal());
        ambientLight = new AmbientLight();

        this.app.getRootNode().addLight(ambientLight);
        this.app.getRootNode().addLight(directionalLight);

        Spatial base = app.getAssetManager().loadModel("Models/Base.obj");
        this.app.getRootNode().attachChild(base);

        coneSpeed=initialConeSpeed;
        coneWidth=((BoundingBox)basicTrafficCone.getWorldBound()).getXExtent()*2;

        coneFallSound = new AudioNode(app.getAssetManager(), "Sound/coneDrop.ogg", AudioData.DataType.Buffer);
        coneDropSound = new AudioNode(app.getAssetManager(), "Sound/coneFall.ogg", AudioData.DataType.Buffer);

        coneFallSound.setPositional(false);
        coneDropSound.setPositional(false);
        coneDropSound.setVolume(0.5f);

        floatingCone = basicTrafficCone.clone();
        floatingCone.setLocalTranslation(0, heightOffset+1, 0);
        this.app.getRootNode().attachChild(floatingCone);

        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_UP));
        app.getInputManager().addListener(this, "Place");

      updateGraphicsMode(Settings.getInstance().getGraphicsMode());

      spawnCone();
    }

    private void spawnCone() {
        Spatial newCone = basicTrafficCone.clone();
        newCone.setLocalTranslation(0, heightOffset, 0);
        cones.add(newCone);
        app.getRootNode().attachChild(newCone);
        getState(CameraAppState.class).setTarget(newCone);
    }

    @Override
    protected void cleanup(Application app) {
        app.getInputManager().removeListener(this);
    }

    @Override
    public void update(float tpf) {
        if(coneDirection == ConeDirection.right) {
            coneX += coneSpeed*tpf;
            if(coneX>coneWidth) {
                coneDirection = ConeDirection.left;
            }
        } else {
            coneX -= coneSpeed*tpf;
            if(coneX<=-coneWidth) {
                coneDirection = ConeDirection.right;
            }
        }
        coneX=Math.clamp(coneX, -coneWidth, coneWidth);
        floatingCone.setLocalTranslation(coneX, heightOffset+2, 0);
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    public void resetGame(boolean resetX) {
        coneX = resetX ? 0 : coneX;
        coneSpeed = initialConeSpeed;
        heightOffset = 0;
        score = 0;
        for (Spatial cone : cones) {
            app.getRootNode().detachChild(cone);
        }
        cones.clear();
        spawnCone();
    }

    public void setStackingEnabled(boolean enabled) {
        this.stackingEnabled=enabled;
    }

    public void updateGraphicsMode(GraphicsMode mode) {
        switch (mode) {
            case legacy -> {
                graphicsMode = GraphicsMode.legacy;

                basicTrafficCone=legacyTrafficCone;

                app.getRootNode().detachChild(floatingCone);
                floatingCone = basicTrafficCone.clone();
                app.getRootNode().attachChild(floatingCone);

                coneOffsetPerStack=0.4f;

                resetGame(false);
            }
            case standard -> {
                graphicsMode = GraphicsMode.standard;

                basicTrafficCone=standardTrafficCone;

                app.getRootNode().detachChild(floatingCone);
                floatingCone = basicTrafficCone.clone();
                app.getRootNode().attachChild(floatingCone);

                coneOffsetPerStack=0.25f;

                resetGame(false);
            }
        }
    }

    private enum ConeDirection {
        left, right
    }

    public enum GraphicsMode {
        legacy, standard
    }
}
