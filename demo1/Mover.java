import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

/** this class emulates the people and their decision-making process.
 The mutual exclusion code is included in this class. */
public class Mover implements Runnable
{
	protected JFrame f;
	protected JSlider s;
	protected int x, y; /** coordinates of the Mover */
	protected int state; /** state of the Mover (in CS, waiting for CS, or neither) */
	protected int direction; /** current direction of Mover */
	protected int ID;
	protected LinkedBlockingQueue<Message> [] q; /** acts as the message channels for all movers */
	protected boolean [] pending = new boolean[MainDemo.NUM_OF_MOVERS]; /** to keep track of all pending requests */
	protected boolean [] acks = new boolean[MainDemo.NUM_OF_MOVERS]; /** to keep track of received acks */
	protected Date reqStamp; /** so we know when we sent a request */
	
	public Mover(JFrame frame, JSlider slide, LinkedBlockingQueue [] Q)
	{
		f = frame;
		s = slide;
		q = Q;
		state = NEITHER;
	}
	
	/** clears all records of pending requests we received */
	private void clearPending()
	{
		for(int i = 0; i < MainDemo.NUM_OF_MOVERS; i++)
		{
			pending[i] = false;
		}
	}
	
	/** clears all records of acks we received */
	private void clearAcks()
	{
		for(int i = 0; i < MainDemo.NUM_OF_MOVERS; i++)
		{
			acks[i] = false;
		}
		acks[ID] = true;
	}
	
	/** this method takes a Message as input and the mover
	and takes the appropriate action according to the content of the message */
	private synchronized void parseMessage(Message m)
	{
		if(m.isRequest()) { 
			switch(state)
			{
				case IN_CS: // queue the request
					pending[m.sender()] = true;
					break;
				case WAITING: // only send ack if timestamp is before our own, else queue it
					if(m.getTimestamp().before(reqStamp))
						sendAckTo(m.sender());
					else
						pending[m.sender()] = true;
					break;
				case NEITHER: // send ack automatically
					sendAckTo(m.sender());
					break;
			}

		} else if(m.isAck()) {
			acks[m.sender()] = true; // record the ack we receive
		}
	}
	
	/** this method puts a request into the queue of all other movers */
	private synchronized void sendRequestToAll()
	{
		reqStamp = new Date();
	
		for(int i = 0; i < MainDemo.NUM_OF_MOVERS; ++i)
			if(i != ID) // don't put into our own queue, dummy
			{
				try
				{
					q[i].put(new Message(ID, Message.REQUEST, reqStamp)); // puts a request in all others' queue
				} catch(InterruptedException ex){};
			}
	}
	
	/** this method puts an ack message into the queue of the specified mover */
	private synchronized void sendAckTo(int receiver)
	{
		try
		{
			q[receiver].put(new Message(ID, Message.ACK, null)); // timestamp is null because we don't care about timestamps on acks
		} catch(InterruptedException ex){};
	}
	
	/** this method tells the mover whether it has received all necessary acks or not.
	It returns true if the mover has all needed acks (can enter CS), otherwise false */
	private boolean checkAcks()
	{
		for(int i = 0; i < MainDemo.NUM_OF_MOVERS; i++)
		{
			// Return false if any acks in the array are still missing
			if(i != ID) // Don't check self
			{
				if(acks[i] == false)
				{
					return false;
				}
			}
		}
		
		// We made it through the loop, all acks were received.
		return true;
	}
	
	/** this method puts an ack message into the queue of all movers
	recorded in the pending array */
	private void sendAcksToPending()
	{
		for(int i = 0; i < MainDemo.NUM_OF_MOVERS;i++)
		{
			if(pending[i] == true)
			{
				sendAckTo(i);
			}
		}
	}
	
