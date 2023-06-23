import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DirectoryUI {
	
	static FileSystem fs = new FileSystem();

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to the Alumni Directory System\r\n");
		while(true) {
			FileMenu();
		}
	}

	private static void FileMenu() throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("File Menu\r\n");
		
		System.out.println("Directories:");
		System.out.println(fs.toString());
		System.out.println();
		System.out.println();
		
		System.out.print("Please select one action:\r\n"
				+ "\tNew (1)\r\n"
				+ "\tOpen (2)\r\n"
				+ "\tClose (3 [directory number])\r\n"
				+ "\tSave (4 [directory number])\r\n"
				+ "\tSave As (5 [directory number])\r\n"
				+ "\tEdit (6 [directory number])\r\n"
				+ "\tQuit (7)\r\n"
				+ "\r\n"
				+ "Enter a number: ");
		int number = sc.nextInt();
		int dir = 0;
		if(number == 3 || number == 4 || number == 5 || number == 6) dir = sc.nextInt()-1;
		
		switch(number) {
		case 1:
			fs.createDirectory();
			break;
		case 2:
			JFileChooser chooser = new JFileChooser();
	        FileNameExtensionFilter filter = new FileNameExtensionFilter("Alumni Directory Object", "ser");
	        chooser.setFileFilter(filter);
	        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	        	
	        		fs.loadDirectory(chooser.getSelectedFile());
	        	
	        break;
		case 3:
			checkChanges(dir);
			fs.closeDirectory(dir);
			break;
		case 4:
			try {
	    		fs.saveDirectory(dir, null);
	    	}catch(Exception e) {
	    		JFileChooser chooser2 = new JFileChooser();
				chooser2.setDialogTitle("Save");
		        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Alumni Directory Object", "ser");
		        chooser2.setFileFilter(filter2);
			    if (chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			    	try {
			    		File f = chooser2.getSelectedFile();
			    		fs.saveDirectory(dir, f);
			    		fs.getDirectory(dir).setTitle(f.getName().substring(0, f.getName().indexOf(".")));
			    		fs.getDirectory(dir).setPath(f.getPath());
			    	}catch(Exception e2) {}
			    }
	    	}
			break;
		case 5:
			JFileChooser chooser2 = new JFileChooser();
			chooser2.setDialogTitle("Save");
	        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Alumni Directory Object", "ser");
	        chooser2.setFileFilter(filter2);
		    if (chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    	try {		    		
		    		File f = chooser2.getSelectedFile();
		    		fs.saveDirectory(dir, f);
		    		fs.getDirectory(dir).setTitle(f.getName().substring(0, f.getName().indexOf(".")));
		    		fs.getDirectory(dir).setPath(f.getPath());
		    	}catch(Exception e) {}
		    }
		    break;
		case 6:
			directoryMenu(fs.getDirectory(dir));
			break;
		case 7:
			for(int i = 0; i < fs.getNumber(); i++)
				checkChanges(i);
			System.exit(0);
		}
		System.out.println("\r\n");
	}
	
	private static void directoryMenu(Directory directory) {
		while(true) {
			Scanner sc = new Scanner(System.in);
			System.out.println("\r\n\r\n");
			System.out.println(directory.getTitle() + " Directory Menu\r\n");
			
			System.out.println("Alumnus:");
			System.out.println(String.format("#    %-13s %-13s %-25s %-15s %-10s %-5s %-15s %-10s %-10s", "first Name", "last Name",
					"address", "city", "state", "zip", "faculty", "department", "phoneNumber"));
			int counter = 1;
			for(Alumni a : directory.getAlumnus())
				System.out.println(String.format("%-3d  ", counter++) + a.toString());
			
			System.out.println();
			System.out.println("Please select one action:\r\n"
					+ "\tAdd (1 [first name] [last name] [address] [city] [state] [zip] [faculty] [department] [phone number])\r\n"
					+ "\tEdit (2 [Alumni number])\r\n"
					+ "\tDelete (3 [Alumni number])\r\n"
					+ "\tSort by last name (4)\r\n"
					+ "\tSort by ZIP code (5)\r\n"
					+ "\tSort by department (6)\r\n"
					+ "\tSort by facult (7)\r\n"
					+ "\tPrint (8)\r\n"
					+ "\tFile Menu (9)\r\n"
					+ "\r\n"
					+ "Enter a number: ");
			
			int number = sc.nextInt();
			if(number != 8 && number != 9) directory.setChanged(true);
			
			switch(number) {
			case 1:
				directory.add(new Alumni(sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.nextInt(), 
						sc.next(), sc.next(), sc.nextInt()));
				break;
			case 2:
				EdittingMenu(directory.getAlumnus().get(sc.nextInt()-1));
				break;
			case 3:
				directory.delete(sc.nextInt()-1);
				break;
			case 4:
				Collections.sort(directory.getAlumnus(), new Comparator<Alumni>() {
					public int compare(Alumni a1,Alumni a2){
						int val = a1.getLastName().compareTo(a2.getLastName());
						if(val == 0)
							return a1.getFirstName().compareTo(a2.getFirstName());
						return val;
					}
				});
				break;
			case 5:
				Collections.sort(directory.getAlumnus(), new Comparator<Alumni>() {
					public int compare(Alumni a1,Alumni a2) {
						if(a1.getZip() == a2.getZip()) {
							int val = a1.getFirstName().compareTo(a2.getFirstName());
							if(val == 0)
								return a1.getLastName().compareTo(a2.getLastName());
							return val;
						}
						return a1.getZip() > a2.getZip() ? 1 : -1;
					}
				});
				break;
			case 6:
				Collections.sort(directory.getAlumnus(), new Comparator<Alumni>() {
					public int compare(Alumni a1,Alumni a2){
						return a1.getDepartment().compareTo(a2.getDepartment());
					}
				});
				break;
			case 7:
				Collections.sort(directory.getAlumnus(), new Comparator<Alumni>() {
					public int compare(Alumni a1,Alumni a2){
						return a1.getFaculty().compareTo(a2.getFaculty());
					}
				});
				break;
			case 8:
				for(Alumni al : directory.getAlumnus()) {
					System.out.println("|------------------------------------------|");
					System.out.println(String.format("|  %-40s|", String.format("Mr. %s %s", al.getLastName(), al.getFirstName())));
					System.out.println(String.format("|  %-40s|", String.format("%s, %s-%s %d", al.getAddress(), al.getState(), al.getCity(), al.getZip())));
					System.out.println(String.format("|  %-40s|", String.format("%s - %s", al.getFaculty(), al.getDepartment())));
					System.out.println(String.format("|  %-40s|", String.format("%d", al.getPhoneNumber())));
					System.out.println("|------------------------------------------|");
					System.out.println();
				}
			case 9:
				return;
			}
		}
	}

	private static void EdittingMenu(Alumni al) {
		
		while(true) {
			Scanner sc = new Scanner(System.in);
			System.out.println("\r\n\r\n");
			System.out.println("Editting Directory Menu\r\n");
			System.out.println("Alumni:");
			System.out.println("\t1 - first name = " + al.getFirstName() + "\r\n" +
					"\t2 - last name = " + al.getLastName() + "\r\n" +
					"\t3 - address = " + al.getAddress() + "\r\n" +
					"\t4 - city = " + al.getCity() + "\r\n" +
					"\t5 - state = " + al.getState() + "\r\n" +
					"\t6 - zip = " + al.getZip() + "\r\n" +
					"\t7 - faculty = " + al.getFaculty() + "\r\n" +
					"\t8 - department = " + al.getDepartment() + "\r\n" +
					"\t9 - phone number = " + al.getPhoneNumber());
			
			System.out.println();
			System.out.println("Please Enter [attribute number] [new value] for edit or [0] for return to Directory Menu: ");
			int number = sc.nextInt();
					
			switch(number) {
			case 0:
				return;
			case 1:
				al.setFirstName(sc.next());
				break;
			case 2:
				al.setLastName(sc.next());
				break;
			case 3:
				al.setAddress(sc.next());
				break;
			case 4:
				al.setCity(sc.next());;
				break;
			case 5:
				al.setState(sc.next());
				break;
			case 6:
				al.setZip(sc.nextInt());
				break;
			case 7:
				al.setFaculty(sc.next());
				break;
			case 8:
				al.setDepartment(sc.next());
				break;
			case 9:
				al.setPhoneNumber(sc.nextInt());
				break;
			}
		}
	}

	private static void checkChanges(int dir) {
		if(fs.getDirectory(dir).isChanged()) {
			int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to save directory " + fs.getDirectory(dir).getTitle() + " ?","Warning", JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION)
				try {
		    		fs.saveDirectory(dir, null);
		    	}catch(Exception e) {
		    		JFileChooser chooser2 = new JFileChooser();
					chooser2.setDialogTitle("Save");
			        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Alumni Directory Object", "ser");
			        chooser2.setFileFilter(filter2);
				    if (chooser2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				    	try {
				    		File f = chooser2.getSelectedFile();
				    		fs.saveDirectory(dir, f);
				    		fs.getDirectory(dir).setTitle(f.getName().substring(0, f.getName().indexOf(".")));
				    		fs.getDirectory(dir).setPath(f.getPath());
				    	}catch(Exception e2) {}
				    }
		    	}
		}
	}
}
