import java.io.Serializable;
import java.util.ArrayList;

public class Directory implements Serializable {
	
	private ArrayList<Alumni> alumnus;
	private String title;
	private String path;
	private boolean Changed;
	
	public Directory(String title, ArrayList<Alumni> alumnus) {
		this.alumnus = alumnus;
		this.title = title;
		Changed = false;
	}
	public Directory(String title) {
		this.alumnus = new ArrayList<Alumni>();
		this.title = title;
		Changed = false;
	}
	
	public void add(Alumni Al) {
		alumnus.add(Al);
	}
	
	public void replace(int index, Alumni Al) {
		alumnus.set(index, Al);
		alumnus.remove(index+1);
	}
	
	public void delete(int index) {
		alumnus.remove(index);
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isChanged() {
		return Changed;
	}
	public void setChanged(boolean isChanged) {
		Changed = isChanged;
	}
	
	public ArrayList<Alumni> getAlumnus() {
		return alumnus;
	}
	
	@Override
	public String toString() {
		String star = (Changed) ? "*":"";
		return title + " " + star;
	}
	
	
}
