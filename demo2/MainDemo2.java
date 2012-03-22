import java.awt.*;
import javax.swing.*;
import java.util.concurrent.*;

/** This class simulates mutual exclusion that allows multiple movers
to cross the bridge if they are going in the same direction */
public class MainDemo2 extends JFrame 
{
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
    
    private Mover2 [] movers; /** the movers in the simulation */
    private JSlider [] sliders; /** used to control the speed of the movers */
    private LinkedBlockingQueue<Message2> [] threadq = new LinkedBlockingQueue[NUM_OF_MOVERS]; /** acts as the message channels for every mover */
    Graphics bufferGraphics;
    Image offscreen;

    Container c = getContentPane();

    public static void main(String [] args)
    {
        new MainDemo2();
    }

    public MainDemo2()
    {
        c.setLayout(new FlowLayout());

        // set up sliders to control speeds
        sliders = new JSlider[NUM_OF_MOVERS];
		for(int i = 0; i < NUM_OF_MOVERS; ++i)
		{
			threadq[i] = new LinkedBlockingQueue();
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
			c.add(label);
			c.add(sliders[i]);
		}

        setTitle("Mutual Exclusion");
        setSize(1300,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        offscreen = createImage(MAX_X+15,MAX_Y+15);
        bufferGraphics = offscreen.getGraphics();

        // set the movers' initial positions
        movers = new Mover2[NUM_OF_MOVERS];
        movers[0] = new Mover2(this, sliders[0], threadq);
        movers[0].setPosition(MIN_X, 300);
        movers[0].setDirection(Mover2.UP);
        movers[0].setID(0);

        movers[1] = new Mover2(this, sliders[1], threadq);
		movers[1].setPosition(MIN_X, 350);
        movers[1].setDirection(Mover2.UP);
        movers[1].setID(1);

        movers[2] = new Mover2(this, sliders[2], threadq);
        movers[2].setPosition(MAX_X, 300);
        movers[2].setDirection(Mover2.DOWN);
        movers[2].setID(2);

        movers[3] = new Mover2(this, sliders[3], threadq);
        movers[3].setPosition(MAX_X, 350);
        movers[3].setDirection(Mover2.DOWN);
        movers[3].setID(3);

        // start all the movers
        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_MOVERS);
        for(int i = 0; i < NUM_OF_MOVERS; ++i)
        { 
            executor.execute(movers[i]);
        }
    }

    public void update(Graphics g)
    {
		super.paint(g);
    }

    public void paint(Graphics g)
    {   
        //------------//
        super.paint(g);

		// draw the path for the movers
        bufferGraphics.clearRect(0,0,(MAX_X+15),(MAX_Y+15));

        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.drawLine(MIN_X, MIN_Y, BRIDGE_LEFT, BRIDGE_Y);
        bufferGraphics.drawLine(BRIDGE_LEFT, BRIDGE_Y, BRIDGE_RIGHT, BRIDGE_Y);
        bufferGraphics.drawLine(BRIDGE_RIGHT, BRIDGE_Y, MAX_X, MIN_Y);
        bufferGraphics.drawLine(MAX_X, MIN_Y, MAX_X, MAX_Y);
        bufferGraphics.drawLine(MAX_X, MAX_Y, BRIDGE_RIGHT, BRIDGE_Y);
        bufferGraphics.drawLine(BRIDGE_RIGHT, BRIDGE_Y, BRIDGE_LEFT, BRIDGE_Y);
        bufferGraphics.drawLine(BRIDGE_LEFT, BRIDGE_Y, MIN_X, MAX_Y);
        bufferGraphics.drawLine(MIN_X, MAX_Y, MIN_X, MIN_Y);

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
}