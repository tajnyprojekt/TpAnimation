import com.tajnyprojekt.tpanimation.TpAnimation;
import processing.core.PApplet;

public class App extends PApplet {

    public float x = 100;
    public float y = 400;

    private TpAnimation animation;

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        noCursor();

        animation = new TpAnimation(this, 2000)
                .setLoopMirror(true)
                .exitOnRenderFinish(false)
                .addVariableToAnimation("x", 100, 675)
                .addVariableToAnimation("y", 100, 675, 150, 900)
                ;
        animation.render();
    }

    public void draw() {
        background(0);
        stroke(255);
        noFill();
        translate(x, y);
        box(50);
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        PApplet.main( App.class.getCanonicalName());
    }
}
