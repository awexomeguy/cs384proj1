import java.util.concurrent.Exchanger;

public class Demo
{
	public static void main(String[] args)
	{
		Exchanger e = new Exchanger();
		Thread Mike = new People("Mike",e), Joel = new People("Joel",e);
		Mike.start();
		Joel.start();
	}
}