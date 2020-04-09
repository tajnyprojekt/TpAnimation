package com.tajnyprojekt.tpanimation;

import processing.core.PApplet;
import java.lang.reflect.Field;


/**
 * TpAnimatedVariable represents and stores information about a field or an array item from the Processing sketch that will be animated.<br>
 * Your variable can be of type <code>int</code>, <code>float</code> or <code>double</code>, and for arrays <code>int[]</code>, <code>float[]</code> or <code>double[]</code>
 * This class is also responsible for easing and updating the field's value based on parent animation progress.<br>
 * The field must be marked as <code>public</code> in order to be animated.<br>
 * This is described along with an example here: {@link TpAnimation#addVariableAnimation(String, float, float, int, int, int)}<br>
 *
 * @see TpAnimation
 * @see TpAnimation#addVariableAnimation(String, float, float, int, int, int)
 * @see TpAnimation#addArrayItemAnimation(Object, int, float, float, int, int, int)
 * @see TpEasing
 */
public class TpAnimatedVariable {

    private PApplet parent;
    private TpAnimation animation;

    private boolean isField;

    private String name;
    private Field field;
    private Class fieldType;

    private Object var;

    private Object array;
    private int index;

    private boolean ignore;

    private float localProgress;
    private float startProgress;
    private float endProgress;

    private float val;
    private float from;
    private float to;

    private boolean isFullLength;
    private int startMillis;
    private int endMillis;

    private boolean ease;
    private int easingFunctionNumber;

    /**
     * Creates an object representing variable that will be animated.<br>
     * The field will be identified by it's name.
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * This version of constructor let's you create variable's transition taking the full lenght of parent animation.
     *
     * @see com.tajnyprojekt.tpanimation.TpAnimation#addVariableAnimation(String, float, float, int, int, int)
     * <code>TpAnimation.addVariableAnimation</code> for full description with an example.
     *
     * @param animation the animation that variable belongs to
     * @param varName the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param easing number indicating which easing function to use
     * @see TpEasing available easing functions
     */
    public TpAnimatedVariable(TpAnimation animation, String varName, float from, float to, int easing) {
        this(animation, varName, from, to, 0, 0, easing);
        isFullLength = true;
    }

    /**
     * Creates an object representing variable that will be animated.<br>
     * The field will be identified by it's name.
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * This version of constructor let's you create variable's transition that begins or end with some time offset
     * to the parent animation.
     *
     * @see com.tajnyprojekt.tpanimation.TpAnimation#addVariableAnimation(String, float, float, int, int, int)
     * <code>TpAnimation.addVariableAnimation</code> for full description with an example.
     *
     * @param animation the animation that variable belongs to
     * @param varName the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transitio
     * @param startMillis the start time in animation for value transition
     * @param endMillis the end time in animation for value transition
     * @param easing the number indicating which easing function to use
     * @see TpEasing available easing functions.
     */
    public TpAnimatedVariable(TpAnimation animation, String varName, float from, float to,
                              int startMillis, int endMillis, int easing) {
        init(animation, from, to, startMillis, endMillis, easing);
        isField = true;
        findFieldInSketch(varName);
    }

    /**
     * Creates an object representing the variable that will be animated.<br>
     * The variable will be identified by array reference and the item index.<br>
     * The type of the passed field must be <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * This version of constructor let's you create variable's transition taking the full lenght of parent animation.
     *
     * @see com.tajnyprojekt.tpanimation.TpAnimation#addVariableAnimation(String, float, float, int, int, int)
     * <code>TpAnimation.addVariableAnimation</code> for full description with an example.
     *
     * @param animation the animation that variable belongs to
     * @param array the array containing field that will be animated
     * @param index the index of the array item that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param easing number indicating which easing function to use
     * @see TpEasing available easing functions
     */
    public TpAnimatedVariable(TpAnimation animation, Object array, int index, float from, float to, int easing) {
        this(animation, array, index, from, to, 0, 0, easing);
        isFullLength = true;
    }

