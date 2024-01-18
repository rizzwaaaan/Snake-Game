import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener 
{
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 15;
	static final int GAME_UNITS = (SCREEN_HEIGHT*SCREEN_WIDTH)/UNIT_SIZE;
	static int DELAY = 50;
	static int INITIAL_DELAY = DELAY;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 1;
	int applesEaten = 0;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	boolean playAgain = false;
	JButton playAgainButton;
	public GamePanel() 
	{
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
    public void startGame() 
    {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        
        playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                resetGame();
            }
        });
        this.add(playAgainButton);
        playAgainButton.setVisible(false);
    }
    private void resetGame() 
    {
        playAgainButton.setVisible(false); // Hide the button
        applesEaten = 0;
        bodyParts = 1;
        direction = 'R';
        running = true;
        DELAY = INITIAL_DELAY;
        timer.setDelay(DELAY);
        newApple();
        for (int i = 0; i < bodyParts; i++) 
        {
            x[i] = 0;
            y[i] = 0;
        }
        timer.start();
        repaint();
        this.setFocusable(true);
        this.requestFocusInWindow();
        
    }
    public void paintComponent(Graphics g) 
    {
    	super.paintComponent(g);
    	draw(g);
    }
    public void draw(Graphics g)
    {
        if(running) 
        {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        
            for(int i = 0; i< bodyParts;i++) 
            {
                if(i == 0) 
                {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else 
                {
                    g.setColor(new Color(45,180,0));
                    //g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }           
            }
        }
        else 
        {
            gameOver(g);
            
        }
    }
    
    public void newApple()
    {
    	appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
    	appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }
    public void move()
    {
        for(int i = bodyParts;i>0;i--) 
        {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        switch(direction) 
        {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
        
    }
    public void checkApple()
    {
        if((x[0] == appleX) && (y[0] == appleY)) 
        {
            bodyParts++;
            applesEaten++;

            // Check if the number of apples eaten is a multiple of 5
            if (applesEaten % 5 == 0) {
                reduceDelay(); // Reduce the delay
            }

            newApple();
        }    	
    }
    public void reduceDelay() 
    {
        if (DELAY > 10) 
        { // Make sure the delay doesn't go below a certain threshold
            DELAY -= 5; // Reduce the delay by 5
            timer.setDelay(DELAY); // Update the Timer's delay
        }
    }
    public void checkCollisions() 
    {
      //checks if head collides with body
        for(int i = bodyParts;i>0;i--) 
        {
            if((x[0] == x[i])&& (y[0] == y[i])) 
            {
                running = false;
            }
        }
        //check if head touches left border
        if(x[0] < 0) 
        {
            running = false;
        }
        //check if head touches right border
        if(x[0] > SCREEN_WIDTH) 
        {
            running = false;
        }
        //check if head touches top border
        if(y[0] < 0) 
        {
            running = false;
        }
        //check if head touches bottom border
        if(y[0] > SCREEN_HEIGHT) 
        {
            running = false;
        }
        
        if(!running) 
        {
            timer.stop();
        }
	}
    public void gameOver(Graphics g)
    {
        //Score
        g.setColor(Color.red);
        g.setFont( new Font("Dubai Medium",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Dubai Medium",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
        playAgainButton.setBounds(SCREEN_WIDTH / 2 - 80, SCREEN_HEIGHT / 2 + 50, 160, 40);
        playAgainButton.setVisible(true);
        this.setFocusable(false);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(running)
		{
		    move();
		    checkApple();
		    checkCollisions();
		}
		repaint();
		
	}
	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
		    if(!playAgain)
		    {
		         switch(e.getKeyCode()) 
		         {
                     case KeyEvent.VK_LEFT:
                        if(direction != 'R') 
                        {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(direction != 'L') 
                        {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                       if(direction != 'D') 
                       {
                            direction = 'U';
                       }
                       break;
                    case KeyEvent.VK_DOWN:
                        if(direction != 'U') 
                        {
                            direction = 'D';
                        }
                        break;
                 }
			
		    }		
	    }	
    }
}
