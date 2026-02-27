package net.alus;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.Random;

public class StackerAppState extends BaseAppState implements ActionListener {
    private ConeStackerJ app;
    private AudioNode coneFallSound;
    private AudioNode coneDropSound;
    private Spatial basicTrafficCone;
    private Spatial floatingCone;
    private float coneSpeed;
    public float coneX;
    private ConeDirection coneDirection = ConeDirection.left;
    private float heightOffset = 0;
    private ArrayList<Spatial> cones = new ArrayList<>();
    private int score = -1; // initial cone makes this 0
    private boolean stackingEnabled = false;
    private final float initialConeSpeed = 4.55f;
    private final float maxConeSpeed = 12f;
    private final float speedChangePerCone = 0.9f;
    private float coneWidth;
    private final Random random = new Random();

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("Place") && isPressed && stackingEnabled) {
            spawnCone();
        }
    }

    @Override
    protected void initialize(Application app) {
        this.app = (ConeStackerJ) app;

        basicTrafficCone = app.getAssetManager().loadModel("Models/trafficcone.obj");
        Spatial base = app.getAssetManager().loadModel("Models/Base.obj");
        this.app.getRootNode().attachChild(base);

        coneSpeed=initialConeSpeed;
        coneWidth=((BoundingBox)basicTrafficCone.getWorldBound()).getXExtent()*2;

        coneFallSound = new AudioNode(app.getAssetManager(), "Sound/coneDrop.ogg", AudioData.DataType.Buffer);
        coneDropSound = new AudioNode(app.getAssetManager(), "Sound/coneFall.ogg", AudioData.DataType.Buffer);

        floatingCone = basicTrafficCone.clone();
        floatingCone.setLocalTranslation(0, heightOffset+1, 0);
        this.app.getRootNode().attachChild(floatingCone);

        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_UP));
        app.getInputManager().addListener(this, "Place");

        spawnCone();
    }

    private void spawnCone() {
        if(coneX>-coneWidth/2 && coneX<coneWidth/2) {
            Spatial newCone = basicTrafficCone.clone();
            newCone.setLocalTranslation(0, heightOffset, 0);
            heightOffset += 0.4f;
            cones.add(newCone);
            app.getRootNode().attachChild(newCone);
            getState(CameraAppState.class).setTarget(newCone);
            score++;
            coneX=0;
            if (coneSpeed <= maxConeSpeed) {
                coneSpeed += speedChangePerCone;
            }
            getState(UiAppState.class).updateScore(score);
            if(score>0) {
              AudioNode sound = coneFallSound.clone();
              sound.setPitch(coneFallSound.getPitch()+random.nextFloat(-0.1f, 0.1f));
              app.getAudioRenderer().playSource(sound);
            }
        } else {
            for(Spatial cone : cones) {
                app.getRootNode().detachChild(cone);
            }
            Leaderboard.getInstance().saveScore(score);
            coneX = 0;
            coneSpeed = initialConeSpeed;
            heightOffset = 0;
            score = -1;
            cones.clear();
            getState(UiAppState.class).updateScore(score);
            spawnCone();
            getState(UiAppState.class).handleGameOver();
            app.getAudioRenderer().playSource(coneDropSound.clone());
        }
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

    public void setStackingEnabled(boolean enabled) {
        this.stackingEnabled=enabled;
    }

    private enum ConeDirection {
        left, right
    }
}
