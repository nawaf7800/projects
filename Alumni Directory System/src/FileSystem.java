import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class FileSystem {
	
	Directory[] directories;
	int number;

	public FileSystem() {
		directories = new Directory[20];
		number = 0;
	} 
	
	public void createDirectory() {
		if(number < 20) {
			directories[number++] = new Directory("Untitled");
		}
	}
	
	public void saveDirectory(int index, File f) throws IOException {
		Directory dir = directories[index];
		dir.setChanged(false);
		if(f != null) dir.setPath(f.getPath());
		FileOutputStream fos = new FileOutputStream(dir.getPath());
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(dir);
		oos.flush();    
		oos.close();
	}
	
	public void loadDirectory(File f) throws Exception {
		FileInputStream fis = new FileInputStream(f.getPath());
		ObjectInputStream ois = new ObjectInputStream(fis);
		directories[number] = (Directory) ois.readObject();
		ois.close();
		directories[number++].setTitle(f.getName().substring(0, f.getName().indexOf(".")));
	}
	
	public void closeDirectory(int index) {
		for(int i = index; i <= number; i++) {
			directories[i] = directories[i+1];
		}
		number--;
	}
	
	public Directory getDirectory(int index) {
		return directories[index];
	}

	public int getNumber() {
		return number;
	}

	@Override
	public String toString() {
		String v = "";
		for(int i = 0; i < number; i++) {
			v += (i+1) + ". " + directories[i].toString() + "\n";
		}
		return v;
	}
	
	
}
