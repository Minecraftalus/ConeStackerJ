package net.alus;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.simsilica.lemur.GuiGlobals;

/**
 * This is the Main Class of your Game. It should boot up your game and do initial initialisation
 * Move your Logic into AppStates or Controls or other java classes
 */
public class ConeStackerJ extends SimpleApplication {
    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);

        GuiGlobals.initialize(this);
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        setDisplayStatView(false);
        setDisplayFps(false);

        stateManager.attach(new UiAppState());
        stateManager.attach(new CameraAppState());
        stateManager.attach(new StackerAppState());

        Leaderboard.init(false);

        float col = 159/255f;
        viewPort.setBackgroundColor(new ColorRGBA(col, col, col, 1f));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //this method will be called every game tick and can be used to make updates
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //add render code here (if any)
    }
}