    /**
     * Creates an object representing variable that will be animated.<br>
     * The variable will be identified by array reference and the item index.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * This version of constructor let's you create variable's transition that begins or end with some time offset
     * to the parent animation.
     *
     * @see com.tajnyprojekt.tpanimation.TpAnimation#addVariableAnimation(String, float, float, int, int, int)
     * <code>TpAnimation.addVariableAnimation</code> for full description with an example.
     *
     * @param animation the animation that variable belongs to
     * @param array the array containing field that will be animated
     * @param index the index of the array item that will be animated
     * @param from the initial value for transition
     * @param to the final value for transitio
     * @param startMillis the start time in animation for value transition
     * @param endMillis the end time in animation for value transition
     * @param easing the number indicating which easing function to use
     * @see TpEasing available easing functions.
     */
    public TpAnimatedVariable(TpAnimation animation, Object array, int index, float from, float to,
                              int startMillis, int endMillis, int easing) {
        init(animation, from, to, startMillis, endMillis, easing);
        isField = false;
        this.array = array;
        this.index = index;
        checkArray();

    }

    private void init(TpAnimation animation, float from, float to,
                      int startMillis, int endMillis, int easing) {
        this.animation = animation;
        this.parent = animation.parent;
        ignore = false;
        this.from = from;
        this.to = to;
        isFullLength = false;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        updateProgressBounds();
        this.ease = true;
        this.easingFunctionNumber = easing;
    }

