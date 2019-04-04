import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author Allysia Freeman
 * @class COP4600-16 Operating Systems
 * @input processes.in
 * @output processes.out
 */

public class ProcessScheduler {
	
	//set the basic info to defaults, changable later
	static String inputFilename = "processes.in";
	static String outputFilename = "processes.out";
	
	//Set the process scheduler defaults
	static String algorithmType = "unknown";
	static int runtime = 0;
	static int quantum = -1;
	static int selectedIndex = 0; //selected process
    
    static boolean idle = true; //keep track of idling

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		File into = new File (inputFilename);
		Scanner sc = new Scanner(into);
		
		int numProcess = 0; //number of processes
		
		for(int i=0; i<4; i++) //determines basic information from file
		{
			String line = sc.nextLine();
			
			if(i != 2)
			{
				if(!algorithmType.equals("Round-Robin") && i == 3){} //if not round robin, ignore the third line
				else
				{
					String str = cleanString(line).get(1);
					int num = Integer.parseInt(str); //ignore all after #, then split by spaces
					if(i == 0)
						numProcess = num;
					else if (i == 1)
						runtime = num;
					else if (i == 3)
						quantum = num;
				}
			}
			else //determines type
			{
				switch (cleanString(line).get(1))
				{
					case "fcfs":
						algorithmType = "First-Come First-Served";
						break;
					case "sjf":
						algorithmType = "Preemptive Shortest Job First";
						break;
					case "rr":
						algorithmType = "Round-Robin";
						break;
					default:
						algorithmType = "unknown";
				}
			}
		}
		
		//create the array of processes
		Process[] processes = new Process[numProcess+1]; //the first process is a dummy
		processes[0] = new Process();
		processes[0].set(cleanString("process name dummy arrival 0 burst 99"));
		
		for(int i=1; i<numProcess+1; i++)
		{
			processes[i] = new Process();
			processes[i].set(cleanString(sc.nextLine()));
		}
	    sc.close();
	    
	    PrintWriter writer = new PrintWriter(outputFilename, "UTF-8");
	    
	    //Print the basic info
	    writer.println(numProcess+" processes");
	    writer.println("Using "+algorithmType);
	    if(algorithmType.equals("Round-Robin"))
	    	writer.println("Quantum "+quantum);
	    writer.println();
	    
	    int timer = -1;
	    if(algorithmType.equals("Round-Robin"))
	    	timer = quantum;
	    
	    //start the process		
	    for(int time=0; time<runtime; time++)
	    {
	    	boolean arrivals = checkArrivals(writer, processes, time); //check for any arrivals, set them to ready, print them
	    	
	    	if(((algorithmType.equals("Round-Robin") && (timer == 0 || ((!processes[selectedIndex].readyState) && arrivals)))) || //if roundRobin is up
	    	    (algorithmType.equals("First-Come First-Served") && !processes[selectedIndex].readyState) || //if the current process is finished
	    	    (algorithmType.equals("Preemptive Shortest Job First") && (arrivals || !processes[selectedIndex].readyState))) //if any new arrivals or the current job is up
	    			idle = true;
	    	//If idle you need to run the scheduler
	    	if(idle)
	    		selectedIndex = scheduler(writer, processes, time);
	    	
	    	//if you're still idle, print the message.
	    	if(idle)
	    		writer.println("Time "+time+": IDLE");
	    	else //else run the selected process
	    		processes[selectedIndex].run();
	    	
	    	//if Round-Robin, tick tock the clock
	    	if(timer == 0)
	    		timer = quantum;
	    	if(quantum != -1)
	    		timer--;
	    	
	    	//check to see if the process finished
	    	if(processes[selectedIndex].burstLeft == 0)
	    	{
	    		processes[selectedIndex].finish(time+1); //it will finish next tick, but it's easier to check here.
				writer.println("Time "+(time+1)+": "+processes[selectedIndex].name+" finished");
				
				if(algorithmType.equals("Round-Robin")) //if round robin force a new process next run
					timer = 0;
	    	}
	    }
	    
	    if(checkSuccess(processes)) //if you're successful or not
	    	writer.println("Finished at time "+runtime);
	    else
	    	writer.println("Failed to complete all processes at time "+runtime);
	    
	    printProcSpecs(writer, processes); //print the process specs
	    writer.close();

	}
	
	private static int scheduler(PrintWriter writer, Process[] processes, int time) {
		int selectIndex = 0; //set to dummy
		if(algorithmType.equals("Preemptive Shortest Job First"))
		{
			//find the smallest, ready process and select that.
			int min = Integer.MAX_VALUE;
			for(int i=1; i<processes.length; i++)
			{
				if(processes[i].readyState && processes[i].burstLeft < min) //find the minimum burst process
				{
					min = processes[i].burstLeft;
					selectIndex = i;
					idle = false;
				}
			}
			if(selectIndex != 0 && (selectIndex != selectedIndex || selectedIndex == 0)) //if it found one, and it's new
				writer.println("Time "+time+": "+processes[selectIndex].name+" selected (burst "+processes[selectIndex].burstLeft+")");
		}
		else //First come first served or Round Robin, both use basically the same search sequence
		{
			//Find the process that arrived the soonest from time zero and is not finished
			int iQuantum = 0;
			for(int i=0; i<runtime; i++)
			{
				for(int j=1; j<processes.length; j++)
				{
					if(algorithmType.equals("Round-Robin"))
						iQuantum = (1+i+processes[selectedIndex].arrival)%runtime;
					else
						iQuantum = i;
					
					if(processes[j].arrival == iQuantum && processes[j].readyState)
					{
						selectIndex = j;
						idle = false;
						if(selectIndex != selectedIndex || selectedIndex == 0 || algorithmType.equals("Round-Robin")) //if it found one, and it's new
							writer.println("Time "+time+": "+processes[selectIndex].name+" selected (burst "+processes[selectIndex].burstLeft+")");
						i = runtime;
						break;
					}
				}
			}
		}
		return selectIndex;
	}

	//Checks for new arrivals at time time, prints accordingly
	private static boolean checkArrivals(PrintWriter writer, Process[] processes, int time) {
		boolean arrival = false;
		for(int i=1; i<processes.length; i++) 
		{
			if(processes[i].arrival == time) 
			{
				writer.println("Time "+time+": "+processes[i].name+" arrived");
				processes[i].readyState = true;
				arrival = true;
			}
		}
		return arrival;
	}
	
	//Print the process results at the end of the program
	private static void printProcSpecs(PrintWriter writer, Process[] processes) {
		writer.println();
		for(int i=1; i<processes.length; i++)
		{
			if(processes[i].turnaround == 0)
				writer.println(processes[i].name+" wait "+processes[i].wait+" turnaround N/A");
			else
				writer.println(processes[i].name+" wait "+processes[i].wait+" turnaround "+processes[i].turnaround);
		}
		
	}
	
	//Checks if successful finish
	private static boolean checkSuccess(Process[]processes)
	{
		for(int i=1; i<processes.length; i++)
			{
				if(processes[i].burstLeft > 0)
					return false;
			}
		return true;
	}
	
	private static ArrayList<String> cleanString(String str)
	{
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(((((str.trim()).split("#"))[0].trim()).split(" "))));
		list.removeAll(Collections.singleton(null));
		list.removeAll(Collections.singleton(""));
		return list;
	}

}
