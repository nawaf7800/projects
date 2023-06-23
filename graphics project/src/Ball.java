
public class Ball {
	
    private Vector position;
    private Vector velocity;
    private Color color;
    private float radius;
    
    public Ball() {
        position = new Vector(0,0);
        velocity = new Vector(0,0);
        color = new Color(1,1,1);
        radius = 1;
    }
    
    // setters
    public void setPosition(float x, float y) {
        position.set(x,y);
    }
    public void setVelocity(float x, float y) {
        velocity.set(x,y);
    }
    public void setColor(float r, float g, float b) {
        color.set(r,g,b);
    }
    public void setRadius(float r) {
        radius = r;
    }
    
    // getters
    public Vector getPosition() {
        return position;
    }
    public Vector getVelocity() {
        return velocity;
    }
    public Color getColor() {
        return color;
    }
    public float getRadius() {
        return radius;
    }
    
    
    public void update(float frameTime) {
        Vector delta = velocity.clone().scale(frameTime);
        position.add(delta);
    }
    
    public void fire(float posX, float posY, float speed) {
        position.set(posX, posY);
        
        velocity.x = -1;
        velocity.y = (float)(Math.random() * 2 -1);
        velocity.normalize();
        velocity.scale(speed);        
    }    
}
