
public class Paddle {
	
    private Vector position;
    private Color color;
    private float speed;
    private float width;
    private float height;
    
    private float minY;
    private float maxY;
    
    private boolean movingUp;       
    private boolean movingDown;
	
    public Paddle() {
        position = new Vector();
        color = new Color();
        speed = 0;
        width = height = 1;
        minY = maxY = 0;
        movingUp = movingDown = false;
    }
    
    // setters
    public void setPosition(float x, float y) {
        position.set(x, y);
    }
    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public void setWidth(float width) {
        this.width = width;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public void setBounds(float min, float max) {
        minY = min;
        maxY = max;
    }
    public void setMovingUp(boolean flag) {
        movingUp = flag;
    }
    public void setMovingDown(boolean flag) {
        movingDown = flag;
    }
    
    // getters
    public Vector getPosition() {
        return position;
    }
    public Color getColor() {
        return color;
    }
    public float getSpeed() {
        return speed;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    public boolean isMovingUp() {
        return movingUp;
    }
    public boolean isMovingDown() {
        return movingDown;
    }
    
    public void update(float frameTime) {
        if(movingUp) {
            position.y += speed * frameTime;
            if (position.y > maxY)
                position.y = maxY;
        }
        
        if(movingDown) {
            position.y -= speed * frameTime;
            if (position.y < minY) position.y = minY;
        }
    }
}
