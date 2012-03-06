import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Mover implements Runnable
{
	protected JApplet a;
	protected JSlider s;
	protected int x, y; // coordinates of the Mover
	
	public Mover(JApplet app, JSlider slide)
	{
		a = app;
		s = slide;
	}
	
	public void run()
	{
		for(int i = 0; i < 60; ++i)
		{
			try
			{
				Thread.sleep(100);
			} catch(InterruptedException ex){};
			
			setPosition(x + s.getValue(), y);
			a.repaint();
		}
	}
	
	// Set x and y coordinates of Mover
	public void setPosition(int a, int b)
	{ 
		x = a;
		y = b;
	}

	// get x coordinate
	public int getX() 
	{ 
		return x; 
	}  

	// get y coordinate
	public int getY() 
	{ 
		return y; 
	}

	// convert the position of the Mover into a String representation
	public String toString() 
	{ 
		return "[" + x + ", " + y + "]"; 
	}
}