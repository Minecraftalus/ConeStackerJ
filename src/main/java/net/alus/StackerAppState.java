package net.alus;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Spatial;

import java.util.ArrayList;

public class StackerAppState extends BaseAppState implements ActionListener {
    private ConeStackerJ app;
    private Spatial basicTrafficCone;
    private Spatial floatingCone;
    private float coneSpeed = 2f;
    public float coneX;
    private ConeDirection coneDirection = ConeDirection.left;
    private float heightOffset = 0;
    private ArrayList<Spatial> cones = new ArrayList<>();
    private int score = -1; // initial cone makes this 0
    private boolean stackingEnabled = false;

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


        floatingCone = basicTrafficCone.clone();
        floatingCone.setLocalTranslation(0, heightOffset+1, 0);
        this.app.getRootNode().attachChild(floatingCone);

        app.getInputManager().addMapping("Place", new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addListener(this, "Place");

        spawnCone();
    }

    private void spawnCone() {
        if(coneX>-0.8 && coneX<0.8) {
            Spatial newCone = basicTrafficCone.clone();
            newCone.setLocalTranslation(0, heightOffset, 0);
            heightOffset += 0.4f;
            cones.add(newCone);
            app.getRootNode().attachChild(newCone);
            getState(CameraAppState.class).setTarget(newCone);
            score++;
            coneX=0;
            if (coneSpeed <= 9) {
                coneSpeed += 0.8f;
            }
            getState(UiAppState.class).updateScore(score);
        } else {
            for(Spatial cone : cones) {
                app.getRootNode().detachChild(cone);
            }
            Leaderboard.getInstance().addScore(score);
            coneX=0;
            coneSpeed=2f;
            heightOffset=0;
            score=0;
            cones.clear();
            getState(UiAppState.class).updateScore(score);
            spawnCone();
            getState(UiAppState.class).handleGameOver();
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
            if(coneX>=1.8) {
                coneDirection = ConeDirection.left;
            }
        } else {
            coneX -= coneSpeed*tpf;
            if(coneX<=-1.8) {
                coneDirection = ConeDirection.right;
            }
        }
        coneX=Math.clamp(coneX, -1.8f, 1.8f);
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
