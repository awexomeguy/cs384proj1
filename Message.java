import java.util.Date;

public class Message
{
	Message(int ID, boolean Type, Date Timestamp)
	{
		m_id = ID;
		m_type = Type;
		m_timestamp = Timestamp;
	}
	public int m_id;
	public boolean m_type;
	public Date m_timestamp;
}