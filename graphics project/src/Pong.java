import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
//import com.jogamp.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

//import com.jogamp.opengl.util.FPSAnimator;
//import com.jogamp.opengl.util.awt.TextRenderer;
import javax.media.opengl.GLCanvas;
import com.sun.opengl.util.Animator; 



public class Pong implements GLEventListener {
	
	// CONSTANTS
    private static final int FPS = 60;
    
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    
    private static final float BALL_SPEED = 250;
    private static final float BALL_RADIUS = 6;
    private static final float PADDLE_SPEED = 230;
    private static final float PADDLE_WIDTH = 10;
    private static final float PADDLE_HEIGHT = 90;
    
    private static final float AI_UPDATE_TIME = 0.3f;
    private static final int MAX_SCORE = 6;
    
    
    // MEMBER VARIABLES
    private float gameTime;
    private float frameTime;
    private long  prevTime;
    private float readyTime;
    private float aiTime;

    private int screenWidth;
    private int screenHeight;
    
    private int playerScore;
    private int computerScore;
    
    private static GLCanvas canvas;
    private static FPSAnimator animator;
    private static GLU glu;
    private static Frame frame;

    private Ball ball;
    private Paddle player;
    private Paddle computer;
    //private TextRenderer textRenderer;
    
    private GameState gameState = GameState.MENU;

    public enum GameState { MENU, START, READY, GAME }
    
    public Pong() {
        initPong();
        initJOGL();

        // reset timer
        prevTime = System.nanoTime();
        gameTime = frameTime = 0;
    }
    
    private void initPong() {
    	
        ball = new Ball();
        ball.setRadius(BALL_RADIUS);
        ball.setColor(1,1,1);
        
        player = new Paddle();
        player.setSpeed(PADDLE_SPEED);
        player.setColor(1,1,1);
        player.setWidth(PADDLE_WIDTH);
        player.setHeight(PADDLE_HEIGHT);
        player.setBounds(0, SCREEN_HEIGHT);
        player.setPosition(10.0f, SCREEN_HEIGHT/2.0f);
        
        computer = new Paddle();
        computer.setSpeed(PADDLE_SPEED);
        computer.setColor(1,1,1);
        computer.setWidth(PADDLE_WIDTH);
        computer.setHeight(PADDLE_HEIGHT);
        computer.setBounds(0, SCREEN_HEIGHT);
        computer.setPosition(SCREEN_WIDTH-10.0f, SCREEN_HEIGHT/2.0f);
        
        //textRenderer = new TextRenderer(new Font("Dialog", Font.BOLD, 60));
        playerScore = computerScore = 0;
    }
    
    private void initJOGL() {
    	
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        frame = new Frame("Pong Game");
        canvas = new GLCanvas(); //channnnnnnnnnnnnnnnnnnn
        animator = new FPSAnimator(FPS); //channnnnnnnnnnnnnnnn

        frame.add(canvas);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setLocation(100, 100);
        frame.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
            	animator.stop();
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);

        canvas.addGLEventListener(this);
        canvas.requestFocus();

