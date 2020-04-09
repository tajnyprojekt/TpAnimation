import com.tajnyprojekt.tpanimation.*;

TpAnimation animation;

public float h;

color c1 = color(106, 96, 248);
color c2 = color(84, 220, 186);

void setup() {
  size(400, 400);
  noStroke();
  
  animation = new TpAnimation(this, 2500)
        .addVariableAnimation("h", 0, height, TpEasing.BOUNCE_OUT);
        
  animation.loop();
}

void draw() {
  background(c1);
  fill(c2);
  rect(0, 0, width, h); 
}

void swapColors() {
  color cTmp = c1;
  c1 = c2;
  c2 = cTmp;
}

public void onLoopEnd(TpAnimation a) {
  swapColors();  
}