import com.tajnyprojekt.tpanimation.*;


TpAnimation animation;

// create variables or arrays controlling the state of sketch in the main scope
// and mark them as public,
// the initial values will be overridden by the animation
public float x = 170;
public float[] r = {120, 140};

void setup() {

    size(400, 400);

    // create and initialize the animation
    animation = new TpAnimation(this, 2000)
    // set the animation to play back and forth during looping
    .setLoopMirror(true)
    // add variable x and animate it from 170 to 230,
    // starting after 800ms and ending after 1400 since start of the animation
    .addVariableAnimation("x", 170, 230, 800, 1400)
    // add first item of r array
    // and animate it from 170 to 280 with elastic out easing
    .addArrayItemAnimation(r, 0, 0, 280, TpEasing.ELASTIC_OUT)
    // add second item of r array ...
    .addArrayItemAnimation(r, 1, 0, 240, TpEasing.BOUNCE_OUT)
    ;

    // start the animation in an infinite loop
    animation.loop();
}


void draw() {
    // use your animated variables to create a simple blinking eye animation
    background(0);
    noStroke();
    fill(69, 247, 148);
    ellipse(200, 200, r[0], r[1]);
    fill(0);
    ellipse(x, 200, 120, 120);
}

// you can also implement an event to know when the animation is finished
// Note: it won't be called by the animation in this example,
//       because it plays in an infinite loop, but you'll see if you change
//        animation.loop() to animation.play() in setup() above
public void onAnimationFinished(TpAnimation a) {
    println(String.format("The animation has finished after %d loops.", a.getLoopCount()));
}
