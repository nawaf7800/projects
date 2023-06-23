import java.util.Scanner;

public class SJF {
	
	public static int[][] calc_schedule(int[] process_AT, int[] process_BT) {
		
		int[] AT = process_AT.clone();
		int[] BT = process_BT.clone();
		
		int[][] p = new int[process_AT.length][3];
		
		int index_min = 0;
		for(int i = 0; i < AT.length; i++) {
			if(AT[i] < AT[index_min]) index_min = i;
			else if (AT[i] == AT[index_min]) 
			{
				if(BT[i] < BT[index_min]) index_min = i;
			}
		}
		p[0][0] = index_min;
		p[0][1] = AT[index_min];
		p[0][2] = AT[index_min] + BT[index_min];
		AT[index_min] = Integer.MAX_VALUE;
		BT[index_min] = Integer.MAX_VALUE;
		
		
		
		for(int j = 1; j < AT.length; j++) {
			
			index_min = 0;
			boolean a = false;
			for(int i = 0; i < AT.length; i++) {
				if(p[j-1][2] >= AT[i]) {
					if(BT[i] < BT[index_min]) {
						index_min = i;
					}
					if(BT[i] <= BT[index_min]) a = true;
				}
			}
			if(!a) {
				
				for(int i = 0; i < AT.length; i++) {
					if(AT[i] < AT[index_min]) index_min = i;
					else if (AT[i] == AT[index_min]) 
					{
						if(BT[i] < BT[index_min]) index_min = i;
					}
				}
				
				p[j][0] = index_min;
				p[j][1] = AT[index_min];
				p[j][2] = AT[index_min] + process_BT[index_min];
				AT[index_min] = Integer.MAX_VALUE;
				BT[index_min] = Integer.MAX_VALUE;
			}
			else {
				p[j][0] = index_min;
				p[j][1] = p[j-1][2];
				p[j][2] = p[j-1][2] + process_BT[index_min];
				AT[index_min] = Integer.MAX_VALUE;
				BT[index_min] = Integer.MAX_VALUE;
			}
		}
		return p;
	}

	private static void printSchedule(int[][] p) {
		
		String t = "";
		int current = 0;
		int num_of_char = 0;
		for(int i = 0; i < p.length; i++) {
			
			if(current != p[i][1]) {
				for(int j = 0; j < p[i][1] - current; j++)
					t += " ";
				t += "|";
				current += p[i][1] - current;
				num_of_char++;
			}
			
			int len = p[i][2] - p[i][1];
			for(int j = 0; j < len/2; j++)
				t += " ";
			t += "P" + (p[i][0]+1);
			for(int j = 0; j < len/2; j++)
				t += " ";
			t += "|";
			current += len;
			num_of_char = num_of_char + 3;
		}
		t += "\n";
		
		for(int j = 0; j < current+num_of_char-2; j++)
			System.out.print("-");
		System.out.print("\n" + t);
		for(int j = 0; j < current+num_of_char-2; j++)
			System.out.print("-");
		
	}
	
	public static void main(String[] args) {
		
		Scanner read = new Scanner(System.in);
		System.out.print("Number of processes in the system: ");
		int num_of_processes = read.nextInt();
		
		int[] process_AT = new int[num_of_processes];
		int[] process_BT = new int[num_of_processes];
		
		for(int i = 0; i < num_of_processes; i++) {
			System.out.print("P" + (i+1) + " arrival time is ");
			process_AT[i] = read.nextInt();
		}
		System.out.println();
		for(int i = 0; i < num_of_processes; i++) {
			System.out.print("P" + (i+1) + " burst time is ");
			process_BT[i] = read.nextInt();
		}
		
		int[][] p = calc_schedule(process_AT, process_BT);
		
		System.out.println();
		printSchedule(p);
		
		System.out.println();
		System.out.println();
		System.out.println("Process\tB.T\tA.T\tC.T\tT.A.T\tW.T");
		for(int i = 0; i < process_AT.length; i++) {
			
			System.out.println("P" + (p[i][0]+1) + "\t" + process_BT[p[i][0]] + "\t" + 
					process_AT[p[i][0]] + "\t" + p[i][2] + "\t" + (p[i][2]-process_AT[p[i][0]]) + "\t" + 
					(p[i][2]-process_AT[p[i][0]]-process_BT[p[i][0]]));
		}
		
		double AvgWT = 0;
		double AvgTAT = 0;
		for(int i = 0; i < p.length; i++) {
			AvgWT = AvgWT + p[i][2]-process_AT[p[i][0]]-process_BT[p[i][0]];
			AvgTAT = AvgTAT + p[i][2]-process_AT[p[i][0]];
		}
		AvgWT = AvgWT / p.length;
		AvgTAT = AvgTAT / p.length;
		
		System.out.print("\nAverage waiting time = " + AvgWT +
				"\nAverage turnaround time = " + AvgTAT);
		
		System.out.println();
	}
}