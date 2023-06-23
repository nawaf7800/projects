import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class parser {

	public static void main(String[] args) throws FileNotFoundException {
		
		File file = new File("input.txt");
		
        if (!file.exists()) {
            System.out.println("Files not founds");
            System.exit(0);
        }
        
        Scanner read = new Scanner(file);
        
        //1 is the 1st production (E => T E’), 2 is the 2nd (E’ => + T E’), etc
        int table[][] = {
        //		id	+	*	(	)	$
               {1,	0,	0,	1, 	0, 	0},
               {0, 	2, 	0, 	0, 	3,	3},
               {4, 	0, 	0, 	4, 	0, 	0},
               {0, 	6, 	5, 	0,	6, 	6},
               {8, 	0,	0, 	7, 	0, 	0},
            };
        
        while(read.hasNext()) {
        	
        	Stack<String> stack = new Stack<>();
            stack.add("0");
            
            String s = read.nextLine();
            String input[] = s.split(" ");
            int ip = 0;
            System.out.println("Left most derivation for the arithmetic expression " + s + ":");
            
            while(!stack.isEmpty()) {
            	
            	try {
            		int x = Integer.parseInt(stack.peek());
            		int a = getToken(input[ip]);
            		
            		if(table[x][a] > 0) {
            			
            			stack.pop();
                    	switch(table[x][a]) {
                    	case 1:
                    		System.out.println("E => T E’");
                    		stack.push("1");
                    		stack.push("2");
                    		break;
                    	case 2:
                    		System.out.println("E’ => + T E’");
                    		System.out.println();
                    		stack.push("1");
                    		stack.push("2");
                    		stack.push("+");
                    		break;
                    	case 3:
                    		System.out.println("E’ => none");
                    		break;
                    	case 4:
                    		System.out.println("T => F T’");
                    		stack.push("3");
                    		stack.push("4");
                    		break;
                    	case 5:
                    		System.out.println("T’ => * F T’");
                    		System.out.println();
                    		stack.push("3");
                    		stack.push("4");
                    		stack.push("*");
                    		break;
                    	case 6:
                    		System.out.println("T’ => none");
                    		break;
                    	case 7:
                    		System.out.println("F => ( E )");
                    		System.out.println();
                    		stack.push(")");
                    		stack.push("0");
                    		stack.push("(");
                    		break;
                    	case 8:
                    		System.out.println("F => id");
                    		System.out.println();
                    		stack.push("id");
                    		break;
                    	}
            		}
            		else {
            			System.err.println("Syntax Error");
                        System.exit(0);
            		}
            	}
            	catch(Exception e) {
            		
                	if(stack.peek().equals(input[ip])) {
                		stack.pop();
                		ip++;
                	}
                	else {
                		System.err.println("Syntax Error");
                        System.exit(0);
                	}
            	}
            }
            if(input.length == ip+1) System.out.println("Parsing successfully halts\r\n");
            else {
            	System.err.println("Syntax Error");
                System.exit(0);
            }
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
                System.err.println("Syntax Error");
                System.exit(0);
                return -1;
        }
	}
}