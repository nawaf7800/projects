
public class Color {
	
    private float red;
    private float green;
    private float blue;
    
    // CONSTRUCTORS
    public Color() {
        set(1, 1, 1);
    }
    public Color(float r, float g, float b) {
        set(r, g, b);
    }
    public Color(Color c) {
        this(c.red, c.green, c.blue);
    }
    
    // setters
    public final void set(float r, float g, float b) {
        red = clamp(r);
        green = clamp(g);
        blue = clamp(b);
    }
    public void set(Color c) {
        set(c.red, c.green, c.blue);
    }
    public void setRed(float red) {
        this.red = clamp(red);
    }
    public void setGreen(float green) {
        this.green = clamp(green);
    }
    public void setBlue(float blue) {
        this.blue = clamp(blue);
    }

    // getters
    public Color get() {
        return this;
    }
    public float getRed() {
        return red;
    }
    public float getGreen() {
        return green;
    }
    public float getBlue() {
        return blue;
    }
    
    private float clamp(float value) 
    {
        if(value < 0) value = 0;
        else if(value > 1) value = 1;
        return value;
    }
}
