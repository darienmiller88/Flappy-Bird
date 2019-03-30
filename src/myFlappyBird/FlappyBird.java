package myFlappyBird;

import java.awt.Color;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
/**
 * The main class whereby the game logic will be invoked. The class inherits from the JPanel container, allowing me direct access to its methods, and it will
 * implement the ActionListener and KeyListener interfaces to determine how often the paint() method is called and allow keyboard input, respectively.
 * 
 * @author Darien Miller
 *
 */
public class FlappyBird extends JPanel implements ActionListener, KeyListener{
	private static final int WIDTH = 600, HEIGHT = 800;
	private int frames, groundX, groundY, groundImageWidth, groundSpeed = 5, highScore;
    private FrameRate FPS;
	private Timer t;
	private JFrame frame;
	private Bird bird;
	private LinkedList<Pipe> pipes;
	private Image sky, ground, gameOver;
	private boolean isGameOver, gameIsStarted;

	public FlappyBird() throws IOException {
		frame = new JFrame("Flappy Bird!");
		gameOver = ImageIO.read(new File("C:\\Users\\Darien Miller\\Desktop\\flappyBirdGameOver.png"));
		sky = ImageIO.read(new File("C:\\Users\\Darien Miller\\Desktop\\flappyBirdSunnyBackground.png")).getScaledInstance(WIDTH, HEIGHT, Image.SCALE_DEFAULT);
		ground = ImageIO.read(new File("C:\\Users\\Darien Miller\\Desktop\\ground.png"));
		groundY = HEIGHT - ground.getHeight(this);
		groundImageWidth = ground.getWidth(this);
		bird = new Bird(WIDTH / 2, HEIGHT / 2,  HEIGHT - ground.getHeight(this), -20);

		//timer determines how often the actionPerformed method is called. In this case, it is called every 5 milliseconds.
		t = new Timer(5, this);
		FPS = new FrameRate();
		pipes = new LinkedList<Pipe>();
		pipes.add(new Pipe(WIDTH + WIDTH / 2, HEIGHT, ground.getHeight(this), new File("C:\\Users\\Darien Miller\\Desktop\\topPipe.png"), 
				new File("C:\\Users\\Darien Miller\\Desktop\\bottomPipe.png")));
		
		bird.addToSpriteList(new File("C:\\Users\\Darien Miller\\Desktop\\smallBirdUp_75.png"));
		bird.addToSpriteList(new File("C:\\Users\\Darien Miller\\Desktop\\smallBirdNeutral_75.png"));
		bird.addToSpriteList(new File("C:\\Users\\Darien Miller\\Desktop\\smallBirdDown_75.png"));
		
		frame.add(this);
		frame.addKeyListener(this);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//These boolean assist in determining when to start, stop and restart the game. Initially, they will be false since the game hasn't
		//started yet, and the player didn't kill the bird yet.
		isGameOver = false;
		gameIsStarted = false;
		
		t.start();
	}
	
	public static void main(String args[]) throws IOException {
		new FlappyBird();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(sky, 0, 0, this);
		printPipes(g);
		bird.animateBird(g, isGameOver);
		g.drawImage(ground, groundX, groundY, this);
		g.drawImage(ground, groundX + groundImageWidth, groundY, this);
		printScore(g);
		endGame(g);
		FPS.limitFPSTo60();
		FPS.printFPS(g, WIDTH - 100, HEIGHT - 100);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		//The method call is called outside of the if statement because even after getting a gameover, we want the bird to fall down to
		//the ground. Everything else in the if statement is not called, and thus not updated when the game is over, causing everything to stop moving!
		
		
		//if the player did not start the game yet, simply move the ground and nothing else
		if(!gameIsStarted) {
			moveGround();
		}
		//otherwise, let the bird start falling once the player has started the game
		else
			bird.fall();
		
		//animate the pipes, check to see if the bird collided with either the ground or the pipe as long as the game is running
		if(!isGameOver && gameIsStarted) {
			moveGround();
			isGameOver = hitGround();
			try {
				addPipe();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			movePipes();
			frames++;
		}
		
		//System.out.println(bird.getVelocity());
		FPS.calculateFPS(0);
		repaint();
		
	}
	
	/**
	 * Method to draw the game over mesage to the screen.
	 * @param g
	 */
	public void endGame(Graphics g) {
		if(isGameOver)
			g.drawImage(gameOver, WIDTH / 4, 100, this);
	}
	
	public void printPipes(Graphics g) {
		for(Pipe p : pipes)
			p.printPipe(g);
	}
	
	/**
	 * checks to see if the bird hit the ground, which counts as a game over.
	 */
	public boolean hitGround() {
		if(bird.getY() + bird.getBirdHeight() >= groundY)
			return true;
		return false;
	}
	
	/**
	 * This method will produce movement in the pipes, and check to see whether or not a bird has hit them, which will cause a game over, or if
	 * a bird has passed in between, earning the player a point.
	 */
	public void movePipes() {
		for(int i = 0; i < pipes.size() && !isGameOver; i++) {
			pipes.get(i).pipeMeetsBird(bird);
			pipes.get(i).movePipe();
			isGameOver = pipes.get(i).pipeHitsBird(bird);
		}	
		
	}
	
	public void printScore(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 40));
		g.drawString(bird.getScore().toString(), WIDTH / 2, 50);
	}

	public void moveGround() {
		groundX -= groundSpeed;
		
		if(groundX <= -groundImageWidth ) 
			groundX = 0;
	}
	
	public void addPipe() throws IOException {
		
		//every 100 frames, add a new pipe to the linkedlist of pipes. The more frames, the bigger the gap between each pipe object in the list. The less frames,
		//the smaller the gap.
		if(frames == 80) {
			pipes.add(new Pipe(WIDTH + WIDTH / 2, HEIGHT, ground.getHeight(this), new File("C:\\Users\\Darien Miller\\Desktop\\topPipe.png"), 
					new File("C:\\Users\\Darien Miller\\Desktop\\bottomPipe.png")));
			frames = 0;
		}
		
		//if a pipe is off of the screen, remove it from the list as it will no longer be needed. We are using a linkedlist instead of an arraylist due to 
		//an element from the front in a linkedlist taking O(1) time instead of O(n) time in an arraylist.  
		if(pipes.get(0).getX() <= -pipes.get(0).getWidth())
			pipes.remove(0);
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		//if the player presses the space bar while the game is running, make the bird flap
		if(key.getKeyCode() == KeyEvent.VK_SPACE && !isGameOver && gameIsStarted)
			bird.flap();
		
		//pressing the space bar here will start the game by setting the gameIsStarted boolean to true  
		else if(key.getKeyCode() == KeyEvent.VK_SPACE && !gameIsStarted) {
			gameIsStarted = true;
			bird.flap();
		}

		//pressing the space bar here will restart the game by resetting all of the values to their initial states 
		else if(key.getKeyCode() == KeyEvent.VK_SPACE && isGameOver && hitGround()) {
			bird.reset();
			bird.flap();
			pipes.clear();
			gameIsStarted = false;
			if(bird.getScore() > highScore)
				highScore = bird.getScore();
			bird.setScore(0);
			isGameOver = false;
			try {
				pipes.add(new Pipe(WIDTH + WIDTH / 2, HEIGHT, ground.getHeight(this), new File("C:\\Users\\Darien Miller\\Desktop\\topPipe.png"), 
						new File("C:\\Users\\Darien Miller\\Desktop\\bottomPipe.png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frames  = 0;
		}
			
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	
}
