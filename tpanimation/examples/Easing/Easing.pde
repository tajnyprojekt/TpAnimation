import com.tajnyprojekt.tpanimation.*;


TpAnimation animation;

// time variable
public float t;

// array for all possible easing results
public float[] easingResults;

int tileSize = 80;
int tileGap = 50; // the distance between tiles
int dotSize = 5;

// tiles grid size
int gridW = 11;
int gridH = 3;

// array for easing names
String[] names;

void setup() {
    size(1480, 510, P2D);

    // initialize the array
    easingResults = new float[TpEasing.COUNT];
    
    // create and initialize 2000ms long animation 
    animation = new TpAnimation(this, 2000)
        // add time animation from 0 to 1 without easing (linear)
        .addVariableAnimation("t", 0, 1, TpEasing.LINEAR);
    
    
    // to each array item assign corresponding easing function number
    // animate the value between 0..1
    for (int i = 0; i < TpEasing.COUNT; i++) {
      animation.addArrayItemAnimation(easingResults, i, 0, 1, i);
    }
    
    // prepare array with easing names
    createNames();
    
    // setup text
    textAlign(CENTER);
    textFont(createFont("monospace", 15));
    textSize(15);

    // start the animation in an infinite loop
    animation.loop();
    background(255);
}


void draw() {
  fill(255, 6);
  noStroke();
  rect(0, 0, width, height);
  int index = 0;
  int x = tileGap;
  int y = tileGap;
  for (int col = 0; col < gridW; col++) {
    y = tileGap;
    for (int row = 0; row < gridH; row++) {
      if (!(col == 0 && row == 2)) {
        drawEasingTile(x, y, index);
        index++;
      }
      y += tileSize + tileGap + 20;
    }
    x += tileSize + tileGap;
  }
}

void drawEasingTile(int x, int y, int easing) {
  noStroke();
  fill(52, 136, 153);
  ellipse(x + t * tileSize, 
          y + tileSize * (1 - easingResults[easing]), 
          dotSize, dotSize);
  stroke(0);
  fill(0);
  text(names[easing], x + tileSize / 2, y + tileSize + 30);
}

void createNames() {
  names = new String[TpEasing.COUNT];
  names[0] = "LINEAR";      
  names[1] = "BASIC_INOUT"; 
  names[2] = "QUAD_IN";     
  names[3] = "QUAD_OUT";    
  names[4] = "QUAD_INOUT";  
  names[5] = "CUBIC_IN";    
  names[6] = "CUBIC_OUT";   
  names[7] = "CUBIC_INOUT"; 
  names[8] = "QUART_IN";    
  names[9] = "QUART_OUT";   
  names[10] = "QUART_INOUT"; 
  names[11] = "QUINT_IN";    
  names[12] = "QUINT_OUT";   
  names[13] = "QUINT_INOUT"; 
  names[14] = "SINE_IN";     
  names[15] = "SINE_OUT";    
  names[16] = "SINE_INOUT";  
  names[17] = "CIRC_IN";     
  names[18] = "CIRC_OUT";    
  names[19] = "CIRC_INOUT";  
  names[20] = "EXPO_IN";     
  names[21] = "EXPO_OUT";    
  names[22] = "EXPO_INOUT";  
  names[23] = "BACK_IN";     
  names[24] = "BACK_OUT";    
  names[25] = "BACK_INOUT";  
  names[26] = "BOUNCE_IN";   
  names[27] = "BOUNCE_OUT";  
  names[28] = "BOUNCE_INOUT";
  names[29] = "ELASTIC_IN";  
  names[30] = "ELASTIC_OUT"; 
  names[31] = "ELASTIC_INOUT";
}