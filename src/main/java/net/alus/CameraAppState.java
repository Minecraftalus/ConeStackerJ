package net.alus;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;

public class CameraAppState extends BaseAppState {
    private RotationCamera2 rotationCamera;
    private float rotationSpeed = 0.6f;
    private ConeStackerJ app;

    @Override
    protected void initialize(Application app) {
        this.app = (ConeStackerJ)app;
        rotationCamera = new RotationCamera2(app.getCamera(), 10, this.app.getRootNode());
        rotationCamera.setLookAtOffset(new Vector3f(0, 1f, 0));
    }

    @Override
    public void update(float tpf) {
        if (rotationCamera != null) {
            rotationCamera.rotate(rotationSpeed, tpf);
            rotationCamera.update(tpf);
        }
    }

    public void setTarget(com.jme3.scene.Spatial spatial) {
        rotationCamera.setTarget(spatial);
    }

    @Override protected void cleanup(Application app) {}
    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}