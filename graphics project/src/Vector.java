
public class Vector {
	
    public float x;
    public float y;
    
    public Vector() {
        this(0,0);
    }
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vector(Vector v) {
        this(v.x, v.y);
    }
    
    // setters
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void set(Vector v) {
        this.set(v.x, v.y);
    }
    
    // vectors operations
    public Vector add(Vector rhs) {
        this.x += rhs.x;
        this.y += rhs.y;
        
        return this;
    }
    
    public Vector subtract(Vector rhs) {
        this.x -= rhs.x;
        this.y -= rhs.y;
        
        return this;
    }
    
    public Vector scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        
        return this;
    }
	
    public float dotProduct(Vector rhs) {
        return (this.x * rhs.x) + (this.y * rhs.y);
    }
    
    public float getDistance(Vector rhs) {
    	
        float disX = this.x - rhs.x;
        float disY = this.y - rhs.y;
        disX *= disX;
        disY *= disY;
        
        return (float)Math.sqrt(disX + disY);
    }
    
    public float getLength() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public Vector normalize() {
        float lengthInverse = 1 / this.getLength();
        this.x *= lengthInverse;
        this.y *= lengthInverse;
        
        return this;
    }
    
    public Vector clone()  {
        return new Vector(this);
    }
}
