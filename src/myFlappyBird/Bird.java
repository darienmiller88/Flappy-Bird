package myFlappyBird;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Let me introduce you to my bird class! This class will contain all of the necessary information needed to create the
 * bird in Flappy Bird. It will include the coordinates of the bird, how fast it can flap, how fast is falls and a list
 * of sprites read in by the main program that will allow it to be animated! This class implements the ImageObserver class
 * due to the drawImage method from the Graphics library requiring it as a parameter. 	
 * 
 * @author Darien Miller
 *
 */
public class Bird implements ImageObserver {
	private static final double AIR_RESISTANCE = 0.95, FLAP_RESISTANCE = 0.93;
	private final int WINDOW_HEIGHT, START_X, START_Y;
	private double velocity, acceleration, gravity, flapForce;
	private int x, y, frame, birdWidth, birdHeight;
	private Integer score;//score must be an Integer because in the main class, we will convert its value to a string, and print it to the screen
	private ArrayList<BufferedImage> birdSprites;
	
	/**
	 * Constructor to take initialize bird object with these specific values
	 * 
	 * @param x - the x position of the bird in the window
	 * @param y - the y position of the bird in the window
	 * @param windowHeight - how tall the window is. The bird class needs this information to prevent itself from
	 * fall off of the screen on its descent.
	 */
	public Bird(int x, int y, int windowHeight, double flapForce) {
		gravity = 0.6; // the gravity constant determines the rate at which the velocity changes over time
		WINDOW_HEIGHT = windowHeight;
		birdSprites = new ArrayList<BufferedImage>();//ArrayList of bird sprites that will be needed to animate the bird
		this.x = x;
		this.y = y;
		this.flapForce = (flapForce > 0) ? -flapForce : flapForce;//We need to ensure that the flap force is negative, so if the value sent to the constructor
		//is positive, assign the negative of that value to the flapForce
		START_X = x;
		START_Y = y;
		score = new Integer(0);
	}
	
	/**
	 * Method to add bird sprites to the ArrayList of BufferedImages.  
	 * 
	 * @param birdSpritePath - The file path that leads to the bird image being read in from the main class.
	 * @throws IOException - throw an Input - Output if the file read in can't be found
	 */
	public void addToSpriteList(File birdSpritePath) throws IOException {	
		birdSprites.add(ImageIO.read(birdSpritePath));
		birdWidth = birdSprites.get(0).getWidth();
		birdHeight = birdSprites.get(0).getHeight();
	}
	
	public int getBirdHeight() {
		return birdHeight;
	}
	
	public int getBirdWidth() {
		return birdWidth;
	}
	
	
	/**
	 * Method to cycle through the ArrayList of images, and print them to the screen in a way that resembles animation.
	 * This is accomplished by printing out a image every frame that the paint() method in the main class. The variable "frame"
	 * will be used to determine which image in the birdSprites ArrayList to print out on a particular frame of execution. 
	 * @param g - graphics object to print out the image to the screen.
	 */
	public void animateBird(Graphics g, boolean isGameOver) {
		g.drawImage(birdSprites.get(frame), x, y, this);
		
		//if the user did not crash the bird into a pipe and cause a game over, increase the frame count.
		if(!isGameOver)
			frame++;
		
		//once the amount of frames is equal to the size of the array, set it back to 0, and allow the first image to be printed. This is what causes
		//the illusion of animation: in place, the 
		if(frame == birdSprites.size())
			frame = 0;
	}
	
	//increase score lol
	public void increaseScore() {
		score++;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	//return the score so it can be printed out
	public Integer getScore() {
		return score;
	}
	
	//return the bird's x position
	public int getX() {
		return x;
	}
	
	//return the bird's y position
	public int getY() {
		return y;
	}
	
	/**
	 * This method will apply a force to the birds acceleration, which will then be added to the velocity
	 */
	public void flap() {
		//When the user presses the space bar in the main class, a negative force (negative to move the bird upwards) will be applied to the acceleration. By
		//making the acceleration some negative value, it will be added to the velocity, decreasing it over time, and causing the bird to soar up.
		acceleration += flapForce;
	}
	
	/**
	 * This method will apply the gravity constant to the bird's velocity every single frame, increasing its overall speed over time.
	 */
	public void fall() {
		//continuously add a gravitational force to the object to force it to the ground. 
		acceleration += gravity;
		velocity += acceleration;
		
		//Only apply air resistance to the velocity if the velocity is positive (object falling down). If applied to the object when soaring upwards, the
		//force pushing it up would be opposed by air resistance, which only works upwards, not downwards.
		if(velocity >= 0)
			velocity *= AIR_RESISTANCE;
		
		//In order to restrict how fast the bird can flap upwards, I made up this idea of a force that, alongside gravity, also opposes the force of the 
		//birds flap. This is to prevent the bird from soaring too high when the force is applied by the space bar.
		else 
			velocity *= FLAP_RESISTANCE;
		
		//finally, add the velocity to the position.
		y += (int)velocity;
		
		//In order to ensure that the bird does not fall of the screen , constrain the bird's y position to either the very top of the screen, and the ground
		if(y + birdHeight >= WINDOW_HEIGHT) {
			y = WINDOW_HEIGHT - birdHeight;
			velocity = 0;
		}else if(y <= 0)
			y = 0;
		
		//we need to set the acceleration to 0 in order to maintain the original acceleration value. If this is not done, the acceleration will
		//increase exponentially, which will make the velocity follow suit and impossible to push upwards. 
		acceleration = 0;
	}
	
	public double getVelocity() {
		return velocity;
	}
	
	public void reset() {
		x = START_X;
		y = START_Y;
	}
	
	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
