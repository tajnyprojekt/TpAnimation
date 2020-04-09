import com.tajnyprojekt.tpanimation.*;

// when the animation rendering is finished, if you have ffmpeg installed
// go to the my-awesome-feedback/ directory in the terminal and run
// ffmpeg -f image2 -r 60 -i %d.png -vcodec libx264 -profile:v high444 -refs 16 -crf 0 -preset slow ../awesome-feedback.mp4
// to encode rendered frames into mp4 video

TpAnimation animation;

public float flowX, flowY;
public float size, col;

void setup() {
   size(1280, 720, P3D);
   
   // create 8000ms (8s) long animation and add variables
   animation = new TpAnimation(this, 3000)
               .exitOnRenderFinish(true) // close the sketch after rendering
               .setLoopMirror(true)
               .setOutputFrameRate(60) // set render frame rate to 60 fps
               // you can change the render output directory (default is animationOutput/)
               .setOutputDir("my-awesome-feedback") 
               .addVariableAnimation("flowX", -5, 10, TpEasing.CIRC_INOUT)
               .addVariableAnimation("flowY", -10, 5, TpEasing.BACK_IN)
               .addVariableAnimation("size", 300, 600, TpEasing.ELASTIC_INOUT)
               .addVariableAnimation("col", 0, 255)
               ;
         
   // render 8 loops of the animation      
   // if you dont want to render the animation, just change render() to loop()
   animation.render(8);  
   
   noFill();
   background(0);
}

void draw() {
  image(get(), -flowX / 2, -flowY / 2, width + flowX, height + flowY);
  stroke(col);
  translate(width / 2, height / 2);
  box(size);
}