import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDemo extends JApplet
{
	private Mover [] movers;
	private JSlider [] sliders;
	private Queue [] threadq = new Queue[NUM_OF_MOVERS];
        Graphics bufferGraphics;
	Image offscreen;
        
	Container c = getContentPane();
	
	public void init()
	{
		c.setLayout(new FlowLayout());
		
		// set up sliders to control speeds
		sliders = new JSlider[NUM_OF_MOVERS];
		for(int i = 0; i < NUM_OF_MOVERS; ++i)
		{
			JPanel panel = new JPanel();
			JLabel label = new JLabel();
			
			switch(i)
			{
				case 0:
					label.setText("red");
					break;
				case 1:
					label.setText("blue");
					break;
				case 2:
					label.setText("green");
					break;
				case 3:
					label.setText("orange");
					break;
			}
			
			sliders[i] = new JSlider(0, 10, 0);
			sliders[i].setSnapToTicks(true);
			panel.add(label);
			panel.add(sliders[i]);
			c.add(panel);
		}
                
                offscreen = createImage(MAX_X+15,MAX_Y+15);
                bufferGraphics = offscreen.getGraphics();

		// set the movers' initial positions
		movers = new Mover[NUM_OF_MOVERS];
		movers[0] = new Mover(this, sliders[0],threadq);
		movers[0].setPosition(MIN_X, 300);
		movers[0].setDirection(Mover.UP);
		movers[0].setID(0);
		
		movers[1] = new Mover(this, sliders[1],threadq);
		movers[1].setPosition(MIN_X, 350);
		movers[1].setDirection(Mover.UP);
		movers[1].setID(1);
		
		movers[2] = new Mover(this, sliders[2],threadq);
		movers[2].setPosition(MAX_X, 300);
		movers[2].setDirection(Mover.DOWN);
		movers[2].setID(2);
		
		movers[3] = new Mover(this, sliders[3],threadq);
		movers[3].setPosition(MAX_X, 350);
		movers[3].setDirection(Mover.DOWN);
		movers[3].setID(3);
		
		// start all the movers
		ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_MOVERS);
		for(int i = 0; i < NUM_OF_MOVERS; ++i)
			executor.execute(movers[i]);
	}
        
	public void update(Graphics g)
        {
            super.paint(g);
        }
        
	public void paint(Graphics g)
	{
            /*
		super.paint(g);
		
		// draw the path for the movers
		g.drawLine(MIN_X, MIN_Y, BRIDGE_LEFT, BRIDGE_Y);
		g.drawLine(BRIDGE_LEFT, BRIDGE_Y, BRIDGE_RIGHT, BRIDGE_Y);
		g.drawLine(BRIDGE_RIGHT, BRIDGE_Y, MAX_X, MIN_Y);
		g.drawLine(MAX_X, MIN_Y, MAX_X, MAX_Y);
		g.drawLine(MAX_X, MAX_Y, BRIDGE_RIGHT, BRIDGE_Y);
		g.drawLine(BRIDGE_RIGHT, BRIDGE_Y, BRIDGE_LEFT, BRIDGE_Y);
		g.drawLine(BRIDGE_LEFT, BRIDGE_Y, MIN_X, MAX_Y);
		g.drawLine(MIN_X, MAX_Y, MIN_X, MIN_Y);
		
		//g.setColor(Color.RED);
		// show the position of the movers
		for(int i = 0; i < NUM_OF_MOVERS; ++i)
		{
			switch(i)
			{
				case 0:
					g.setColor(Color.RED);
					break;
				case 1:
					g.setColor(Color.BLUE);
					break;
				case 2:
					g.setColor(Color.GREEN);
					break;
				case 3:
					g.setColor(Color.ORANGE);
					break;
			}

			g.fillOval(movers[i].getX() - 15, movers[i].getY() - 15, 30, 30);
			g.drawString(movers[i].toString(), 10, 10 + i * 15);
		} */
              
            //------------//
            super.paint(g);
		
		// draw the path for the movers
                bufferGraphics.clearRect(0,0,(MAX_X+15),(MAX_Y+15));
                
		bufferGraphics.drawLine(MIN_X, MIN_Y, BRIDGE_LEFT, BRIDGE_Y);
		bufferGraphics.drawLine(BRIDGE_LEFT, BRIDGE_Y, BRIDGE_RIGHT, BRIDGE_Y);
		bufferGraphics.drawLine(BRIDGE_RIGHT, BRIDGE_Y, MAX_X, MIN_Y);
		bufferGraphics.drawLine(MAX_X, MIN_Y, MAX_X, MAX_Y);
		bufferGraphics.drawLine(MAX_X, MAX_Y, BRIDGE_RIGHT, BRIDGE_Y);
		bufferGraphics.drawLine(BRIDGE_RIGHT, BRIDGE_Y, BRIDGE_LEFT, BRIDGE_Y);
		bufferGraphics.drawLine(BRIDGE_LEFT, BRIDGE_Y, MIN_X, MAX_Y);
		bufferGraphics.drawLine(MIN_X, MAX_Y, MIN_X, MIN_Y);
		
		//g.setColor(Color.RED);
		// show the position of the movers
		for(int i = 0; i < NUM_OF_MOVERS; ++i)
		{
			switch(i)
			{
				case 0:
					bufferGraphics.setColor(Color.RED);
					break;
				case 1:
					bufferGraphics.setColor(Color.BLUE);
					break;
				case 2:
					bufferGraphics.setColor(Color.GREEN);
					break;
				case 3:
					bufferGraphics.setColor(Color.ORANGE);
					break;
			}

			bufferGraphics.fillOval(movers[i].getX() - 15, movers[i].getY() - 15, 30, 30);
			bufferGraphics.drawString(movers[i].toString(), 10, 10 + i * 15);
                        g.drawImage(offscreen,0,50,this);
		}
	}
        
        
	// the following parameters are used to define the path the Movers take
	// the bridge is the critical section in this scenario
	public static final int BRIDGE_LEFT = 300;
	public static final int BRIDGE_RIGHT = 600;
	public static final int BRIDGE_Y = 300;
	public static final int MAX_Y = 500;
	public static final int MIN_Y = 100;
	public static final int MAX_X = 800;
	public static final int MIN_X = 100;
	
	public static final int NUM_OF_MOVERS = 4; // number of movers on the road
}