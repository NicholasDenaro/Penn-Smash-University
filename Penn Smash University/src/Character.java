import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.GameEngine;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.timer.Timer;
import denaro.nick.core.view.GameView2D;


public class Character extends Entity implements ControllerListener
{

	public Character(Sprite sprite,double x,double y)
	{
		super(sprite,x,y);
	}

	@Override
	public void tick()
	{
		//gravity is real
		moveDelta(0,1);
		checkWallCollision();
		
		
		//Left Stick movement
		double angle = calcLStickAngle();
		
		
		angle = Math.toDegrees(angle);
		angle = (angle + 360)%360;
		
		if(angle <= (270-35)&& angle>90)
		{
			imageIndex(0);
			run(-keys[Main.LEFT]);
		}
		else if(angle >= (270+35) || angle<90)
		{
			imageIndex(0);
			run(keys[Main.RIGHT]);
		}
		else//crouching
		{
			crouch();
		}
		//End Movement
				
				
		if(xVelocity>0.5)
		{
			xVelocity-=0.5;
		}
		else if(xVelocity<-0.5)
		{
			xVelocity+=0.5;
		}
		else
		{
			xVelocity=0;
		}
		
		if(xVelocity>MAX_X_VELOCITY)
		{
			xVelocity=MAX_X_VELOCITY;
		}
		else if(xVelocity<-MAX_X_VELOCITY)
		{
			xVelocity=-MAX_X_VELOCITY;
		}
		
		moveDelta(xVelocity,0);
		checkWallCollision();
		
		//Jump
		if(keys[Main.X]==1||keys[Main.Y]==1)
		{
			imageIndex(2);
			//System.out.println("jump!!!");
			if(checkWallCollisionDelta(0,1))
			{
				//System.out.println("on wall");
				yVelocity=-6.5;
				jumpTimer=MAX_JUMP_TIMER;
				doubleJump=false;
				allowDoubleJump=false;
			}
			else
			{
				if(jumpTimer>0)
				{
					jumpTimer--;
					yVelocity=-6.5;
				}
				else if(!doubleJump&&allowDoubleJump)
				{
					yVelocity=-8;
					doubleJump=true;
					System.out.println("double jump!");
				}
			}
		}
		else
		{
			jumpTimer=0;
			allowDoubleJump=true;
		}
		
		yVelocity+=0.3;//gravity
		moveDelta(0,yVelocity);
		if(checkWallCollisionDelta(0,0))
		{
			move(lastX(),lastY());
			yVelocity=0;
			doubleJump=false;
		}
		else
		{
			if(keys[Main.X]==0&&keys[Main.Y]==0)
			{
				allowDoubleJump=true;
			}
		}
		//end jump
		
		//move to top-respawn
		GameEngine g = GameEngine.instance();
		GameView2D v = (GameView2D)g.view();
		int maxHeight = v.height();
		int maxWidth = v.width();
		double curHeight = y();
		if(curHeight>maxHeight)
		{
			
			/*Timer t = new Timer(1, false)
			{

				@Override
				public void action() {
					// TODO Auto-generated method stub
					
				}
				
			};
			t.*/
			move(maxWidth/2,0);
		}
			
		
		
	}
	
	
	/**
	 * Calculates and returns the Angle of the Left stick
	 * @return - the angle of the left stick 
	 */
	private double calcLStickAngle()
	{
		double hStick, vStick;
		if(keys[Main.LEFT]!=0)
			hStick = -keys[Main.LEFT]; 
		else
			hStick = keys[Main.RIGHT];
		if(keys[Main.UP]!=0)
			vStick = keys[Main.UP]; 
		else
			vStick = -keys[Main.DOWN];
		
		return Math.atan2(vStick, hStick);
	}
	
	/**
	 * Returns true if you are running
	 * @return returns horizontal velocity != 0
	 */
	private boolean isRunning()
	{
		return(xVelocity != 0.0);
	}
	
	/**
	 * increases the horizontal velocity based on x
	 * @param x - the amount to increase horizontal velocity
	 */
	private void run(double x)
	{
		xVelocity+=x;
	}
	
	/**
	 * Crouches and increases fall rate and slows sideways movement
	 * Changes image index on sprite page to crouch image
	 */
	private void crouch()
	{
		//fall faster
		yVelocity+=0.1;
		
		//if moving horizontally, slow down faster
		if(xVelocity!=0)
		{
			if(xVelocity>0)
				xVelocity-=.1;
			else
				xVelocity+=.1;
		}
		imageIndex(1);
	}
	private void checkWallCollision()
	{
		GameEngine engine=GameEngine.instance();
		Location loc=engine.location();
		ArrayList<Entity> entities=loc.entityList();
		for(Entity entity:entities)
		{
			if(entity!=this)
			{
				if(this.collision(x(),y(),entity))
				{
					move(lastX(),lastY());
				}
			}
		}
	}
	
	public boolean checkWallCollisionDelta(int x, int y)
	{
		GameEngine engine=GameEngine.instance();
		Location loc=engine.location();
		ArrayList<Entity> entities=loc.entityList();
		for(Entity entity:entities)
		{
			if(entity!=this)
			{
				if(this.collision(x()+x,y()+y,entity))
					return(true);
			}
		}
		return(false);
	}

	@Override
	public void actionPerformed(ControllerEvent event)
	{
		keys[event.code()]=event.modifier();
		if(event.action()==event.MOVED)
		{
			if(Math.abs(event.modifier())<0.1)
				keys[event.code()]=0;
		}
		
	}
	
	private double xVelocity=0;
	private double yVelocity=0;
	private double[] keys=new double[Main.LASTKEYACTION];
	private int jumpTimer=MAX_JUMP_TIMER;
	private boolean doubleJump;
	private boolean allowDoubleJump;
	
	
	private static final int MAX_JUMP_TIMER=10;
	
	public static final double MAX_X_VELOCITY=6;
}
