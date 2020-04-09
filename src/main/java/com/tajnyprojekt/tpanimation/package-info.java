/**
 * <h2>TpAnimation library</h2>
 * TpAnimation is a library for <a href="https://processing.org/">Processing</a>,
 * that aims to simplfy the process of creating animations.
 * It takes care of value transitions, easing, interpolation, timing and playback.
 * It can also render your animation to frames.<br><br>
 * All you have to do is to define the start and end values for your sketch's variables or arrays,
 * set the animation duration (and eventually a couple of other parameters) and start playback.<br>
 * You can play the animation just once with {@link com.tajnyprojekt.tpanimation.TpAnimation#play()},
 * play in a loop with {@link com.tajnyprojekt.tpanimation.TpAnimation#loop()}, then
 * {@link com.tajnyprojekt.tpanimation.TpAnimation#stop()} or
 * {@link com.tajnyprojekt.tpanimation.TpAnimation#pause()} it, and when you are OK with the results
 * {@link com.tajnyprojekt.tpanimation.TpAnimation#render()} it.
 * You also can {@link com.tajnyprojekt.tpanimation.TpAnimation#setForwardPlayback(boolean)},
 * {@link com.tajnyprojekt.tpanimation.TpAnimation#setLoopMirror(boolean)}
 * and a couple of other settings.<br>
 * There are {@link com.tajnyprojekt.tpanimation.TpEasing 32 easing functions available} to experiment with.<br><br>
 *
 * To get started download the library and place it in Processing library folder
 * (for more info see <a href="https://github.com/processing/processing/wiki/How-to-Install-a-Contributed-Library">here.</a>).<br>
 * If you have it done, then let's import the library and define some variables:<br><br>
 *<pre><code class="language-processing">
 *import com.tajnyprojekt.tpanimation.*;
 *
 *TpAnimation animation;
 *public float h;
 *
 * ...
 *</code></pre>
 * Note the <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html">public</a> modifier
 * before the variable <code>a</code>, we're gonna use this variable in our animation, and TpAnimation requires
 * the variable to be <code>public</code> to work with.<br><br>
 * Next initialize the sketch and create 2500ms long animation, add transition for variable <code>h</code> between
 * <code>0</code> and <code>height</code> (window's height). Play the animation in an infinite loop at the end of
 * <code>setup()</code>. Then you can use your animated value in <code>draw()</code> e.g. to contol rect's height.<br><br>
 *<pre><code class="language-processing">
 * ...
 *
 *void setup() {
 *  size(400, 400);
 *
 *  animation = new TpAnimation(this, 2500)
 *      .addVariableAnimation("h", 0, height);
 *
 *  animation.loop();
 *}
 *
 *void draw() {
 *  background(106, 96, 248);
 *  fill(84, 220, 186);
 *  rect(0, 0, width, h);
 *}
 *
 *</code></pre>
 *
 * When you run the sketch it should produce an animation like this:<br><br>
 * <img src="../../../../assets/images/basic-result.gif"><br><br>
 * As you see the value of <code>h</code> is changing automatically inside <code>draw()</code>.
 * To take advantage of that, don't override animated values inside <code>draw()</code>.<br><br>
 *
 *
 * Fine, but what I we want it to look more like this one below?<br><br>
 * <img src="../../../../assets/images/basic-bounce.gif"><br><br>
 * Then you gonna use some easing and swap the colors at the end of each loop.<br>
 * Here's an example how to do this:<br><br>
 *
 *<pre><code class="language-processing">
 *import com.tajnyprojekt.tpanimation.*;
 *
 *TpAnimation animation;
 *
 *public float h;
 *
 *&sol;/ create variables for colors to be able to swap them
 *color c1 = color(106, 96, 248);
 *color c2 = color(84, 220, 186);
 *
 *void setup() {
 *  size(400, 400);
 *  noStroke();
 *
 *  animation = new TpAnimation(this, 2500)     // add bounce out easing to the transition
 *        .addVariableAnimation("h", 0, height, TpEasing.BOUNCE_OUT);
 *
 *  animation.loop();
 *}
 *
 *void draw() {
 *  background(c1);
 *  fill(c2);
 *  rect(0, 0, width, h);
 *}
 *
 *void swapColors() {
 *  color cTmp = c1;
 *  c1 = c2;
 *  c2 = cTmp;
 *}
 *
 *&sol;/ TpAnimation notifies you at the end of each animation loop
 *&sol;/ with the onLoopEnd event, here's where the color swap goes
 *public void onLoopEnd(TpAnimation a) {
 *  swapColors();
 *}
 *
 *</code></pre>
 * <br><br>
 * This covers the basic usage and concpets of the library, for further reading please see
 * {@link com.tajnyprojekt.tpanimation.TpAnimation TpAnimation class overview}
 * and examples included with the library.<br><br>
 * From this tutorial:<br>
 * <ul>
 *     <li>{@link com.tajnyprojekt.tpanimation.TpAnimation TpAnimation}</li>
 *     <li>{@link com.tajnyprojekt.tpanimation.TpAnimation#addVariableAnimation(String, float, float, int, int, int)}</li>
 *     <li>{@link com.tajnyprojekt.tpanimation.TpAnimation#loop()}</li>
 *     <li>{@link com.tajnyprojekt.tpanimation.TpEasing available easing functions}</li>
 * </ul>
 * <br>
 * If you found a bug, want a new feature, have questions or just wanna leave some comment please do so on:<br>
 * <code>michal.urbanski@tajnyprojekt.com</code><br><br>
 *
 *  __<br>
 * <i>created by Michal Urbanski (<a href="https://tajnyprojekt.com">tajny_projekt</a>),<br>
 * during the Corona Time 2020</i><br><br>
 *
 */
package com.tajnyprojekt.tpanimation;
