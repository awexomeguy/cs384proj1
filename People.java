import java.util.concurrent.Exchanger;

public class People extends Thread
{
	private String m_name;
	private Exchanger Line = null;
	public People(){}
	public People(String name, Exchanger<String> ex)
	{
		m_name = name;
		Line = ex;
	}
	public void Greet(){System.out.println("hello, my name is "+m_name+".\n");}
	public void Greet(String name) {System.out.println("hello "+name+"!\n I'm "+m_name);}
	public void Die() 
	{
		System.out.println("Argh!");
	}
	public String Name(){return m_name;}
	public void run()
	{
		String Name = Name();
		for(int i = 0; i < 3; i++)
		{
			try {
				Name = (String) Line.exchange(Name);
				Greet(Name);
				} catch (InterruptedException exc) {
					System.out.println(exc);
				}
			try
			{
				Thread.sleep(10000);
			}
			catch(InterruptedException e){}
		}
		Die();
	}
}