//Abdullah Babrouk - 1847209
//Nawaf Alshaikh - 1937422
//Marwan Babattat - 1943544

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class SLR_parser {

	public static void main(String[] args) throws FileNotFoundException {
		
		File file = new File("input.txt");
		
        if (!file.exists()) {
            System.out.println("Files not founds");
            System.exit(0);
        }
        
        Scanner read = new Scanner(file);
        
        int S4 = 1, S5 = 2, S6 = 3, S7 = 4, S11 = 5;
        int R1 = 6, R2 = 7, R3 = 8, R4 = 9, R5 = 10, R6 = 11;
        int acc = 12;
        
        int table[][] = {
        //		id	+	*	(	)	$	E	T	F
               {S5,	0, 	0, 	S4,	0, 	0, 	1, 	2, 	3},
               {0,	S6, 0, 	0, 	0, 	acc,0, 	0, 	0},
               {0,	R2, S7, 0, 	R2, R2, 0, 	0, 	0},
               {0,	R4, R4, 0, 	R4, R4, 0, 	0, 	0},
               {S5,	0, 	0, 	S4, 0, 	0, 	8, 	2, 	3},
               {0, 	R6, R6, 0, 	R6, R6, 0, 	0, 	0},
               {S5, 0, 	0, 	S4, 0, 	0, 	0, 	9, 	3},
               {S5, 0, 	0, 	S4, 0, 	0, 	0, 	0, 	10},
               {0, 	S6, 0, 	0, 	S11,0, 	0, 	0, 	0},
               {0, 	R1, S7, 0, 	R1, R1, 0, 	0, 	0},
               {0, 	R3, R3, 0, 	R3, R3, 0, 	0, 	0},
               {0, 	R5, R5, 0, 	R5, R5, 0, 	0, 	0}
            };
        
        while(read.hasNext()) {
        	
        	Stack<String> stack = new Stack<>();
        	stack.add("0");
        	
        	String str = read.nextLine();
            String input[] = str.split(" ");
            int ip = 0;
            
            System.out.println("Right most derivation for the arithmetic expression " + str + ":");
            
            boolean flag = true;
            while (flag) {
            	
            	int a = getToken(input[ip]);
                int s = Integer.parseInt(stack.peek());
                
            	switch (table[s][a]) {
            	case 1:
                    //shift (
                    System.out.println("Shift 4");
                    stack.add("(");
                    stack.add("4");
                    ip++;
                    break;
                case 2:
                    //shift id
                    System.out.println("Shift 5");
                    stack.add("id");
                    stack.add("5");
                    ip++;
                    break;
                case 3:
                    //shift +
                    System.out.println("Shift 6");
                    stack.add("+");
                    stack.add("6");
                    ip++;
                    break;
                case 4:
                    //shift *
                    System.out.println("Shift 7");
                    stack.add("*");
                    stack.add("7");
                    ip++;
                    break;
                case 5:
                	//shift )
                    System.out.println("Shift 11");
                    stack.add(")");
                    stack.add("11");
                    ip++;
                    break;
                case 6:
                    System.out.println("Reduce by E --> E + T");
                    for (int j = 0; j < 6; j++) {
                        stack.pop();
                    }
                    s = Integer.parseInt(stack.peek());
                    int x = table[s][6];
                    stack.add("E");
                    stack.add(Integer.toString(x));
                    break;
                case 7:
                    System.out.println("Reduce by E --> T");
                    for (int j = 0; j < 2; j++) {
                        stack.pop();
                    }
                    s = Integer.parseInt(stack.peek());
                    x = table[s][6];
                    stack.add("E");
                    stack.add(Integer.toString(x));
                    break;
                case 8:
                    System.out.println("Reduce by T --> T * F");
                    for (int j = 0; j < 6; j++) {
                        stack.pop();
                    }
                    s = Integer.parseInt(stack.peek());
                    x = table[s][7];
                    stack.add("T");
                    stack.add(Integer.toString(x));
                    break;
                case 9:
                    System.out.println("Reduce by T --> F");
                    for (int j = 0; j < 2; j++) {
                        stack.pop();
                    }
                    s = Integer.parseInt(stack.peek());
                    x = table[s][7];
                    stack.add("T");
                    stack.add(Integer.toString(x));
                    break;
                case 10:
                    System.out.println("Reduce by F --> ( E )");
                    for (int j = 0; j < 6; j++) {
                        stack.pop();
                    }
                    s = Integer.parseInt(stack.peek());
                    x = table[s][8];
                    stack.add("F");
                    stack.add(Integer.toString(x));
                    break;
                case 11:
                    System.out.println("Reduce by F --> id");
                    for (int j = 0; j < 2; j++) {
                        stack.pop();
                    }
                    s = Integer.parseInt(stack.peek());
                    x = table[s][8];
                    stack.add("F");
                    stack.add(Integer.toString(x));
                    break;
                case 12:
                	flag = false;
                    System.out.println("Accept");
                    break;
                default:
                	System.out.println("Syntax Error");
                    System.exit(0);
                    break;
            	}
            }
            System.out.println();
        }
	}

	private static int getToken(String s) {
		
		switch (s) {
        case "id":
            return 0;
        case "+":
        	return 1;
        case "*":
        	return 2;
        case "(":
        	return 3;
        case ")":
        	return 4;
        case "$":
        	return 5;
        default:
            System.out.println("Syntax Error");
            System.exit(0);
            return -1;
		}	
	}
}
