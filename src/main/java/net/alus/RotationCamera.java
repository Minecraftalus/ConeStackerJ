package net.alus;

import com.jme3.input.ChaseCamera;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

public class RotationCamera extends ChaseCamera {
    float rotation = 0;

    public RotationCamera(Camera cam, float distance) {
        super(cam);
        setDefaultDistance(distance);
        setMaxDistance(distance);
        setMinDistance(distance);
        setRotationSpeed(0);
    }

    public void rotate(float speed, float tpf) {
        rotation-=speed*tpf;
        setDefaultHorizontalRotation(rotation);
    }

    public void setTarget(Spatial newTarget) {
        if(target!=null) {
            target.removeControl(this);
        }
        newTarget.addControl(this);
        target = newTarget;
    }
}
