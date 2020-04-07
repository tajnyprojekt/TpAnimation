package com.tajnyprojekt.tpanimation;

import processing.core.PApplet;
import java.util.ArrayList;

/**
 * TpAnimation is the core responsible for playback and managing the animation of Processing sketch's fields.<br>
 * It allows for multiple types of playback like single one, backwards, loop and loop with mirror (back and forth)
 * as well as rendering sketch to frames.<br>
 * The constructor and setters are created in a way similar to builder pattern, that means you can call setters in a chain
 * like in the example below.<br>
 * TpAnimation allows you to configure your value transitions once, during sketch initialization, in a little declarative way,
 * and then does everything for you behind the scenes so you can focus on your awesome visuals and stuff instead
 * coding transitions, timers, interpolations and easing.<br>
 * When your animation looks perfect you can easily render it as frames with <code>render()</code> method, and then assemble
 * generated frames with ffmpeg, PDE's Movie Maker or other tool you like.<br>
 * Default render frame rate is 30fps, you can change it with <code>setFrameRate()</code> method<br><br>
 *
 * Example basic usage:<br>
 *     <code>
 *         import com.tajnyprojekt.tpanimation.TpAnimation;<br><br>
 *         TpAnimation animation;<br><br>
 *         // create variables controlling the state of your sketch in the main scope - and mark them as <b>public</b>!<br>
 *         public float a;<br><br>
 *         void setup() {<br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;... // initialize your sketch<br><br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;// create and initialize the animation<br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;animation = new TpAnimation(this, 1000)<br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setLoopMirror(true)<br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.addVariableToAnimation("a", -10.0, 10.0);<br><br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;// start the animation<br>
 *             &nbsp;&nbsp;&nbsp;&nbsp;animation.play();<br>
 *         }<br><br>
 *         void draw() {<br>
 *         &nbsp;&nbsp;&nbsp;&nbsp;println(a); // this will print a smooth sequence of numbers going from -10 to 10 and backwards<br>
 *         }<br>
 *     </code>
 *
 * @author Michal Urbanski (tajny_projekt)
 */
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

    /**
     * A String pattern used to generate rendered frame's filename with <code>String.format()</code>.
     * Must contain an integer flag <code>%d</code> or it's variation and an extension.
     * Extension can be any image extension supported by Processing.
     */
    private String outputFilenamePattern;
    private String outputDir;
    private int outputIndexOffset;

    /**
     * Name of the library used in logging.
     */
    static final String LIB_NAME = "TpAnimation";

    /**
     * Logs a message tagged with library name to the standard output.
     *
     * @param msg the log message
     */
    static void log(String msg) {
        log(msg, false);
    }

    /**
     * Logs a message tagged with library name to the selected output type.
     *
     * @param msg the log message
     * @param err flag that tells whether to log to error or standard output
     */
    static void log(String msg, boolean err) {
        final String log = LIB_NAME + ": " + msg;
        if (err) {
            System.err.println(log);
        }
        else {
            System.out.println(log);
        }
    }

    /**
     * Creates the animation object and initializes the library.
     *
     * @param parent the parent Processing sketch
     * @param durationMillis duration of the animation in milliseconds
     */
    public TpAnimation(PApplet parent, int durationMillis) {
        this.parent = parent;
        this.durationMillis = durationMillis;

        setInitialState();

        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);

    }

    /**
     * Sets an initial state with default values.
     */
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


    /**
     * Method that is registered in the parent PApplet, executed before sketch's <code>draw()</code><br>
     * Used to animate sketch's values.
     */
    public void pre() {
        updateVariables();
    }

    /**
     * Method that is registered in the parent PApplet, executed after sketch's <code>draw()</code><br>
     * Updates state and progress of the animation.
     */
    public void draw() {
        if (!isPlaying) return;

        if (isRendering) {
            updateRendering();
        }
        else {
            updatePlayback();
        }
    }

    /**
     * Starts playback of the animation.<br>
     * If executed directly, plays the animation once.<br>
     * Can be used to resume playback after pause even in loop mode.
     */
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

    /**
     * Starts playback of the animation in a loop.<br>
     * Use <code>stop()</code> to disable looping.
     * @see #stop()
     */
    public void loop() {
        isLooping = true;
        loopCount = 0;
        play();
    }

    /**
     * Starts rendering of the animation.<br>
     * The animation will be stopped and rendered from the beginning when already playing.
     */
    public void render() {
        if (isRendering) return;
        stop();
        numberOfFrames = PApplet.round(outputFrameRate * durationMillis / 1000.0f);
        isRendering = true;
        play();
        log("rendering started");
    }

    /**
     * Pauses playback of the animation.<br>
     * Use <code>play()</code> or <code>loop()</code> to resume or <code>stop()</code> to reset the animation.
     */
    public void pause() {
        isPlaying = false;
        isPaused = true;
    }

    /**
     * Stops playback, looping and rendering of the animation.
     * After <code>stop()</code> the animation is in <code>finished</code> state.
     */
    public void stop() {
        isPlaying = false;
        isLooping = false;
        isRendering = false;
        isPaused = false;
        isFinished = true;
    }

    /**
     * Prepares the initial animation state for playback.<br>
     * Meant to be called inside methods that can start playback.
     */
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

    /**
     * Iterates over the list of variables and updates their state based on current progress.
     */
    private void updateVariables() {
        for (TpAnimatedVariable var : variables) {
            var.update(progress);
        }
    }

    /**
     * Updates the state and progress of playback when <b>not rendering</b> frames in time based manner.
     */
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

    /**
     * Updates the state and progress of playback when <b>rendering</b> frames based on frame counting.
     */
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

    /**
     * Updates and calculated the time elapsed since start of the animation / loop.
     */
    private void updateTimer() {
        int currentMillis = parent.millis();
        elapsedMillis += currentMillis - lastFrameMillis;
        elapsedMillis = PApplet.constrain(elapsedMillis, 0, durationMillis);
        lastFrameMillis = currentMillis;
    }


    // management

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * @see #addVariableToAnimation(String, float, float, int, int, boolean) this method for full description with an example.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @return the animation object to chain another method calls
     */
    public TpAnimation addVariableToAnimation(String name, float from, float to) {
        return addVariableToAnimation(name, from, to, true);
    }

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * @see #addVariableToAnimation(String, float, float, int, int, boolean) this method for full description with an example.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param ease flag that indicates whether to use easing
     * @return the animation object to chain another method calls
     */
    public TpAnimation addVariableToAnimation(String name, float from, float to, boolean ease) {
        return addVariableToAnimation(new TpAnimatedVariable(this, name, from, to, ease));
    }

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * @see #addVariableToAnimation(String, float, float, int, int, boolean) this method for full description with an example.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param startMillis start time in animation for value transition
     * @param endMillis end time in animation for value transition
     * @return the animation object to chain another method calls
     */
    public TpAnimation addVariableToAnimation(String name, float from, float to, int startMillis, int endMillis) {
        return addVariableToAnimation(name, from, to, startMillis, endMillis, true);
    }

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br><br>
     * Example:<br>
     * <code>
     *
     *     TpAnimation animation;<br><br>
     *
     *     // won't work without the <b>public</b> keyword!<br>
     *     <b>public</b> int myVar1;<br>
     *     <b>public</b> float myVar2;<br><br>
     *
     *     void setup() {<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;animation = new TpAnimation(this, 1000)<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.addVariableToAnimation("myVar1", 1, 100)<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.addVariableToAnimation("myVar2", 0.1, 3.5);<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;...<br>
     *     }<br>
     * </code>
     * <br>
     * Use <code>startMillis</code> and <code>endMillis</code> when want to animate the variable's value not throughout
     * the whole animation time but for a certain period.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param startMillis start time in animation for value transition
     * @param endMillis end time in animation for value transition
     * @param ease flag that indicates whether to use easing
     * @return the animation object to chain another method calls
     */
    public TpAnimation addVariableToAnimation(String name, float from, float to,
                                              int startMillis, int endMillis, boolean ease) {
        return addVariableToAnimation(new TpAnimatedVariable(
                this, name, from, to, startMillis, endMillis, ease));
    }

    /**
     * Adds object defining the variable to be animated.<br>
     * Recommended method is to use {@link #addVariableToAnimation(String, float, float, int, int, boolean)}
     * or it's variations.
     *
     * @param variable already configured variable object
     * @return the animation object to chain another method calls
     */
    public TpAnimation addVariableToAnimation(TpAnimatedVariable variable) {
        variables.add(variable);
        return this;
    }

    /**
     *
     * @return the list of all variables added to animation
     */
    public ArrayList<TpAnimatedVariable> getVariables() {
        return variables;
    }


    /**
     *
     * @return animation duration in milliseconds
     */
    public int getDurationMillis() {
        return durationMillis;
    }

    /**
     * Sets new animation duration in milliseconds.<br>
     * Not allowed to call during animation playback or paused state.
     *
     * @param durationMillis new animation duration in milliseconds
     * @return the animation object to chain another method calls
     */
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

    /**
     * Sets new animation duration in seconds.<br>
     * Not allowed to call during animation playback or paused state.
     *
     * @param seconds new animation duration in seconds
     * @return the animation object to chain another method calls
     */
    public TpAnimation setDurationSeconds(float seconds) {
        int millis = PApplet.round(seconds * 1000);
        return setDurationMillis(millis);
    }

    /**
     *
     * @return number of frames that will be rendered.
     */
    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    /**
     *
     * @return number of frames that have been already rendered
     */
    public int getRenderedFrames() {
        return renderedFrames;
    }

    /**
     *
     * @return current progress of the animation in range 0..1
     */
    public float getProgress() {
        return progress;
    }

    /**
     *
     * @return number of loops done since staring the loop
     */
    public int getLoopCount() {
        return loopCount;
    }


    /**
     *
     * @return if animation is rendering
     */
    public boolean isRendering() {
        return isRendering;
    }

    /**
     * Returns true when the playback is active.
     * The playback is active during normal playback, looping and rendering.
     *
     * @return if animation is playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     *
     * @return if animation is playing in loop mode
     */
    public boolean isLooping() {
        return isLooping;
    }

    /**
     *
     * @return if animation is in paused mode
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     *
     * @return if animation is looping in mirror mode
     */
    public boolean isLoopMirror() {
        return isMirroring;
    }

    /**
     * Sets looping in mirror mode. That means that after calling <code>loop()</code>
     * the animation will play from the beggining to the end,
     * and then in reverse from the end to the beginning and so on.<br>
     * When mirror mode is disabled, each loop is starting from the beginning of the animation.<br>
     * Not allowed to call during animation playback or paused state.
     *
     * @param isMirroring flag that enables or disables looping in mirror mode
     * @return the animation object to chain another method calls
     */
    public TpAnimation setLoopMirror(boolean isMirroring) {
        if (isPlaying || isPaused) {
            log("Playback in progress - setting duration not allowed. Stop the animation first.", true);
            return this;
        }
        this.isMirroring = isMirroring;
        return this;
    }

    /**
     *
     * @return if animation is or will play forward or backward
     */
    public boolean isForwardPlayback() {
        return isForwardPlayback;
    }

    /**
     * Sets the direction of playback. Can be used to control the direction when playing,
     * looping or rendering<br>
     * Not allowed to call during animation playback or paused state.
     *
     * @param isPlaybackForward flag indicating the playback direction
     * @return the animation object to chain another method calls
     */
    public TpAnimation setForwardPlayback(boolean isPlaybackForward) {
        if (isPlaying || isPaused) {
            log("Playback in progress - changing outputFilenamePattern not allowed. Stop the animation first.",
                    true);
            return this;
        }
        this.isForwardPlayback = isPlaybackForward;
        return this;
    }

    /**
     *
     * @return if animation has finished playback or was stopped
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Tells the animation to close the sketch after rendering is done.<br>
     * Can be helpful when rendering long animations in high resolutions that lasts lots of time.
     *
     * @param exitOnFinish flag indicating whether to exit sketch after rendering finished
     * @return the animation object to chain another method calls
     */
    public TpAnimation exitOnRenderFinish(boolean exitOnFinish) {
        this.exitOnRenderFinish = exitOnFinish;
        return this;
    }


    /**
     *
     * @return frame rate used for rendering
     */
    public int getOutputFrameRate() {
        return outputFrameRate;
    }

    /**
     * Sets the frame rate used for rendering output.<br>
     * Not allowed to call during animation rendering.
     *
     * @param frameRate frame rate used for rendering
     * @return the animation object to chain another method calls
     */
    public TpAnimation setOutputFrameRate(int frameRate) {
        if (isRendering) {
            log("Rendering in progress - changing outputFrameRate not allowed.", true);
            return this;
        }
        this.outputFrameRate = frameRate;
        return this;
    }

    /**
     *
     * @return the filename pattern used to generate the output frame names
     */
    public String getOutputFilenamePattern() {
        return outputFilenamePattern;
    }

    /**
     * Sets a String pattern used to generate rendered frame's filename with <code>String.format()</code>.<br>
     * Must contain an integer flag <code>%d</code> or it's variation and an extension, read more in
     * <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html">Java Formatter docs</a>.<br>
     * Extension can be any image extensions supported by Processing, read more in
     * <a href="https://processing.org/reference/save_.html">Processing <code>save()</code> docs</a>.<br>
     * Don't start the filename pattern with slash <code>'/'</code>!<br><br>
     * Example:<br>
     *     <code>
     *         "frame-%04d.jpg" // tells the formatter to pad the frame number with four leading zeros and jpg extension<br>
     *         "%d.png" // default value - results in 0.png, 1.png and so on<br>
     *     </code>
     * <br>
     * Not allowed to call during animation rendering.
     *
     * @param outputFilenamePattern pattern used to generate rendered frame's filename
     * @return the animation object to chain another method calls
     */
    public TpAnimation setOutputFilenamePattern(String outputFilenamePattern) {
        if (isRendering) {
            log("Rendering in progress - changing outputFilenamePattern not allowed.", true);
            return this;
        }
        this.outputFilenamePattern = outputFilenamePattern;
        return this;
    }

    /**
     *
     * @return directory name that rendered frames will be output to
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Sets the name of the output directory for rendering.<br>
     * The name can be provided with, or without a slash at the end.<br>
     * A directory with provided name will be created in sketch's main directory.<br>
     * Not allowed to call during animation rendering.
     *
     * @param outputDir name of the output directory
     * @return the animation object to chain another method calls
     */
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

    /**
     *
     * @return full output path pattern
     */
    public String getOutputPathPattern() {
        return outputDir + outputFilenamePattern;
    }

    /**
     *
     * @return offset used when assigning indexes to rendered filenames
     */
    public int getOutputIndexOffset() {
        return outputIndexOffset;
    }

    /**
     * Sets the offset used when assigning indexes to rendered filenames.<br>
     * Can be helpfull when you want to append some frames to already rendered animation.<br>
     * Not allowed to call during animation rendering.
     *
     * @param outputIndexOffset offset to use when assigning indexes to rendered filenames
     * @return the animation object to chain another method calls
     */
    public TpAnimation setOutputIndexOffset(int outputIndexOffset) {
        if (isRendering) {
            log("Rendering in progress - changing outputIndexOffset not allowed.", true);
            return this;
        }
        this.outputIndexOffset = outputIndexOffset;
        return this;
    }
}