        animator.start();
    }
    
    private float getFrameTime() {
    	
        long currTime = System.nanoTime();
        float calculatedFrameTime = (float)((currTime - prevTime) / 1000000000.0);
        
        prevTime = currTime;
        
        return calculatedFrameTime;
    }
    
    private void update() {
        
        if(gameState == GameState.MENU) {
            player.update(frameTime);
            return;
        }
        else if(gameState == GameState.READY) {
            readyTime += frameTime;
            player.update(frameTime);
            if(readyTime > 1.0) {
                setGameState(GameState.GAME);
            }
            ball.update(frameTime);
            return;
        }
        
        ball.update(frameTime);
        player.update(frameTime);
        computer.update(frameTime);
        
        updateAI();
        
        int hit = hitTest();
        if(hit==3 && (computerScore == MAX_SCORE || playerScore == MAX_SCORE)) setGameState(GameState.MENU);
    }
    
    private void updateAI()
    {
        aiTime += frameTime;
        if(aiTime < AI_UPDATE_TIME) return;
        if(ball.getVelocity().x < 0) return;

        computer.setMovingDown(false);
        computer.setMovingUp(false);
 
        float ballY = ball.getPosition().y;
        float paddleY = computer.getPosition().y;
        float offset = computer.getHeight() / 2.0f;

        if(ballY > (paddleY + offset)) {
            computer.setMovingUp(true);
        }
        else if(ballY < (paddleY - offset)) {
            computer.setMovingDown(true);
        }
        
        aiTime = 0;
    }
    
    private int hitTest() {
        int hit = 0;
        
        Vector position = ball.getPosition();
        Vector velocity = ball.getVelocity();
        Vector leftP = player.getPosition();
        Vector rightP = computer.getPosition();
        float offset = player.getHeight() * 0.5f;
        
        if (position.y < 0) {
            ball.setPosition(position.x, 0);
            ball.setVelocity(velocity.x, -velocity.y);
            hit = 1;
        }
        else if(position.y > SCREEN_HEIGHT) {
            ball.setPosition(position.x,SCREEN_HEIGHT);
            ball.setVelocity(velocity.x, -velocity.y);
            hit = 1;
        }
        
        if (position.x < 0) {
            computerScore++;
            hit = 3;
            setGameState(GameState.READY);
        }
        else if (position.x < leftP.x) {
            if ((leftP.y - offset) < position.y && position.y < (leftP.y + offset)) {
                ball.setPosition(leftP.x, position.y);
                ball.setVelocity(-velocity.x, velocity.y);
                hit = 2;
            }
        }
        else if (position.x > SCREEN_WIDTH) {
            playerScore++;
            hit = 3;
            setGameState(GameState.READY);
        }
        else if (position.x > rightP.x) {
            if (position.y > (rightP.y - offset) && position.y < (rightP.y + offset)) {
                ball.setPosition(rightP.x, position.y);
                ball.setVelocity(-velocity.x, velocity.y);
                hit = 2;
            }
        }
        return hit;
    }
    
    private void setGameState(GameState state) {
        gameState = state;
        switch(gameState) {
            case MENU: break;
            case START:
                playerScore = computerScore = 0;
                ball.setPosition(SCREEN_WIDTH/2.0f, SCREEN_HEIGHT/2.0f);
                ball.setVelocity(0,0);
                setGameState(GameState.READY);
                break;
            case READY: readyTime = 0; break;
            case GAME:
                ball.fire(SCREEN_WIDTH/2.0f, SCREEN_HEIGHT/2.0f, BALL_SPEED);
                break;
        }
    }
    
    private void drawFrame(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();

        drawBackground(gl);
        drawPaddles(gl);
        drawBall(gl);
        
        drawScores(gl);
        drawMessage(gl);
        drawResult(gl);
    }
    
    private void drawBall(GL2 gl) {
        Vector p = ball.getPosition();
        
        float r = ball.getRadius();
        Color color = ball.getColor();
        
        gl.glColor3f(color.getRed(),color.getGreen(),color.getBlue());
        
        gl.glBegin(GL.GL_TRIANGLES); 
        	gl.glVertex2f(p.x-r, p.y-r);
        	gl.glVertex2f(p.x+r, p.y-r);
        	gl.glVertex2f(p.x+r, p.y+r); 
        	gl.glVertex2f(p.x-r, p.y-r);
        	gl.glVertex2f(p.x+r, p.y+r);
        	gl.glVertex2f(p.x-r, p.y+r);
        gl.glEnd();
    }
    
    private void drawPaddles(GL2 gl) {
    	
        Vector center = player.getPosition();
        float offsetX = player.getWidth() / 2.0f;
        float offsetY = player.getHeight() / 2.0f;
        Color color = player.getColor();
        
        gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        gl.glBegin(GL.GL_TRIANGLES);
        	gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        	gl.glVertex2f(center.x + offsetX, center.y - offsetY);
        	gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        	gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        	gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        	gl.glVertex2f(center.x - offsetX, center.y + offsetY);
        gl.glEnd();

        center = computer.getPosition();
        offsetX = computer.getWidth() / 2.0f;
        offsetY = computer.getHeight() / 2.0f;
        color = computer.getColor();

        gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        gl.glBegin(GL.GL_TRIANGLES);
        	gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        	gl.glVertex2f(center.x + offsetX, center.y - offsetY);
        	gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        	gl.glVertex2f(center.x - offsetX, center.y - offsetY);
        	gl.glVertex2f(center.x + offsetX, center.y + offsetY);
        	gl.glVertex2f(center.x - offsetX, center.y + offsetY);
        gl.glEnd();
    }
    
    private void drawScores(GL2 gl) {
        String score1, score2;
        /*textRenderer.beginRendering(SCREEN_WIDTH, SCREEN_HEIGHT);
        
        textRenderer.setColor(1, 1, 1, 0.5f);
        
        score1 = String.valueOf(playerScore);
        textRenderer.draw(score1, 250, 420);
        
        score2 = String.valueOf(computerScore);
        textRenderer.draw(score2, 350, 420);
        
        textRenderer.endRendering();*/
    }
    
    private void drawMessage(GL2 gl) {
        if(gameState != GameState.MENU) return;
        
        String message = "Press SPACE to start";
        /*Rectangle2D rect = textRenderer.getBounds(message);
        
        textRenderer.beginRendering(SCREEN_WIDTH, SCREEN_HEIGHT);
        textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        textRenderer.draw(message, (int)(SCREEN_WIDTH/2.0 - rect.getCenterX()),
                          (int)(SCREEN_HEIGHT/2.0 - rect.getCenterY()));
        textRenderer.endRendering();*/
    }
    
    private void drawResult(GL2 gl) {
        if(playerScore != MAX_SCORE && computerScore != MAX_SCORE) return;
        
        String result = null;
        
        if(playerScore == MAX_SCORE) {
            result = "You Won! Congrats!";
        }
        else {
            result = "You Lost! Try Again!";
        }
        
        /*Rectangle2D rect = textRenderer.getBounds(result);
        
        textRenderer.beginRendering(SCREEN_WIDTH, SCREEN_HEIGHT);
        textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        textRenderer.draw(result, (int)(SCREEN_WIDTH/2.0 - rect.getCenterX()),
                          (int)(SCREEN_HEIGHT/2.0 - rect.getCenterY() + 80));
        textRenderer.endRendering();*/
    }

    private void drawBackground(GL2 gl) {
        gl.glLineWidth(10);
        gl.glColor3f(0.8f, 0.8f, 0.8f);
        gl.glBegin(GL.GL_LINES);
        	gl.glVertex2f(0, 0);
        	gl.glVertex2f(SCREEN_WIDTH, 0);
        gl.glEnd();
        
        gl.glBegin(GL.GL_LINES);
        	gl.glVertex2f(0, SCREEN_HEIGHT);
        	gl.glVertex2f(SCREEN_WIDTH, SCREEN_HEIGHT);
        gl.glEnd();
        
        final int DOT_LENGTH = 10;
        gl.glLineWidth(5);
        gl.glBegin(GL.GL_LINES);
        for(int i = 0; i <= SCREEN_HEIGHT; i += DOT_LENGTH * 2)
        {
            gl.glVertex2f(SCREEN_WIDTH/2, i);
            gl.glVertex2f(SCREEN_WIDTH/2, i + DOT_LENGTH);
        }
        gl.glEnd();
        gl.glLineWidth(1);
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        ((Component)drawable).addKeyListener( new MyEventListener());
        ((Component)drawable).addMouseMotionListener(new MyEventListener());

        GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        screenWidth = w;
        screenHeight = h;
        
        GL2 gl = drawable.getGL().getGL2();

        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, -1, 1);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        frameTime = getFrameTime();
        gameTime += frameTime;
        
        update();
        
        drawFrame(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable gLDrawable) {
    }
    
    // inner class to implements KeyListener and MouseMotionLister
    class MyEventListener implements KeyListener, MouseMotionListener {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                	animator.stop();
                    frame.dispose();
                    System.exit(0);
                    break;

                case KeyEvent.VK_UP:
                    player.setMovingUp(true);
                    break;

                case KeyEvent.VK_DOWN:
                    player.setMovingDown(true);
                    break;    
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    setGameState(GameState.START);
                    break;

                case KeyEvent.VK_UP:
                    player.setMovingUp(false);
                    break;

                case KeyEvent.VK_DOWN:
                    player.setMovingDown(false);
                    break;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            float y = (float)e.getY() / screenHeight * SCREEN_HEIGHT;
            player.getPosition().y = SCREEN_HEIGHT - y;
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
        }
        
        @Override
        public void keyTyped(KeyEvent e) {
        }
    }
    
    public static void main(String[] args) {
        Pong pong = new Pong();
    }
}
