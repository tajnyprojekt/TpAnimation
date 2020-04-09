package com.tajnyprojekt.tpanimation;

import penner.easing.*;

/**
 * Easing functions for use with {@link TpAnimation}.<br>
 * Wrapper around
 * <a href="https://github.com/jesusgollonet/processing-penner-easing">Robert Penner's easing library</a>,
 * exposing all available easing functions through a single static method. Also holds the available easings as constants.<br><br>
 * Example standalone usage:<br><br>
 *
 * <pre><code class="language-processing">
 * // let's assume we have a variable <i>t</i> going from 0 to 1 in some time
 * float easedValue = TpEasing.ease(t, TpEasing.ELASTIC_INOUT);
 * </code></pre>
 * <br><br>
 * Example showing all easing functions in action:<br><br>
 *<pre><code class="language-processing">
 *import com.tajnyprojekt.tpanimation.*;
 *
 *
 *TpAnimation animation;
 *
 *&sol;/ time variable
 *public float t;
 *
 *&sol;/ array for all possible easing results
 *public float[] easingResults;
 *
 *int tileSize = 80;
 *int tileGap = 50; // the distance between tiles
 *int dotSize = 5;
 *
 *&sol;/ tiles grid size
 *int gridW = 11;
 *int gridH = 3;
 *
 *&sol;/ array for easing names
 *String[] names;
 *
 *void setup() {
 *    size(1480, 510, P2D);
 *
 *    // initialize the array
 *    easingResults = new float[TpEasing.COUNT];
 *
 *    // create and initialize 2000ms long animation
 *    animation = new TpAnimation(this, 2000)
 *            // add time animation from 0 to 1 without easing (linear)
 *            .addVariableAnimation("t", 0, 1, TpEasing.LINEAR);
 *
 *
 *    // to each array item assign corresponding easing function number
 *    // animate the value between 0..1
 *    for (int i = 0; i < TpEasing.COUNT; i++) {
 *        animation.addArrayItemAnimation(easingResults, i, 0, 1, i);
 *    }
 *
 *    // prepare array with easing names
 *    createNames();
 *
 *    // setup text
 *    textAlign(CENTER);
 *    textFont(createFont("monospace", 15));
 *    textSize(15);
 *
 *    // start the animation in an infinite loop
 *    animation.loop();
 *    background(255);
 *}
 *
 *
 *void draw() {
 *    fill(255, 6);
 *    noStroke();
 *    rect(0, 0, width, height);
 *    int index = 0;
 *    int x = tileGap;
 *    int y = tileGap;
 *    for (int col = 0; col < gridW; col++) {
 *        y = tileGap;
 *        for (int row = 0; row < gridH; row++) {
 *            if (!(col == 0 && row == 2)) {
 *                drawEasingTile(x, y, index);
 *                index++;
 *            }
 *            y += tileSize + tileGap + 20;
 *        }
 *    x += tileSize + tileGap;
 *    }
 *}
 *
 *void drawEasingTile(int x, int y, int easing) {
 *    noStroke();
 *    fill(52, 136, 153);
 *    ellipse(x + t * tileSize,
 *            y + tileSize * (1 - easingResults[easing]),
 *            dotSize, dotSize);
 *    stroke(0);
 *    fill(0);
 *    text(names[easing], x + tileSize / 2, y + tileSize + 30);
 *}
 *
 *void createNames() {
 *    names = new String[TpEasing.COUNT];
 *    names[0] = "LINEAR";
 *    names[1] = "BASIC_INOUT";
 *    names[2] = "QUAD_IN";
 *    names[3] = "QUAD_OUT";
 *    names[4] = "QUAD_INOUT";
 *    names[5] = "CUBIC_IN";
 *    names[6] = "CUBIC_OUT";
 *    names[7] = "CUBIC_INOUT";
 *    names[8] = "QUART_IN";
 *    names[9] = "QUART_OUT";
 *    names[10] = "QUART_INOUT";
 *    names[11] = "QUINT_IN";
 *    names[12] = "QUINT_OUT";
 *    names[13] = "QUINT_INOUT";
 *    names[14] = "SINE_IN";
 *    names[15] = "SINE_OUT";
 *    names[16] = "SINE_INOUT";
 *    names[17] = "CIRC_IN";
 *    names[18] = "CIRC_OUT";
 *    names[19] = "CIRC_INOUT";
 *    names[20] = "EXPO_IN";
 *    names[21] = "EXPO_OUT";
 *    names[22] = "EXPO_INOUT";
 *    names[23] = "BACK_IN";
 *    names[24] = "BACK_OUT";
 *    names[25] = "BACK_INOUT";
 *    names[26] = "BOUNCE_IN";
 *    names[27] = "BOUNCE_OUT";
 *    names[28] = "BOUNCE_INOUT";
 *    names[29] = "ELASTIC_IN";
 *    names[30] = "ELASTIC_OUT";
 *    names[31] = "ELASTIC_INOUT";
 *}
 *</code></pre><br><br>
 *Example output: <br><br>
 *<img style="width: 100%; height: auto;" src="../../../../assets/images/easings.gif"><br>
 *<center>Choose yours : )</center></cnter><br><br>
 *For more look at the examples included with the library.<br><br>
 *
 * @see #ease(float, int)
 * @see TpAnimation
 * @see TpAnimation#addVariableAnimation(String, float, float, int, int, int)
 */
public class TpEasing {

    // available easing functions

    /**
     * No easing
     */
    public static final int LINEAR         = 0;

    /**
     * Basic in and out easing
     */
    public static final int BASIC_INOUT    = 1;

