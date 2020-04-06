package com.tajnyprojekt.tpanimation;


import processing.core.PApplet;

import java.util.ArrayList;

public class TpAnimation {

    PApplet parent;

    private ArrayList<TpAnimatedVariable> variables;

    private int durationMillis;
    private int lastFrameMillis;
    private int elapsedMillis;

    private int numberOfFrames;
    private int currentFrame;
    private int renderedFrames;

    private float progress;

    private int loopCount;

    private boolean isRendering;
    private boolean isPlaying;
    private boolean isLooping;
    private boolean isPaused;
    private boolean isMirroring;
    private boolean isForwardPlayback;
    private boolean isFinished;
    private boolean exitOnRenderFinish;

    private int outputFrameRate;
    // uses patterns with String.format(), must contain %d flag
    private String outputFilenamePattern;
    private String outputDir;
    private int outputIndexOffset;


    static final String LIB_NAME = "TpAnimation";

    static void log(String msg) {
        log(msg, false);
    }

    static void log(String msg, boolean err) {
        final String log = LIB_NAME + ": " + msg;
        if (err) {
            System.err.println(log);
        }
        else {
            System.out.println(log);
        }
    }


    public TpAnimation(PApplet parent, int durationMillis) {
        this.parent = parent;
        this.durationMillis = durationMillis;

        setInitialState();

        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);

    }

    private void setInitialState() {
        variables = new ArrayList<TpAnimatedVariable>();
        lastFrameMillis = 0;
        elapsedMillis = 0;
        outputFrameRate = 30;
        numberOfFrames = outputFrameRate * durationMillis / 1000;
        renderedFrames = 0;
        currentFrame = 0;
        progress = 0;
        loopCount = 0;
        isRendering = false;
        isPlaying = false;
        isLooping = false;
        isMirroring = false;
        isForwardPlayback = true;
        isFinished = false;
        exitOnRenderFinish = false;
        outputFilenamePattern = "%d.png";
        outputDir = "animationOutput/";
        outputIndexOffset = 0;
    }


    // actions


    public void pre() {
        updateVariables();
    }

    public void draw() {
        if (!isPlaying) return;

        if (isRendering) {
            updateRendering();
        }
        else {
            updatePlayback();
        }
    }

    public void play() {
        if (!isPlaying) {
            prepare();
        }
        isPlaying = true;
        if (isPaused) {
            isPaused = false;
            lastFrameMillis = parent.millis();
        }
    }

    public void loop() {
        isLooping = true;
        loopCount = 0;
        play();
    }

    public void render() {
        if (isRendering) return;
        stop();
        numberOfFrames = PApplet.round(outputFrameRate * durationMillis / 1000.0f);
        isRendering = true;
        play();
        log("rendering started");
    }

    public void pause() {
        isPlaying = false;
        isPaused = true;
    }

    public void stop() {
        isPlaying = false;
        isLooping = false;
        isRendering = false;
        isPaused = false;
        isFinished = true;
    }

    // call directly before starting playback
    private void prepare() {
        renderedFrames = 0;
        loopCount = 0;
        elapsedMillis = 0;
        lastFrameMillis = parent.millis();
        isFinished = false;
        if (isForwardPlayback) {
            currentFrame = 0;
            progress = 0;
        }
        else {
            currentFrame = numberOfFrames - 1;
            progress = 1;
        }
    }

    private void updateVariables() {
        for (TpAnimatedVariable var : variables) {
            var.update(progress);
        }
    }

    private void updatePlayback() {
        // check if finished
        if (elapsedMillis >= durationMillis) {
            if (isLooping) {
                elapsedMillis = 0;
                loopCount++;
                if (isMirroring) {
                    isForwardPlayback = !isForwardPlayback;
                }
            }
            else {
                stop();
            }
        }

        // update progress
        updateTimer();

        if (isForwardPlayback) {
            progress = PApplet.map(elapsedMillis, 0, durationMillis, 0.0f, 1.0f);
        }
        else {
            progress = PApplet.map(elapsedMillis, 0, durationMillis, 1.0f, 0.0f);
        }
    }

    private void updateRendering() {
        // save sketch's frame
        parent.save(String.format(getOutputPathPattern(), outputIndexOffset + renderedFrames));

        // update progress
        renderedFrames++;
        if (isForwardPlayback) {
            currentFrame++;
        }
        else {
            currentFrame--;
        }
        progress = PApplet.map(currentFrame, 0, numberOfFrames - 1, 0.0f, 1.0f);
        log(String.format("rendering %d",
                PApplet.floor(100 * renderedFrames / (float) numberOfFrames)) + "%");

        // check if finished
        if (renderedFrames >= numberOfFrames) {
            stop();
            log("rendering done.");
            if (exitOnRenderFinish) {
                parent.exit();
            }
        }
    }

    private void updateTimer() {
        int currentMillis = parent.millis();
        elapsedMillis += currentMillis - lastFrameMillis;
        elapsedMillis = PApplet.constrain(elapsedMillis, 0, durationMillis);
        lastFrameMillis = currentMillis;
    }


    // management


    public TpAnimation addVariableToAnimation(String name, float from, float to) {
        return addVariableToAnimation(name, from, to, true);
    }

    public TpAnimation addVariableToAnimation(String name, float from, float to, boolean ease) {
        return addVariableToAnimation(new TpAnimatedVariable(this, name, from, to, ease));
    }

    public TpAnimation addVariableToAnimation(String name, float from, float to, int startMillis, int endMillis) {
        return addVariableToAnimation(name, from, to, startMillis, endMillis, true);
    }

    public TpAnimation addVariableToAnimation(String name, float from, float to,
                                              int startMillis, int endMillis, boolean ease) {
        return addVariableToAnimation(new TpAnimatedVariable(
                this, name, from, to, startMillis, endMillis, ease));
    }

    public TpAnimation addVariableToAnimation(TpAnimatedVariable variable) {
        variables.add(variable);
        return this;
    }

    public ArrayList<TpAnimatedVariable> getVariables() {
        return variables;
    }



    public int getDurationMillis() {
        return durationMillis;
    }

    public TpAnimation setDurationMillis(int durationMillis) {
        if (isPlaying || isPaused) {
            log("Playback in progress - setting duration not allowed. Stop the animation first.", true);
            return this;
        }
        this.durationMillis = durationMillis;
        for (TpAnimatedVariable var : variables) {
            var.updateProgressBounds();
        }
        return this;
    }

    public TpAnimation setDurationSeconds(float seconds) {
        int millis = PApplet.round(seconds * 1000);
        return setDurationMillis(millis);
    }


    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    public int getRenderedFrames() {
        return renderedFrames;
    }

    public float getProgress() {
        return progress;
    }

    public int getLoopCount() {
        return loopCount;
    }



    public boolean isRendering() {
        return isRendering;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isLoopMirror() {
        return isMirroring;
    }

    public TpAnimation setLoopMirror(boolean isMirroring) {
        if (isPlaying || isPaused) {
            log("Playback in progress - setting duration not allowed. Stop the animation first.", true);
            return this;
        }
        this.isMirroring = isMirroring;
        return this;
    }

    public boolean isForwardPlayback() {
        return isForwardPlayback;
    }

    public TpAnimation setForwardPlayback(boolean isPlaybackForward) {
        if (isPlaying || isPaused) {
            log("Playback in progress - changing outputFilenamePattern not allowed. Stop the animation first.",
                    true);
            return this;
        }
        this.isForwardPlayback = isPlaybackForward;
        return this;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public TpAnimation exitOnRenderFinish(boolean exitOnFinish) {
        this.exitOnRenderFinish = exitOnFinish;
        return this;
    }




    public int getOutputFrameRate() {
        return outputFrameRate;
    }

    public TpAnimation setOutputFrameRate(int frameRate) {
        if (isRendering) {
            log("Rendering in progress - changing outputFrameRate not allowed.", true);
            return this;
        }
        this.outputFrameRate = frameRate;
        return this;
    }

    public String getOutputFilenamePattern() {
        return outputFilenamePattern;
    }

    public TpAnimation setOutputFilenamePattern(String outputFilenamePattern) {
        if (isRendering) {
            log("Rendering in progress - changing outputFilenamePattern not allowed.", true);
            return this;
        }
        this.outputFilenamePattern = outputFilenamePattern;
        return this;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public TpAnimation setOutputDir(String outputDir) {
        if (isRendering) {
            System.err.println("Rendering in progress - setting outputFilenamePattern not allowed.");
        }
        else {
            if (outputDir.endsWith("/") || outputDir.endsWith("\\")) {
                this.outputDir = outputDir;
            }
            else {
                this.outputDir = outputDir + "/";
            }
        }
        return this;
    }

    public String getOutputPathPattern() {
        return outputDir + outputFilenamePattern;
    }

    public int getOutputIndexOffset() {
        return outputIndexOffset;
    }

    public TpAnimation setOutputIndexOffset(int outputIndexOffset) {
        if (isRendering) {
            log("Rendering in progress - changing outputIndexOffset not allowed.", true);
            return this;
        }
        this.outputIndexOffset = outputIndexOffset;
        return this;
    }
}
