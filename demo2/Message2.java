import java.util.Date;

/** This class contains all information that needs to be passed to different movers during simulation */
public class Message2
{
	private int m_id; /** ID of the mover who generated this message */
	private boolean m_type; /** Type of message. request = FALSE ... ack = TRUE */
	private Date m_timestamp; /** Time at which this message was generated */
	private int m_direction; /** Which direction the sender is moving */
	
	Message2(int senderID, boolean Type, Date Timestamp, int direction)
	{
		m_id = senderID;
		m_type = Type;
		m_timestamp = Timestamp;
		m_direction = direction;
	}
	
	public boolean isAck()
	{
		return (m_type == ACK);
	}
	
	public boolean isRequest()
	{
		return (m_type == REQUEST);
	}
	
	public Date getTimestamp()
	{
		return m_timestamp;
	}
	
	public int sender()
	{
		return m_id;
	}
	
	public int getDirection()
	{
		return m_direction;
	}
	
	// used to determine type of message
	public static final boolean ACK = true;
	public static final boolean REQUEST = false;
}