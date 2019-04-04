import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Allysia Freeman
 * @class COP4600-16 Operating Systems
 */

public class Process
{
	public String name = null;
	public int arrival = 0;
	public int burst = 0;
	public int burstLeft = 0;
	public int wait = 0;
	public int turnaround = 0;
	public boolean readyState = false;
	
	public void set(ArrayList<String> list) //parses input string from input file. will crash if not enough processes in file.
	{
		this.name = list.get(2);
		this.arrival = Integer.parseInt(list.get(4));
		this.burst = Integer.parseInt(list.get(6));
		this.burstLeft = this.burst;
	}
	
	public void run() //Not needed, but pseudo running the process
	{
		this.burstLeft = burstLeft - 1;
	}
	
	public void finish(int time)
	{
		this.turnaround = (time) - this.arrival;
		this.wait = this.turnaround - this.burst;
		this.readyState = false;
	}
}