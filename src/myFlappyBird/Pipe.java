package myFlappyBird;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * This class will contain all of the information needed to define the properties that make up a pipe in FlappyBird. Rather than making each Pipe instance
 * contain one pipe image, a Pipe will contain two, with one at the top of the screen, and one at the bottom. This will make printing a pair of pipes to the
 * screen much more simplified. Again, like with the Bird class, this class will implement the ImageObserver interface due to it being a parameter in the
 * drawImage method. The class will contain two BufferedImages that will hold the image of a pipe .jpg or .png read in by the main class.
 * 
 * @author Darien Miller
 *
 */
public class Pipe implements ImageObserver {
	private int x, topY, bottomY, xSpeed, bottomPipeHeight, topPipeHeight;
	private final int PIPE_GAP, PIPE_WIDTH, WINDOW_WIDTH, WINDOW_HEIGHT;
	private Random rand;
	private BufferedImage topPipe, bottomPipe;
	private boolean passedBird;//in order to determine if the pipe has passed the bird, this boolean is required to ensure that the bird isn't given extra
	//points once the pipe has moved behind where the bird is. 

	
	/**
	 * Constructor to create a pipe object
	 * 
	 * @param windowWidth - width of the JFrame window in main class.
	 * @param windowHeight - height of the JFrame window in main class.
	 * @param groundHeight - height of the ground in the JFrame window in main class.
	 * @param topPipe - image object containing the picture of top pipe
	 * @param bottomPipe - image object containing the picture of the bottom pipe 
	 * @throws IOException - throw exception if the file path was not found
	 */
	public Pipe(int windowWidth, int windowHeight, int groundHeight, File topPipePath, File bottomPipePath) throws IOException {
		rand = new Random ();
		this.topPipe = ImageIO.read(topPipePath);
		this.bottomPipe = ImageIO.read(bottomPipePath);
		WINDOW_HEIGHT = windowHeight;
		WINDOW_WIDTH = windowWidth;
		PIPE_WIDTH = this.topPipe.getWidth();
		PIPE_GAP = 130;//Arbitrarily choose a number of pixels to represent how far apart the pipes will be from each other.
		
		
		/**
		 * Here, we will choose a random height for the bottom pipe. The way we will go about altering the heights of the pipe is more involved than what is
		 * expected. The pipe image we will read in will have a very large height, allowing a small portion of it to be printed onto the screen, with most of
		 * it off of the screen. We can control how much of the pipe is printed on screen by determining how many pixels of the pipe we want to show onto the 
		 * screen, with the minimum being enough to go slightly over the ground, and the max being the entire pipe height.
		 */
		bottomPipeHeight = random(groundHeight + 100, this.bottomPipe.getHeight());
		
		/**
		 * in order to determine what portion of the top pipe will be printed onto the screen, simply subtract the sum of the pipe gap and bottom pipe from the
		 * window height. This works because the sum of the top pipe, bottom pipe and gap in between the pipes in pixels is equal to the height of the window
		 * in the vanilla Flappy Bird game.
		 */
		topPipeHeight = WINDOW_HEIGHT - (bottomPipeHeight + PIPE_GAP);
		
		/**
		 * When printing out an image in the drawImage() method, the top left corner of the image is considered the reference point by which the (x, y) coordinates
		 * are located. Since the top pipe image needs to display the bottom portion where the lip of the pipe is located, we will initially print the image
		 * at negative of its height so the bottom of the pipe will be at y = 0. Afterwards, move it down by the value of "topPipeHeight" so it will be shown onto
		 * the screen.
		 */
		topY = -this.topPipe.getHeight() + topPipeHeight;
		
		/**
		 * To find the proper y location to print out the bottom pipe, simply subtract the "height" of the pipe from the bottom of the window. So, if the "height"
		 * of the bottom pipe ended up being 250 pixels, and the window height is 600, 600 - 250 = 350. The pipe will therefore be at the point (x, y = 350)
		 * where x is a number slightly larger than the width of the window.
		 */
		bottomY = WINDOW_HEIGHT - bottomPipeHeight;
		
		/**
		 * In order to produce the illusion of the pipes sliding onto the screen, rather than instantly being printed onto the screen, have the pipe's x position
		 * be slightly larger than the width of the window, which will print the pipe to the right of the screen. When the pipe begins moving, it will then move
		 * onto the screen.
		 */
		x = WINDOW_WIDTH - this.bottomPipe.getWidth();
		
		//choose arbitrary value for how fast the pipe will move
		xSpeed = 5;
		
		//set this boolean to false as the newly created pipe has not seen the bird yet.
		passedBird = false;
		
	}

	//random method simplified to take in a min and max value
	private int random(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * This method will check to see if a pipe object has passed the bird object. If true increase the bird's score, and set the boolean to true to ensure 
	 * that the bird is not awarded anymore points
	 * 
	 * @param b - the bird object
	 */
	public void pipeMeetsBird(Bird b) {
		//if the x position of the pipe plus half its width is less than or equal to the x position of the bird, and the pipe has not seen the bird yet, set
		//increase the birds score, and set the passedBird boolean to true. This is due to the fact the once the pipe has passed the bird, its x will always
		//be less than that of the birds x, causing the first part of the if statement to always be true, thus it will always reward the bird points.
		if(x + PIPE_WIDTH / 2 <= b.getX() && !passedBird) {
			b.increaseScore();
			passedBird = true;
		}
	}
	
	/**
	 * This method will check to see if a pipe object and a bird object made contact. It will check to see if the bird it the side of a pipe, as well as 
	 * the top or bottom of a pipe. If that is the case, true is returned to the main class to signal a game over, and false otherwise
	 * 
	 * @param b - the bird object
	 * @return true or false depending on whether or not if the bird hit a pipe.
	 */
	public boolean pipeHitsBird(Bird b) {
		
		//checks to see if the bird hit the side of a pipe by measuring the distance between the pipe and the front of the bird, as well as whether or
		//not if the bird is below the bottom pipe, or above the top pipe
		if(Math.abs(b.getX() - x) <= b.getBirdWidth() && (b.getY() >= bottomY || b.getY() <= topPipeHeight)) 
			return true;
		
		//checks to see if the bird landed on top of the bottom pipe or if hit it the bottom of the top pipe
		else if((b.getX() >= x && b.getX() + b.getBirdWidth() <= x + PIPE_WIDTH) && (b.getY() + b.getBirdHeight() >= bottomY || b.getY() <= topPipeHeight)) 
			return true;
		return false;
	}
	
	//produce movement in the pipe by decreasing its x by the xSpeed, causing it to "move" from the right side of the screen to the left
	public void movePipe() {
		x -= xSpeed;
	}
	
	public int getX() {
		return x;
	}
	
	public int getWidth() {
		return PIPE_WIDTH;
	}
	
	public void printPipe(Graphics g) {
		g.drawImage(topPipe, x, topY, this);
		g.drawImage(bottomPipe, x, bottomY, this);
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
