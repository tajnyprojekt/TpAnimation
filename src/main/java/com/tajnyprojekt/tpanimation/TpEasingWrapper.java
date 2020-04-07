package com.tajnyprojekt.tpanimation;

import penner.easing.*;

/**
 * Wrapper around
 * <a href="https://github.com/jesusgollonet/processing-penner-easing">Robert Penner's easing library</a>,
 * exposing all available easing functions through a single static method.
 *
 * @author Michal Urbanski (tajny_projekt)<br>
 * created during Corona Time 2020
 */
public class TpEasingWrapper implements TpEasing {

    /**
     * Applies easing with the selected function.<br>
     * @see TpEasing available easing functions.
     * @param easingFunction number indicating which easing function to use, all available values are in TpEasing
     * @param t value to be eased, range 0..1
     * @return eased value, range 0..1
     */
    public static float ease(int easingFunction, float t) {
        switch(easingFunction) {
            case LINEAR:        return t;
            case SIMPLE_INOUT:  return easeInOut(t);
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
        }
        return t;
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

































