package myFlappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Frame rate class designed to calculate framerate and limit it to 60 FPS.
 * 
 * @author Darien Miller
 *
 */
public class FrameRate {
	private String frameRate;
	private long lastTime, startTicksFor60FPS;
    private long delta, maxFPS;
    private int frameCount;
    
    public FrameRate (){
    	startTicksFor60FPS = System.currentTimeMillis();
	    lastTime = System.currentTimeMillis();
	    frameRate = "FPS 0";
        maxFPS = 60;
    }
    
    /**
     * This method will be used to calculate the amount of frames per second (FPS) in another program. This is done
     * by keeping track of the "delta time", or the amount of time in milliseconds that has passed between when this
     * class was instantiated and when this method was called. 
     *  
     * @param delay will represent that potential amount of delay in milliseconds the thread calling the method may impose
     * This is needed to properly calculate the frames per second, as calculating it without accounting for the delay will
     * result in a much lower recorded framerate. If there is no delay, simply supply a 0.
     */
    public void calculateFPS(int delay){
    	startTicksFor60FPS = System.currentTimeMillis(); 
        long current = System.currentTimeMillis();
        delta += (current - lastTime) - delay;
        lastTime = current;
        frameCount++;

        if(delta > 1000){
            delta -= 1000;
            frameRate = String.format("FPS: %s", frameCount);
            frameCount = 0;	
        }
    }
    
    public void printFPS(Graphics g, int x, int y) {
    	g.setColor(Color.black);
    	g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString(frameRate, x, y);
    }
    
    /**
     * This method will ensure that when calculating the fps, it will never exceed 60. This is accomplished by forcing
     * the thread to sleep for a certain amount of milliseconds. In order to achieve an fps of 60 or lower, at least 16
     * milliseconds must go by with each frame, resulting in 1000 ms by 60 frames, or 1 sec (1000 ms = 1 sec).
     * If the difference in time between when this class was instantiated and when this method was called is less than
     * 16 milliseconds, delay the thread for the difference between 16 and the frameTicks value. What this accomplishes
     * is ensuring at least 16 milliseconds is spent on each frame. Ex frameTicks = 6. 16 - 6 = 10; Sleep for 10 ms.
     */
    public void limitFPSTo60()  {
    	long frameTicks = System.currentTimeMillis() - startTicksFor60FPS;
    	if(frameTicks < 1000.0 / maxFPS) {
			try {
				Thread.sleep((1000 / maxFPS) - frameTicks);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
	
    public String getFrameRate(){
        return frameRate;
    }

}
