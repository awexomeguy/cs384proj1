import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainDemo extends JApplet implements ActionListener
{
	private Mover m;
	private JButton startButton, stopButton;
	private JSlider slider;
	
	Container c = getContentPane();
	
	public void init()
	{
		c.setLayout(new FlowLayout());
		
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		
		stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		
		slider = new JSlider(0, 10, 5);
		slider.setSnapToTicks(true);
		
		c.add(startButton);
		c.add(stopButton);
		c.add(slider);
		
		m = new Mover(this, slider);
		m.setPosition(275, 250);
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(m);
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		g.drawOval(m.getX(), m.getY(), 50, 50);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		
	}
	
	// the following parameters are used to define the path the Movers take
	// the bridge is the critical section in this scenario
	public int BRIDGE_LEFT_X;
	public int BRIDGE_RIGHT_X;
	public int BRIDGE_Y;
	public int MAX_Y;
	public int MIN_Y;
	public int MAX_X;
	public int MIN_X;
}