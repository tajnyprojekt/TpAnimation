import com.tajnyprojekt.tpanimation.*;

TpAnimation hoverAnimation;
TpAnimation clickAnimation;

// variables to be animated
public float boxScale1 = 1.0;
public float boxScale2 = 1.0;

// sprites
PImage cursor, pointer, pointerClick, box;

int boxCenterX = 280;
int boxCenterY = 220;

// app state flags
boolean isMouseOver = false;
boolean isMousePressed = false;

void setup() {  
  size(1280, 820);
  
  // animations setup, we need two separate animation objects to run them independently
  hoverAnimation = new TpAnimation(this, 400)
        .addVariableAnimation("boxScale1", 1.0, 1.2, TpEasing.SINE_OUT);
   
  clickAnimation = new TpAnimation(this, 400)
        .addVariableAnimation("boxScale2", 1.0, 0.6, TpEasing.EXPO_OUT);
      
  
  // load images and set some styles
  cursor = loadImage("cursor.png");
  pointer = loadImage("pointer.png");
  pointerClick = loadImage("pointer-click.png");
  box = loadImage("box.png");
  noCursor();
  textFont(createFont("Arial Bold", 100));
  textSize(100);
  textAlign(CENTER);
  textLeading(96);
  stroke(0, 143, 255);
  fill(0, 143, 255);
}

void draw() {
  
  // make some feedback when mouse is pressed over the box
  if (isMousePressed && mousePressed) {
    image(get(), -10, -10, width + 20, height + 20);  
  }
  else {
    background(12, 232, 163);
  }
  
  // draw box
  float boxW = box.width * boxScale1 * boxScale2;
  float boxH = box.height * boxScale1 * boxScale2;
  float boxPosX = boxCenterX - boxW / 2;
  float boxPosY = boxCenterY - boxH / 2;
  image(box, boxPosX, boxPosY, boxW, boxH);
  text("click\nME!", boxCenterX, boxCenterY + 200);
  
  // check if mouse is over the box
  if (mouseX >= boxPosX 
      && mouseX <= boxPosX + boxW 
      && mouseY >= boxPosY
      && mouseY <= boxPosY + boxH) {
        // trigger zoom in the box if it is not zoomed
        if (!isMouseOver && !hoverAnimation.isPlaying()) {
           hoverAnimation.setForwardPlayback(true).play();
           isMouseOver = true;
        }
        // trigger push button back on mouse pressed
        if (mousePressed && !isMousePressed && !clickAnimation.isPlaying()) {
           clickAnimation.setForwardPlayback(true).play();
           isMousePressed = true;
        }
  } // mouse left
  else if (isMouseOver && !hoverAnimation.isPlaying()) {
    hoverAnimation.stop();
    hoverAnimation.setForwardPlayback(false).play();
    isMouseOver = false;
  }
  
  // handle mouse release
  if (!mousePressed && !clickAnimation.isPlaying() && isMousePressed) {
    clickAnimation.setForwardPlayback(false).play();
    isMousePressed = false;
  }
  
  drawCursor();
}

void drawCursor() {
    if (isMouseOver || isMousePressed) {
      if (isMousePressed) {
        image(pointerClick, mouseX - 90, mouseY - 13);
      }
      else {
        image(pointer, mouseX - 90, mouseY - 13);
      }
    }
    else {
      image(cursor, mouseX - 90, mouseY - 13);
    }
}