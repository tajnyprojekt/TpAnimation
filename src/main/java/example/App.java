package example;

import com.tajnyprojekt.tpanimation.TpAnimation;

import com.tajnyprojekt.tpanimation.TpEasing;
import processing.core.PApplet;

public class App extends PApplet {

    public float a = 270;
    public float b = 100;
    public float x = 170;

    private TpAnimation animation;

    public void settings() {
        size(400, 400, P3D);
        smooth(8);
    }

    public void setup() {
        noCursor();

        animation = new TpAnimation(this, 2000)
                .setLoopMirror(true)
                .setOutputFrameRate(30)
                .setOutputIndexOffset(60)
                .setForwardPlayback(false)
                .exitOnRenderFinish(true)
                .addVariableAnimation("a", 0, 280, TpEasing.ELASTIC_OUT)
                .addVariableAnimation("b", 0, 240, TpEasing.BOUNCE_OUT)
                .addVariableAnimation("x", 170, 230, 800, 1400, TpEasing.BACK_IN)
                ;
        animation.render();
    }

    public void draw() {
        background(0);
        noStroke();
        fill(69, 247, 148);
        ellipse(200, 200, a, b);
        fill(0);
        ellipse(x, 200, 120, 120);
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        PApplet.main( App.class.getCanonicalName());
    }
}
