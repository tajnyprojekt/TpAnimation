package example;

import com.tajnyprojekt.tpanimation.TpAnimatedVariable;
import com.tajnyprojekt.tpanimation.TpAnimation;

import com.tajnyprojekt.tpanimation.TpEasing;
import processing.core.PApplet;

public class App extends PApplet {

    public float a = 270;
    public float b = 100;
    public float x = 170;

    float[] params = {270, 100, 170};

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
                .exitOnRenderFinish(true)
                .addArrayItemAnimation(params, 0, 0, 280, 0, 2000, TpEasing.ELASTIC_OUT)
                .addArrayItemAnimation(params, 1, 0, 240, 0, 2000, TpEasing.BOUNCE_OUT)
                .addArrayItemAnimation(params, 2, 170, 230, 800, 1400, TpEasing.BACK_IN)
//                .addVariableAnimation(a, 0, 280, TpEasing.QUINT_IN)
//                .addVariableAnimation("b", 0, 240, TpEasing.BOUNCE_OUT)
//                .addVariableAnimation("x", 170, 230, 800, 1400, TpEasing.BACK_IN)
                ;
        animation.play();
    }

    public void draw() {
        background(0);
        noStroke();
        fill(69, 247, 148);
        ellipse(200, 200, params[0], params[1]);
        fill(0);
        ellipse(params[2], 200, 120, 120);
    }

    public void keyPressed() {
        if (key == ' ') {
            animation.loop(2);
        }
    }

    public void onAnimationFinished(TpAnimation a) {
        println(String.format("The animation has finished after %d loops.", a.getLoopCount()));
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        PApplet.main( App.class.getCanonicalName());
    }
}
