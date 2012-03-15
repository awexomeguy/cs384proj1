import java.util.Date;

public class Message
{
	public int m_id;
	public boolean m_type; // request = FALSE ... ack = TRUE
	public Date m_timestamp;
	
	Message(int ID, boolean Type, Date Timestamp)
	{
		m_id = ID;
		m_type = Type;
		m_timestamp = Timestamp;
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
	
	// used to determine type of message
	public static final boolean ACK = true;
	public static final boolean REQUEST = false;
}