	/** this method is the actual simulation.
	The mover will change its position, direction, and state according to messages it receives */
	public void run()
	{  
		while(true)
		{
			try
			{
				Thread.sleep(100);
			} catch(InterruptedException ex){};
			
			// Check for messages.
			while(!q[ID].isEmpty())
			{
				try 
				{
					parseMessage(q[ID].poll(100, TimeUnit.MILLISECONDS));
				} catch(InterruptedException ex){};
			}

			if(direction == RIGHT) // we are currently moving right
			{
				if(x < MainDemo.BRIDGE_LEFT) // not yet to the bridge
				{
					setPosition(x + s.getValue(), y + s.getValue());
					if(x >= MainDemo.BRIDGE_LEFT) // if we reach the bridge
					{
						setPosition(MainDemo.BRIDGE_LEFT, MainDemo.BRIDGE_Y);
						state = WAITING;
						
						// Send request to all
						sendRequestToAll();
					}
				}
				else if(x >= MainDemo.BRIDGE_LEFT && x < MainDemo.BRIDGE_RIGHT) // we are on the bridge
				{
					if(state == WAITING)
					{
						// Go to critical section if we received all ACKs.
						//setAcks(); // REMOVE THIS ***
						if(checkAcks() == true)
						{
							state = IN_CS;
							clearAcks();
						}
					}	
					else if(state == IN_CS)
					{
						setPosition(x + s.getValue(), MainDemo.BRIDGE_Y);
						if(x >= MainDemo.BRIDGE_RIGHT) // if we reach the end of the bridge
						{
							setPosition(MainDemo.BRIDGE_RIGHT, MainDemo.BRIDGE_Y);
							state = NEITHER;
							
							// Send ack to each on pending list
							sendAcksToPending();
							clearPending();
						}
					}
				}
				else if(x >= MainDemo.BRIDGE_RIGHT) // we are past the bridge
				{
					setPosition(x + s.getValue(), y - s.getValue());
					if(x >= MainDemo.MAX_X || y <= MainDemo.MIN_Y) // we reach the right edge, start going down
					{
						x = MainDemo.MAX_X;
						y = MainDemo.MIN_Y;
						direction = DOWN;
					}
				}
			}
			else if(direction == LEFT) // we are currently moving left
			{
				if(x > MainDemo.BRIDGE_RIGHT) // not yet to the bridge
				{
					setPosition(x - s.getValue(), y - s.getValue());
					if(x <= MainDemo.BRIDGE_RIGHT) // if we reach the bridge
					{
						setPosition(MainDemo.BRIDGE_RIGHT, MainDemo.BRIDGE_Y);
						state = WAITING;
						
						// SEND REQUEST TO ALL
						sendRequestToAll();
					}
				}
				else if(x > MainDemo.BRIDGE_LEFT && x <= MainDemo.BRIDGE_RIGHT) // we are on the bridge
				{
					if(state == WAITING)
					{
						// Go to critical section if we received all ACKs.
						//setAcks(); // Remove this ***
						if(checkAcks() == true)
						{
							state = IN_CS;
							clearAcks();
						}
					}
					else if(state == IN_CS)
					{
						setPosition(x - s.getValue(), MainDemo.BRIDGE_Y);
						if(x <= MainDemo.BRIDGE_LEFT) // if we reach the end of the bridge
						{
							setPosition(MainDemo.BRIDGE_LEFT, MainDemo.BRIDGE_Y);
							state = NEITHER;
							
							// SEND ACK TO EACH ON PENDING
							sendAcksToPending();
							clearPending();
						}
					}
				}
				else if(x <= MainDemo.BRIDGE_LEFT) // we are past the bridge
				{
					setPosition(x - s.getValue(), y + s.getValue());
					if(x <= MainDemo.MIN_X || y >= MainDemo.MAX_Y) // we reach the left edge, start going up
					{
						x = MainDemo.MIN_X;
						y = MainDemo.MAX_Y;
						direction = UP;
					}
				}
			}
			else if(direction == UP) // we are currently moving up
			{
				setPosition(x, y - s.getValue());
				if(y < MainDemo.MIN_Y) // if we get to the top of the loop, turn right
				{
					y = MainDemo.MIN_Y;
					direction = RIGHT;
				}
			}
			else if(direction == DOWN)
			{
				setPosition(x, y + s.getValue());
				if(y > MainDemo.MAX_Y) // if we get to the bottom of the loop, turn left
				{
					y = MainDemo.MAX_Y;
					direction = LEFT;
				}
			}
			
			//repaint the frame to reflect the position change
			f.repaint();
		}
	}
	
	/** Set x and y coordinates of Mover */
	public void setPosition(int a, int b)
	{ 
		x = a;
		y = b;
	}

	public void setID(int id)
	{
		ID = id;
	}
	
	public int getID()
	{
		return ID;
	}
	
	/** get x coordinate */
	public int getX() 
	{ 
		return x; 
	}  

	/** get y coordinate */
	public int getY() 
	{ 
		return y; 
	}
	
	public int getState()
	{
		return state;
	}
	
	public void setState(int n)
	{
		if(n >= 0 && n <= 2)
			state = n;
		else
			return;
	}
	
	public int getDirection()
	{
		return direction;
	}
	
	public void setDirection(int n)
	{
		if(n >= 0 && n <= 3)
			direction = n;
		else
			return;
	}

	/** convert the position of the Mover into a String representation */
	public String toString() 
	{ 
		return "[" + x + ", " + y + "]";
	}
	
	// these represent the current direction the Mover is moving
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;
	
	// these represent the states of the Mover regarding the CS
	public static final int WAITING = 0;
	public static final int IN_CS = 1;
	public static final int NEITHER = 2;
}