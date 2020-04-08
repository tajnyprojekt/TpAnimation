package com.tajnyprojekt.tpanimation;

import processing.core.PApplet;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * TpAnimation is the main class responsible for playback and managing the animation of Processing sketch's fields.<br>
 * It allows for multiple types of playback like single forward, backwards, loop and loop with mirror (back and forth),
 * as well as rendering your animation to frames.<br><br>

 * TpAnimation allows you to configure value transitions once, during sketch initialization, in a little declarative way,
 * and then does all the calculations for you behind the scenes, so you can focus on your awesome visuals and stuff, instead
 * coding transitions, timers, interpolations and easings.<br>
 * You can choose from 32 available easing functions (30 of them come from
 * <a href="https://github.com/jesusgollonet/processing-penner-easing">Robert Penner's easing library</a>.
 * See {@link TpEasing} for the list of all available easing functions waiting for you.<br><br>
 *
 * The constructor, setters and <code>add..</code> methods are created in a way that enables you to call them in a chain
 * like in the example below.<br>
 * Keep in mind that when the animation is playing, looping or rendering the animated variables' values will be set
 * at the beginning of each <code>draw()</code> call. The animation won't work if you override the values
 * inside your sketch's <code>draw()</code> method.<br><br>
 *
 * The library fires an event when playback or rendering is finished (but not when <code>stop()</code> is called).
 * Implement a method <code class="language-processing">public void onAnimationFinished(TpAnimation a) { ... }</code>
 * to listen to this event and get notified when animation is finished.<br><br>
 *
 * When your animation looks perfect you can easily render it as frames with <code>{@link #render()}</code> method. After that, you can find the frames in the <code>animationOutput/</code> directory in your sketch's folder. You have to assemble
 * generated frames to a video on your own with e.g. ffmpeg, PDE's Movie Maker, After Effects or any other tool you like.<br>
 * Default render frame rate is 30fps, you can change it with <code>{@link #setOutputFrameRate(int)}</code> method<br><br>
 *
 *
 * Example basic usage:<br><br>
 *<pre><code class=language-processing>
 *import com.tajnyprojekt.tpanimation.*;
 *
 *
 *TpAnimation animation;
 *
 *&sol;/ create variables or arrays controlling the state of sketch in the main scope
 *&sol;/ and mark them as <b>public</b>,
 *&sol;/ the initial values will be overridden by the animation
 *public float x = 170;
 *public float[] r = {120, 140};
 *
 *void setup() {
 *
 *    size(400, 400);
 *
 *    // create and initialize the animation
 *    animation = new TpAnimation(this, 1000)
 *        // set the animation to play back and forth during looping
 *        .setLoopMirror(true)
 *        // add variable <i>x</i> and animate it from 170 to 230,
 *        // starting after 800ms and ending after 1400 since start of the animation
 *        .addVariableAnimation("x", 170, 230, 800, 1400)
 *        // add first item of <i>r</i> array
 *        // and animate it from 170 to 280 with elastic out easing
 *        .addArrayItemAnimation(r, 0, 0, 280, TpEasing.ELASTIC_OUT)
 *        // add second item of <i>r</i> array ...
 *        .addArrayItemAnimation(r, 1, 0, 240, TpEasing.BOUNCE_OUT)
 *        ;
 *
 *    // start the animation in an infinite loop
 *    animation.loop();
 *}
 *
 *
 *void draw() {
 *
 *    // use your animated variables to create a simple blinking eye animation
 *    background(0);
 *    noStroke();
 *    fill(69, 247, 148);
 *    ellipse(200, 200, a, b);
 *    fill(0);
 *    ellipse(x, 200, 120, 120);
 *}
 *
 *&sol;/ you can also implement an event to know when the animation is finished
 *&sol;/ Note: it won't be called by the animation in this example,
 *&sol;/       because it plays in an infinite loop, but you'll see if you change
 *&sol;/        <i>animation.loop()</i> to <i>animation.play()</i> in <i>setup()</i> above
 *public void onAnimationFinished(TpAnimation a) {
 *    println(String.format("The animation has finished after %d loops.", a.getLoopCount()));
 *}
 *
 *</code></pre><br><br>
 * Example output:<br><br>
 * <img src="../../../../assets/images/eye.gif"><br><br>
 * For more look at the examples included with the library.<br><br>
 * __<br>
 * <i>created by Michal Urbanski (<a href="https://tajnyprojekt.com">tajny_projekt</a>),<br>
 * during Corona Time 2020</i><br><br>
 * @see #TpAnimation(PApplet, int)
 * @see #setLoopMirror(boolean)
 * @see #addVariableAnimation(String, float, float, int, int, int)
 * @see #loop()
 * @see TpEasing available easing functions
 *
 */
public class TpAnimation {

    PApplet parent;

    private ArrayList<TpAnimatedVariable> variables;

    private int durationMillis;
    private int lastFrameMillis;
    private int elapsedMillis;

    private int numberOfFrames; // number of frames in a single loop
    private int totalNumberFrames; // total number of frames to be rendered
    private int currentFrame;
    private int renderedFrames;

    private float progress;
    private int loopCount;
    private int loopsToDo; // 0 means infinite number of loops

    private boolean isRendering;
    private boolean isPlaying;
    private boolean isLooping;
    private boolean isPaused;
    private boolean isMirroring;
    private boolean isForwardPlayback; // flag that is triggered during mirrored loops
    private boolean isForwardPlaybackSetting; // user setting (if playback starts from end to beginning)

    private boolean isFinished;
    private boolean exitOnRenderFinish;
    private Method finishedEvent;

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
     * @param durationMillis the duration of the animation in milliseconds
     */
    public TpAnimation(PApplet parent, int durationMillis) {
        this.parent = parent;
        this.durationMillis = durationMillis;

        setInitialState();

        parent.registerMethod("pre", this);
        parent.registerMethod("draw", this);
        findFinishedEventCallback();
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
        totalNumberFrames = numberOfFrames;
        renderedFrames = 0;
        currentFrame = 0;
        progress = 0;
        loopCount = 0;
        loopsToDo = 1;
        isRendering = false;
        isPlaying = false;
        isLooping = false;
        isMirroring = false;
        isForwardPlayback = true;
        isForwardPlaybackSetting = true;
        isFinished = false;
        exitOnRenderFinish = false;
        outputFilenamePattern = "%d.png";
        outputDir = "animationOutput/";
        outputIndexOffset = 0;
    }

    /**
     * Checks if the user has implemented
     * public void onAnimationFinished(TpAnimation a)
     */
    private void findFinishedEventCallback() {
        try {
            finishedEvent =
                    parent.getClass().getMethod("onAnimationFinished",
                            new Class[] { TpAnimation.class });
            log("found onAnimationFinished callback");
        } catch (Exception e) {
            log("no callback set");
        }
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
     * Starts playback of the animation in an infinite loop.<br>
     * Use <code>stop()</code> to disable looping.
     * @see #stop()
     */
    public void loop() {
        loop(0); // 0 means infinite number of loops
    }

    /**
     * Starts playback of <code>n</code> loops of the animation.<br>
     * Use <code>stop()</code> to disable looping.
     * @param n the number of loops to render, 0 or negative indicates infinite loop
     * @see #stop()
     */
    public void loop(int n) {
        isLooping = true;
        if (n < 0) n = 0;
        this.loopsToDo = n;
        loopCount = 0;
        play();
    }

    /**
     * Starts rendering of the animation.<br>
     * The animation will be stopped and rendered from the beginning when already playing.
     */
    public void render() {
        render(1);
    }

    /**
     * Starts rendering of <code>n</code> loops of the animation.<br>
     * The animation will be stopped and rendered from the beginning when already playing.
     * @param n the number of loops to render, must be greater than 0
     */
    public void render(int n) {
        if (isRendering || n <= 0) return;
        stop();
        loopsToDo = n;
        numberOfFrames = PApplet.round(outputFrameRate * durationMillis / 1000.0f);
        totalNumberFrames = loopsToDo * numberOfFrames;
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
        loopsToDo = 1;
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
        isForwardPlayback = isForwardPlaybackSetting;
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
            loopCount++;
            if (isLooping) {
                elapsedMillis = 0;
                if (isMirroring) {
                    isForwardPlayback = !isForwardPlayback;
                }
                if (loopsToDo != 0 && loopCount >= loopsToDo) {
                    finish();
                }
            }
            else {
                finish();
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

        if (isForwardPlayback && currentFrame == numberOfFrames - 1
                || !isForwardPlayback && currentFrame == 0) { // end of loop
            loopCount++;
            if (isMirroring) {
                isForwardPlayback = !isForwardPlayback;
            }
            else {
                if (isForwardPlayback) {
                    currentFrame = 0;
                }
                else {
                    currentFrame = numberOfFrames - 1;
                }
            }
        }


        progress = PApplet.map(currentFrame, 0, numberOfFrames - 1, 0.0f, 1.0f);
        log(String.format("rendering %d",
                PApplet.floor(100 * renderedFrames / (float) totalNumberFrames)) + "%");

        // check if finished
        if (renderedFrames >= totalNumberFrames) {
            log("rendered " + renderedFrames + " frames\nrendering done.");
            finish();
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

    private void finish() {
        stop();
        if (finishedEvent != null) {
            try {
                finishedEvent.invoke(parent, new Object[] {this});
            }
            catch (Exception e) {
                log("onAnimationFinished callback error - disabling.", true);
                e.printStackTrace();
                finishedEvent = null;
            }
        }
    }


    // management

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * @see #addVariableAnimation(String, float, float, int, int, int) this method for full description with an example.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @return the animation object to chain another method calls
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addVariableAnimation(String name, float from, float to) {
        return addVariableAnimation(name, from, to, TpEasing.BASIC_INOUT);
    }

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * @see #addVariableAnimation(String, float, float, int, int, int) this method for full description with an example.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param easingFunctionNumber the number indicating which easing function to use
     * @return the animation object to chain another method calls
     * @see TpEasing available easing functions.
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addVariableAnimation(String name, float from, float to, int easingFunctionNumber) {
        return addVariableAnimation(new TpAnimatedVariable(this, name, from, to, easingFunctionNumber));
    }

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br>
     * @see #addVariableAnimation(String, float, float, int, int, int) this method for full description with an example.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param startMillis the start time in animation for value transition
     * @param endMillis the end time in animation for value transition
     * @return the animation object to chain another method calls
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addVariableAnimation(String name, float from, float to, int startMillis, int endMillis) {
        return addVariableAnimation(name, from, to, startMillis, endMillis, TpEasing.BASIC_INOUT);
    }

    /**
     * Adds the sketch's field to the animation. The field will be identified by it's name.<br>
     * The type of the passed field must be declared as <code>int</code>, <code>float</code> or <code>double</code>
     * and marked as public in the main scope of your Processing sketch in order to be animated.<br><br>
     * Example:<br><br>
     *<pre><code class="language-processing">
     *import com.tajnyprojekt.tpanimation.*;
     *
     *
     *TpAnimation animation;<br><br>
     *
     *&sol;/ won't work without the <b>public</b> modifier!
     *<b>public</b> int myVar1;
     *<b>public</b> float myVar2;
     *
     *void setup() {
     *    ...
     *    animation = new TpAnimation(this, 1000)
     *        .addVariableAnimation("myVar1", 1, 100)
     *        .addVariableAnimation("myVar2", 0.1, 3.5, 200, 800, TpEasing.EXPO_IN)
     *        ;
     *    ...
     *}
     *</code></pre>
     * <br>
     * Use <code>startMillis</code> and <code>endMillis</code> when want to animate the variable's value not throughout
     * the whole animation time but for a certain period.
     *
     * @param name the name of the sketch's field that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param startMillis the start time in animation for value transition
     * @param endMillis the end time in animation for value transition
     * @param easingFunctionNumber the number indicating which easing function to use
     * @return the animation object to chain another method calls
     * @see TpEasing available easing functions.
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addVariableAnimation(String name, float from, float to,
                                            int startMillis, int endMillis, int easingFunctionNumber) {
        return addVariableAnimation(new TpAnimatedVariable(
                this, name, from, to, startMillis, endMillis, easingFunctionNumber));
    }

    /**
     * Adds the sketch's array item to the animation. The item will be identified by an array and it's index.<br>
     * The type of the passed array must be declared as <code>int[]</code>, <code>float[]</code> or <code>double[]</code>
     * in the main scope of your Processing sketch in order to be animated.<br><br>
     *
     * @param array the array containing field that will be animated
     * @param index the index of the array item that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @return the animation object to chain another method calls
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addArrayItemAnimation(Object array, int index, float from, float to) {
        return addArrayItemAnimation(array, index, from, to, TpEasing.BASIC_INOUT);
    }

    /**
     * Adds the sketch's array item to the animation. The item will be identified by an array and it's index.<br>
     * The type of the passed array must be declared as <code>int[]</code>, <code>float[]</code> or <code>double[]</code>
     * in the main scope of your Processing sketch in order to be animated.<br><br>
     *
     * @param array the array containing field that will be animated
     * @param index the index of the array item that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param easingFunctionNumber the number indicating which easing function to use
     * @return the animation object to chain another method calls
     * @see TpEasing available easing functions.
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addArrayItemAnimation(Object array, int index, float from, float to, int easingFunctionNumber) {
        return addVariableAnimation(new TpAnimatedVariable(this, array, index, from, to, easingFunctionNumber));
    }

    /**
     * Adds the sketch's array item to the animation. The item will be identified by an array and it's index.<br>
     * The type of the passed array must be declared as <code>int[]</code>, <code>float[]</code> or <code>double[]</code>
     * in the main scope of your Processing sketch in order to be animated.<br><br>
     *
     * @param array the array containing field that will be animated
     * @param index the index of the array item that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param startMillis the start time in animation for value transition
     * @param endMillis the end time in animation for value transition
     * @return the animation object to chain another method calls
     * @see #addArrayItemAnimation(Object, int, float, float, int, int)
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addArrayItemAnimation(Object array, int index, float from, float to, int startMillis, int endMillis) {
        return addArrayItemAnimation(array, index, from, to, startMillis, endMillis, TpEasing.BASIC_INOUT);
    }

    /**
     * Adds the sketch's array item to the animation. The item will be identified by an array and it's index.<br>
     * The type of the passed array must be declared as <code>int[]</code>, <code>float[]</code> or <code>double[]</code>
     * in the main scope of your Processing sketch in order to be animated.<br><br>
     * Example:<br><br>
     *<pre><code class="language-processing">
     *import com.tajnyprojekt.tpanimation.*;
     *
     *
     *TpAnimation animation;<br><br>
     *
     *&sol;/ declare your array
     *float[] params = {0.2, 0.7};
     *
     *
     *void setup() {
     *    ...
     *    // configure the animation
     *    animation = new TpAnimation(this, 1000)
     *        .addArrayItemAnimation(params, 0, 1, 100)
     *        .addArrayItemAnimation(params, 1, 0.1, 3.5, 200, 800, TpEasing.EXPO_IN)
     *        ;
     *    ...
     *}
     *</code></pre>
     * <br>
     * Use <code>startMillis</code> and <code>endMillis</code> when want to animate the variable's value not throughout
     * the whole animation time but for a certain period.
     *
     * @param array the array containing field that will be animated
     * @param index the index of the array item that will be animated
     * @param from the initial value for transition
     * @param to the final value for transition
     * @param startMillis the start time in animation for value transition
     * @param endMillis the end time in animation for value transition
     * @param easingFunctionNumber the number indicating which easing function to use
     * @return the animation object to chain another method calls
     * @see TpEasing available easing functions.
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addArrayItemAnimation(Object array, int index, float from, float to,
                                            int startMillis, int endMillis, int easingFunctionNumber) {
        return addVariableAnimation(new TpAnimatedVariable(
                this, array, index, from, to, startMillis, endMillis, easingFunctionNumber));
    }

    /**
     * Adds object defining the variable to be animated.<br>
     * Recommended method is to use {@link #addVariableAnimation(String, float, float, int, int, int)}
     * or it's variations.
     *
     * @param variable the already configured variable object
     * @return the animation object to chain another method calls
     * @see #addVariableAnimation(String, float, float, int, int, int)
     * @see TpAnimatedVariable
     */
    public TpAnimation addVariableAnimation(TpAnimatedVariable variable) {
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
     * @param durationMillis the new animation duration in milliseconds
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
     * @param seconds the new animation duration in seconds
     * @return the animation object to chain another method calls
     */
    public TpAnimation setDurationSeconds(float seconds) {
        int millis = PApplet.round(seconds * 1000);
        return setDurationMillis(millis);
    }

    /**
     *
     * @return the number of frames that will be rendered.
     */
    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    /**
     *
     * @return the number of frames that have been already rendered
     */
    public int getRenderedFrames() {
        return renderedFrames;
    }

    /**
     *
     * @return the current progress of the animation in range 0..1
     */
    public float getProgress() {
        return progress;
    }

    /**
     *
     * @return the number of loops done since staring the loop
     */
    public int getLoopCount() {
        return loopCount;
    }


    /**
     *
     * @return true if animation is rendering
     */
    public boolean isRendering() {
        return isRendering;
    }

    /**
     * Returns true when the playback is active.
     * The playback is active during normal playback, looping and rendering.
     *
     * @return true if animation is playing
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     *
     * @return true if animation is playing in loop mode
     */
    public boolean isLooping() {
        return isLooping;
    }

    /**
     *
     * @return true if animation is in paused mode
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     *
     * @return true if animation is looping in mirror mode
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
     * @param isMirroring the flag that enables or disables looping in mirror mode
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
     * @return if animation will play forward or backward
     */
    public boolean isForwardPlayback() {
        return isForwardPlayback;
    }

    /**
     * Sets the direction of playback. Can be used to control the direction when playing,
     * looping or rendering<br>
     * Not allowed to call during animation playback or paused state.
     *
     * @param isPlaybackForward the flag indicating the playback direction
     * @return the animation object to chain another method calls
     */
    public TpAnimation setForwardPlayback(boolean isPlaybackForward) {
        if (isPlaying || isPaused) {
            log("Playback in progress - changing outputFilenamePattern not allowed. Stop the animation first.",
                    true);
            return this;
        }
        this.isForwardPlaybackSetting = isPlaybackForward;
        return this;
    }

    /**
     *
     * @return true if animation has finished playback or was stopped
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
     * @return the frame rate used for rendering
     */
    public int getOutputFrameRate() {
        return outputFrameRate;
    }

    /**
     * Sets the frame rate used for rendering output.<br>
     * Not allowed to call during animation rendering.
     *
     * @param frameRate the frame rate used for rendering
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
     * @param outputFilenamePattern the pattern used to generate rendered frame's filename
     * @return the animation object to chain another method calls
     * @see #setOutputDir(String)
     * @see #getOutputPathPattern()
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
     * @see #setOutputFilenamePattern(String)
     * @see #getOutputPathPattern()
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
     * @return the full output path pattern
     */
    public String getOutputPathPattern() {
        return outputDir + outputFilenamePattern;
    }

    /**
     *
     * @return the offset used when assigning indexes to rendered filenames
     */
    public int getOutputIndexOffset() {
        return outputIndexOffset;
    }

    /**
     * Sets the offset used when assigning indexes to rendered filenames.<br>
     * Can be helpfull when you want to append some frames to already rendered animation.<br>
     * Not allowed to call during animation rendering.
     *
     * @param outputIndexOffset the offset to use when assigning indexes to rendered filenames
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
