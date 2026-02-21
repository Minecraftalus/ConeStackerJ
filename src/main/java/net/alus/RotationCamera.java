package net.alus;

import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class RotationCamera extends ChaseCamera {
    float rotation = 0;
    Node pivot;
    Spatial targeted;
    private float followSpeed = 6.7f;


    public RotationCamera(Camera cam, float distance, Node rootNode) {
        super(cam);

        this.pivot = new Node();
        rootNode.attachChild(pivot);

        pivot.addControl(this);
        this.target = pivot;

        setDefaultDistance(distance);
        setMaxDistance(distance);
        setMinDistance(distance);
        setRotationSpeed(0);
    }

    public void rotate(float speed, float tpf) {
        rotation -= speed * tpf;
        setDefaultHorizontalRotation(rotation);
    }

    public void update(float tpf) {
        if (targeted != null) {
            Vector3f targetPos = targeted.getWorldTranslation();
            pivot.getLocalTranslation().interpolateLocal(targetPos, Math.min(1, tpf * followSpeed));

            pivot.setLocalTranslation(pivot.getLocalTranslation());

            super.update(tpf);
        }
    }

    public void setTarget(Spatial newTarget) {
        targeted = newTarget;
    }
}