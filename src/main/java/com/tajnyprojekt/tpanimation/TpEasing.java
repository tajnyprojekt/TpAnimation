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
 *
 * @see #ease(float, int)
 * @see TpAnimation
 * @see TpAnimation#addVariableAnimation(String, float, float, int, int, int)
 */
public class TpEasing {

    // available easing functions

    public static final int LINEAR         = 0;
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