    /**
     * Finds field with given name in the sketch and checks it's type
     *
     * @param variableName the name of the sketch's field to find
     */
    @SuppressWarnings("rawtypes")
    private void findFieldInSketch(String variableName) {
        try {
            field = parent.getClass().getField(variableName);
            fieldType = field.getType();
            if (!(isInt() || isFloat() || isDouble())) {
                ignore = true;
                TpAnimation.log("Wrong type: " + fieldType.toString()
                        + " for variable name: " + variableName
                        + ". \nVariable to be animated must be of type int, float or double.", true);
            }
        }
        catch (Exception e) {
            ignore = true;
            TpAnimation.log("An error occured during looking for variable: " + variableName
                    + ". \nMake sure that the variable is marked as 'public', "
                    + "and located in the main scope of the sketch (above setup()), \ne.g. public float "
                    + variableName + ";", true);
            e.printStackTrace();
            parent.exit();
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object getValueOf(Object clazz, String lookingForValue)
            throws Exception {
        Field field = clazz.getClass().getField(lookingForValue);
        Class clazzType = field.getType();
        if (clazzType.toString().equals("double"))
            return field.getDouble(clazz);
        else if (clazzType.toString().equals("int"))
            return field.getInt(clazz);
        // else other type ...
        // and finally
        return field.get(clazz);
    }

    /**
     * Checks type of array
     */
    @SuppressWarnings("rawtypes")
    private void checkArray() {
        try {
            fieldType = array.getClass();
            if (!(isIntArray() || isFloatArray() || isDoubleArray())) {
                ignore = true;
                TpAnimation.log("Wrong type of array: " + fieldType.toString()
                        + ". \nArray containing item to be animated must be of type int[], float[] or double[].", true);
            }
        }
        catch (Exception e) {
            ignore = true;
            TpAnimation.log("An error occured during looking for array"
                    + ". \nMake sure that the array is marked as 'public', "
                    + "and located in the main scope of the sketch (above setup()), \ne.g. public float[] "
                    + "someArray;", true);
            e.printStackTrace();
            parent.exit();
        }
    }

    /**
     * Updates the sketch's field value
     */
    private void updateFieldValue() {
        try {
            if (isField) {
                if (isInt()) {
                    field.setInt(parent, PApplet.round(val));
                } else if (isFloat()) {
                    field.setFloat(parent, val);
                } else if (isDouble()) {
                    field.setDouble(parent, val);
                }
            }
            else {
                if (isIntArray()) {
                    ((int[]) array)[index] = PApplet.round(val);
                } else if (isFloatArray()) {
                    ((float[]) array)[index] = val;
                } else if (isDoubleArray()) {
                    ((double[]) array)[index] = val;
                }
            }
        }
        catch (Exception e) {
            TpAnimation.log("An error occured during updating variable's value.", true);
            e.printStackTrace();
        }
    }

    /**
     * Checks type of assigned field.
     * @return true when assigned field is type <code>int</code>
     */
    public boolean isInt() {
        return fieldType.equals(int.class);
    }

    /**
     * Checks type of assigned field.
     * @return true when assigned field is type <code>float</code>
     */
    public boolean isFloat() {
        return fieldType.equals(float.class);
    }

    /**
     * Checks type of assigned field.
     * @return true when assigned field is type <code>double</code>
     */
    public boolean isDouble() {
        return fieldType.equals(double.class);
    }

    /**
     * Checks type of assigned array.
     * @return true when assigned field is type <code>int[]</code>
     */
    public boolean isIntArray() {
        return fieldType.equals(int[].class);
    }

    /**
     * Checks type of assigned array.
     * @return true when assigned field is type <code>float[]</code>
     */
    public boolean isFloatArray() {
        return fieldType.equals(float[].class);
    }

    /**
     * Checks type of assigned array.
     * @return true when assigned field is type <code>double[]</code>
     */
    public boolean isDoubleArray() {
        return fieldType.equals(double[].class);
    }

    /**
     * Updates the field's value based on parent animation progress.
     * @param progress parent animation progress
     */
    public void update(float progress) {
        if (ignore) return;

        updateLocalProgress(progress);
        val = PApplet.lerp(from, to, localProgress);
        updateFieldValue();
    }

    /**
     * Updates field's local progress based on parent animation progress and eventual time bounds.<br>
     * Also applies easing.
     * @param progress the parent animation progress
     */
    private void updateLocalProgress(float progress) {
        if (isFullLength) {
            localProgress = progress;
        }
        else {
            localProgress = PApplet.constrain(progress, startProgress, endProgress);
            localProgress = PApplet.map(localProgress, startProgress, endProgress, 0.0f, 1.0f);
        }
        if (ease) {
            localProgress = TpEasing.ease(localProgress, easingFunctionNumber);
        }
    }

    /**
     * Calculates progress bounds based on transition start and end time offsets and parent animation's duration.
     */
    public void updateProgressBounds() {
        startProgress = PApplet.map(startMillis, 0, animation.getDurationMillis(), 0, 1);
        endProgress = PApplet.map(endMillis, 0, animation.getDurationMillis(), 0, 1);
    }

    /**
     *
     * @return parent animation
     */
    public TpAnimation getAnimation() {
        return animation;
    }

    /**
     *
     * @return assigned field's name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return assigned field
     */
    public Field getField() {
        return field;
    }

    /**
     *
     * @return assigned field type
     */
    public Class getFieldType() {
        return fieldType;
    }

    /**
     *
     * @return the animated item index in array
     */
    public int getIndex() {
        return index;
    }


    /**
     * Tells whether field will be ignored during updates or not.
     * @return true when assigning the field failed in the constructor
     */
    public boolean isIgnored() {
        return ignore;
    }

    /**
     *
     * @return local progress
     */
    public float getLocalProgress() {
        return localProgress;
    }

    /**
     *
     * @return local progress at which the transition will start
     */
    public float getStartProgress() {
        return startProgress;
    }

    /**
     *
     * @return local progress at which the transition will end
     */
    public float getEndProgress() {
        return endProgress;
    }

    /**
     *
     * @return current field's value
     */
    public float getVal() {
        return val;
    }

    /**
     *
     * @return the initial value for transition
     */
    public float getFrom() {
        return from;
    }

    /**
     *
     * @param from the initial value for transition
     */
    public void setFrom(float from) {
        this.from = from;
    }

    /**
     *
     * @return the final value for transition
     */
    public float getTo() {
        return to;
    }

    /**
     *
     * @param to the final value for transition
     */
    public void setTo(float to) {
        this.to = to;
    }

    /**
     *
     * @return true when transition will last full parent animation's length
     */
    public boolean isFullLength() {
        return isFullLength;
    }

    /**
     *
     * @param fullLength flag indicating if transition will last full parent animation's length
     */
    public void setFullLength(boolean fullLength) {
        isFullLength = fullLength;
    }

    /**
     *
     * @return start time in animation for value transition
     */
    public int getStartMillis() {
        return startMillis;
    }

    /**
     * Sets start time in animation for value transition.<br>
     * After setting this value you must call {@link #updateProgressBounds()}
     * @param startMillis start time in animation for value transition
     */
    public void setStartMillis(int startMillis) {
        this.startMillis = startMillis;
    }

    /**
     *
     * @return end time in animation for value transition
     */
    public int getEndMillis() {
        return endMillis;
    }

    /**
     * Sets end time in animation for value transition.<br>
     * After setting this value you must call {@link #updateProgressBounds()}
     * @param endMillis end time in animation for value transition
     */
    public void setEndMillis(int endMillis) {
        this.endMillis = endMillis;
    }

    /**
     *
     * @return true when easing enabled
     */
    public boolean isEase() {
        return ease;
    }

    /**
     *
     * @param easingFunctionNumber number indicating which easing function to use
     * @see TpEasing available easing functions.
     */
    public void setEasing(int easingFunctionNumber) {
        this.easingFunctionNumber = easingFunctionNumber;
    }
}