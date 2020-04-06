package com.tajnyprojekt.tpanimation;

import processing.core.PApplet;
import java.lang.reflect.Field;

// requires field to be public to animate it

public class TpAnimatedVariable {

    private PApplet parent;
    private TpAnimation animation;

    private String name;
    private Field field;
    private Class fieldType;
    private boolean ignore;

    public float localProgress;
    public float startProgress;
    public float endProgress;

    public float val;
    public float from;
    public float to;

    public boolean isFullLength;
    public int startMillis;
    public int endMillis;

    public boolean ease;

    public TpAnimatedVariable(TpAnimation animation, String varName, float from, float to, boolean ease) {
        this(animation, varName, from, to, 0, 0, ease);
        isFullLength = true;
    }

    public TpAnimatedVariable(TpAnimation animation, String varName, float from, float to,
                              int startMillis, int endMillis, boolean ease) {
        this.animation = animation;
        this.parent = animation.parent;
        this.name = varName;
        ignore = false;
        findFieldInSketch(varName);
        this.from = from;
        this.to = to;
        isFullLength = false;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        updateProgressBounds();
        this.ease = ease;
    }

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

    private void updateFieldValue() {
        try {
            if (isInt()) {
                field.setInt(parent, PApplet.round(val));
            }
            else if (isFloat()) {
                field.setFloat(parent, val);
            }
            else if (isDouble()) {
                field.setDouble(parent, val);
            }
        }
        catch (Exception e) {
            TpAnimation.log("An error occured during updating variable's value.", true);
            e.printStackTrace();
        }
    }

    public boolean isInt() {
        return fieldType.toString().equals("int");
    }

    public boolean isFloat() {
        return fieldType.toString().equals("float");
    }

    public boolean isDouble() {
        return fieldType.toString().equals("double");
    }

    public void update(float progress) {
        if (ignore) return;

        updateLocalProgress(progress);
        val = PApplet.lerp(from, to, localProgress);
        updateFieldValue();
    }

    private void updateLocalProgress(float progress) {
        if (isFullLength) {
            localProgress = progress;
        }
        else {
            localProgress = PApplet.constrain(progress, startProgress, endProgress);
            localProgress = PApplet.map(localProgress, startProgress, endProgress, 0.0f, 1.0f);
        }
        if (ease) {
            localProgress = easeInOut(localProgress);
        }
    }

    public void updateProgressBounds() {
        startProgress = PApplet.map(startMillis, 0, animation.getDurationMillis(), 0, 1);
        endProgress = PApplet.map(endMillis, 0, animation.getDurationMillis(), 0, 1);
    }

    public float easeInOut(float t) {
        float sqt = t * t;
        return sqt / (2.0f * (sqt - t) + 1.0f);
    }

    public TpAnimation getAnimation() {
        return animation;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public boolean isIgnored() {
        return ignore;
    }

    public float getLocalProgress() {
        return localProgress;
    }

    public float getStartProgress() {
        return startProgress;
    }

    public float getEndProgress() {
        return endProgress;
    }

    public float getVal() {
        return val;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public boolean isFullLength() {
        return isFullLength;
    }

    public void setFullLength(boolean fullLength) {
        isFullLength = fullLength;
    }

    public int getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(int startMillis) {
        this.startMillis = startMillis;
    }

    public int getEndMillis() {
        return endMillis;
    }

    public void setEndMillis(int endMillis) {
        this.endMillis = endMillis;
    }

    public boolean isEase() {
        return ease;
    }

    public void setEase(boolean ease) {
        this.ease = ease;
    }
}