    public static final int QUAD_IN        = 2;
    public static final int QUAD_OUT       = 3;
    public static final int QUAD_INOUT     = 4;
    public static final int CUBIC_IN       = 5;
    public static final int CUBIC_OUT      = 6;
    public static final int CUBIC_INOUT    = 7;
    public static final int QUART_IN       = 8;
    public static final int QUART_OUT      = 9;
    public static final int QUART_INOUT    = 10;
    public static final int QUINT_IN       = 11;
    public static final int QUINT_OUT      = 12;
    public static final int QUINT_INOUT    = 13;
    public static final int SINE_IN        = 14;
    public static final int SINE_OUT       = 15;
    public static final int SINE_INOUT     = 16;
    public static final int CIRC_IN        = 17;
    public static final int CIRC_OUT       = 18;
    public static final int CIRC_INOUT     = 19;
    public static final int EXPO_IN        = 20;
    public static final int EXPO_OUT       = 21;
    public static final int EXPO_INOUT     = 22;
    public static final int BACK_IN        = 23;
    public static final int BACK_OUT       = 24;
    public static final int BACK_INOUT     = 25;
    public static final int BOUNCE_IN      = 26;
    public static final int BOUNCE_OUT     = 27;
    public static final int BOUNCE_INOUT   = 28;
    public static final int ELASTIC_IN     = 29;
    public static final int ELASTIC_OUT    = 30;
    public static final int ELASTIC_INOUT  = 31;

    /**
     * The total number of available easings. May be helpful for iteration over easings.
     */
    public static final int COUNT          = 32;

    // to prevent instantiation
    private TpEasing() {}

    /**
     * Applies easing with the selected function.<br>
     * Uses LINEAR easing when number passed as <code>easingFunction</code> does not match to any available<br>
     * @param t the <i>time</i> value to be eased, range 0..1
     * @param easingFunction number indicating which easing function to use
     * @return the eased value, range 0..1
     * @see TpEasing available easing functions.
     */
    public static float ease(float t, int easingFunction) {
        switch(easingFunction) {
            case LINEAR:        return t;
            case BASIC_INOUT:   return easeInOut(t);
            case QUAD_IN:       return Quad.easeIn       (t, 0.0f, 1.0f, 1.0f);
            case QUAD_OUT:      return Quad.easeOut      (t, 0.0f, 1.0f, 1.0f);
            case QUAD_INOUT:    return Quad.easeInOut    (t, 0.0f, 1.0f, 1.0f);
            case CUBIC_IN:      return Cubic.easeIn      (t, 0.0f, 1.0f, 1.0f);
            case CUBIC_OUT:     return Cubic.easeOut     (t, 0.0f, 1.0f, 1.0f);
            case CUBIC_INOUT:   return Cubic.easeInOut   (t, 0.0f, 1.0f, 1.0f);
            case QUART_IN:      return Quart.easeIn      (t, 0.0f, 1.0f, 1.0f);
            case QUART_OUT:     return Quart.easeOut     (t, 0.0f, 1.0f, 1.0f);
            case QUART_INOUT:   return Quart.easeInOut   (t, 0.0f, 1.0f, 1.0f);
            case QUINT_IN:      return Quint.easeIn      (t, 0.0f, 1.0f, 1.0f);
            case QUINT_OUT:     return Quint.easeOut     (t, 0.0f, 1.0f, 1.0f);
            case QUINT_INOUT:   return Quint.easeInOut   (t, 0.0f, 1.0f, 1.0f);
            case SINE_IN:       return Sine.easeIn       (t, 0.0f, 1.0f, 1.0f);
            case SINE_OUT:      return Sine.easeOut      (t, 0.0f, 1.0f, 1.0f);
            case SINE_INOUT:    return Sine.easeInOut    (t, 0.0f, 1.0f, 1.0f);
            case CIRC_IN:       return Circ.easeIn       (t, 0.0f, 1.0f, 1.0f);
            case CIRC_OUT:      return Circ.easeOut      (t, 0.0f, 1.0f, 1.0f);
            case CIRC_INOUT:    return Circ.easeInOut    (t, 0.0f, 1.0f, 1.0f);
            case EXPO_IN:       return Expo.easeIn       (t, 0.0f, 1.0f, 1.0f);
            case EXPO_OUT:      return Expo.easeOut      (t, 0.0f, 1.0f, 1.0f);
            case EXPO_INOUT:    return Expo.easeInOut    (t, 0.0f, 1.0f, 1.0f);
            case BACK_IN:       return Back.easeIn       (t, 0.0f, 1.0f, 1.0f);
            case BACK_OUT:      return Back.easeOut      (t, 0.0f, 1.0f, 1.0f);
            case BACK_INOUT:    return Back.easeInOut    (t, 0.0f, 1.0f, 1.0f);
            case BOUNCE_IN:     return Bounce.easeIn     (t, 0.0f, 1.0f, 1.0f);
            case BOUNCE_OUT:    return Bounce.easeOut    (t, 0.0f, 1.0f, 1.0f);
            case BOUNCE_INOUT:  return Bounce.easeInOut  (t, 0.0f, 1.0f, 1.0f);
            case ELASTIC_IN:    return Elastic.easeIn    (t, 0.0f, 1.0f, 1.0f);
            case ELASTIC_OUT:   return Elastic.easeOut   (t, 0.0f, 1.0f, 1.0f);
            case ELASTIC_INOUT: return Elastic.easeInOut (t, 0.0f, 1.0f, 1.0f);
            default: {
                TpAnimation.log("TpEasing ease - easing function with number "
                        + easingFunction +" does not exist. Using LINEAR instead", true);
                return t;
            }
        }
    }

    /**
     * Applies easing at the beginning and end values.
     * @param t value to be eased, range 0..1
     * @return eased value, range 0..1
     */
    private static float easeInOut(float t) {
        float sqt = t * t;
        return sqt / (2.0f * (sqt - t) + 1.0f);
    }
}

