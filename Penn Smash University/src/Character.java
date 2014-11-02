import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.GameEngine;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.timer.TickingTimer;
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
		//GAME PHYSICS 
		
		//gravity is real
		gravity(.35);
		//Make sure if you're not moving purposely, you're slowing down
		applyFriction(.5);
		//Make sure you're not going through the floor!
		checkWallCollision();
		
		
		//PLAYER ATTACKING
		checkForAttacks();
		
		
		//PLAYER MOVEMENT
		
		//if character is allowed to move, check LStick
		if(allowMove)
		{
			//Left Stick movement
			double angle = calcLStickAngle(); 
			
			if(angle <= (270-35)&& angle>90)
			{
				imageIndex(3);
				
				if(keys[Main.LEFT]!=0)
				{
					//System.out.println("left: "+keys[Main.LEFT]);
					run(-keys[Main.LEFT]);
				}
			}
			else if(angle >= (270+35) || angle<90)
			{
				imageIndex(0);
				if(keys[Main.RIGHT]!=0)
					run(keys[Main.RIGHT]);
			}
			else//crouching
			{
				crouch();
			}
		}//end if(allowMove)
		
		//make sure our moving isn't too fast
		checkMaxXVelocity();
		
		//once velocity is ok, move and check collision
		moveDelta(xVelocity,0);
		checkWallCollision();
		
		//set back button to taunt
		if(keys[Main.BACK]> 0 && allowTaunt)
		{
			int startingImageIndex = 6;
			int endingImageIndex = 10;
			int numTicks = 30;
			taunt(startingImageIndex, endingImageIndex, numTicks);
		}
			
		
		
		//if x or y pressed, jump
		if(keys[Main.X]==1||keys[Main.Y]==1)
		{
			jump();
		}
		//if x and y not pressed
		else if(keys[Main.X]==0&&keys[Main.Y]==0)
		{
			jumpTimer=0;
			allowDoubleJump=true;
		}
		//end jump
		
		//if need to respawn - respawn
		checkRespawn();
			
		
	}//end tick

	private void jump()
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
				//NO. No. no. no... This is done. No more printing double jump. It's over. 
				//Stop it. Please just stop it. 
				//System.out.println("double jump!");
			}
		}
	}
	
	private void checkForAttacks()
	{
		if(keys[Main.A]==1)
		{
			if(allowAttack)
			{
				if(attack!=null)
					attack.execute(this);
				allowAttack=false;
			}
		}
		else
		{
			allowAttack=true;
		}
	}
	
	//TODO:turn this into a method that calls a 'slideshow' method
	//     allow taunt to break e.g. when hit
	/**
	 * goes through a taunt, starting at the first imageindex through to the second
	 * and updates image every numTicks
	 * @param start - starting index of taunt in sprite
	 * @param end - ending image of taunt in sprite
	 * @param numTicks - the number of ticks between updating each image
	 */
	private void taunt(int start, int end, int numTicks)
	{
		allowTaunt = false;
		allowMove = false;
		
		TickingTimer t = new TickingTimer(numTicks*(end-start+1),true)
		{
			int index = start;
			int ticks = 0;
			@Override
			public void action() {
				//last tick, reset image, allow taunt and move
				if(ticks==numTicks*(end-start+1))
				{
					imageIndex(0);
					allowTaunt = true;
					allowMove = true;
				}		
				//every ticks ticks change imageindex
				else if(ticks++%numTicks==0)
				{
					imageIndex(index++);
				}
				
							
			}			
		};
		GameEngine.instance().addTimer(t);
	}
	
	/**
	 * @return 
	 */
	private void checkMaxXVelocity()
	{
		if(xVelocity>MAX_X_VELOCITY)
		{
			xVelocity=MAX_X_VELOCITY;
		}
		else if(xVelocity<-MAX_X_VELOCITY)
		{
			xVelocity=-MAX_X_VELOCITY;
		}
	}
	
	
	/**
	 * checks if character needs to respawn - if they do, respawns them
	 * 
	 */
	private void checkRespawn()
	{ 
		//check of character has moved off of screen too far with an allowance of extra each way
		GameEngine g = GameEngine.instance();
		GameView2D v = (GameView2D)g.view();
		int maxHeight = v.height();
		int maxWidth = v.width();
		int hAllowance = maxHeight/5;
		int wAllowance = maxWidth/5;
		double curHeight = y();
		double curWidth = x();
		if(curHeight>(maxHeight+hAllowance) || curHeight<(-hAllowance) 
				|| curWidth>(maxWidth+wAllowance) || curWidth<(-wAllowance))
		{
			//set how long to wait to respawn
			double respawnTime = .5;
			
			//set off timer to respawn when done
			TickingTimer w = new TickingTimer((int)(respawnTime*60), false)
			{
				@Override
				public void action() {
					yVelocity=0;
					move(maxWidth/2,maxHeight/5);
				}
				
			};
			GameEngine.instance().addTimer(w);
		}
	}
	
	/**
	 * Reduces horizontal velocity by x unless it's less than x, then it becomes 0
	 * @param x parameter to reduce velocity by
	 */
	private void applyFriction(double x)
	{
		if(xVelocity==0)
			return;
		double xVelLast=xVelocity;
		if(xVelocity>x)
		{
			xVelocity-=x;
		}
		else if(xVelocity<-x)
		{
			xVelocity+=x;
		}
		else
		{
			xVelocity=0;
		}
		
		if(xVelLast>0&&xVelocity<0)
			xVelocity=0;
		
		if(xVelLast<0&&xVelocity>0)
			xVelocity=0;
	}
	
	/**
	 * Takes in a number, and moves delta down in the y direction by that number
	 * @param y - int to move delta down by
	 */
	private void gravity(double y)
	{
		yVelocity+=y;//gravity
		moveDelta(0,yVelocity);
		if(checkWallCollisionDelta(0,0))
		{
			double lasty=lastY();
			move(lastX(),lastY());
			yVelocity=0;
			
			if(y()<lasty)
				doubleJump=false;
		}
		else
		{
			if(keys[Main.X]==0&&keys[Main.Y]==0)
			{
				allowDoubleJump=true;
				//System.out.println("no collision with wall, allow jump and doubleJump");
			}
		}
		if(checkWallCollisionDelta(0,1))
		{
			//TODO Optimize this shit o_o
			doubleJump=false;
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
		
		double angle = Math.atan2(vStick, hStick);
		angle = Math.toDegrees(angle);
		angle = (angle + 360)%360;
		
		if(angle>90&&angle<270)
			hDirection=LEFT;
		else
			hDirection=RIGHT;
		
		return angle;
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
			if(entity!=this && entity instanceof ParticleMask==false)
			{
				//TODO: this is said issue? fix with interpolation?
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
			if(entity!=this&& entity instanceof ParticleMask==false)
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
		if(event.code()>=Main.LASTKEYACTION||event.code()==-1)
			return;
		keys[event.code()]=event.modifier();
		if(event.action()==event.MOVED)
		{
			if(Math.abs(event.modifier())<0.1)
				keys[event.code()]=0;
		}
	}
	
	public boolean hDirection()
	{
		return(hDirection);
	}
	
	public void setVelocity(double direction, double vel)
	{
		xVelocity+=Math.cos(direction)*vel;
		yVelocity-=Math.sin(direction)*vel;
	}
	
	protected Attack attack;
	
	private double xVelocity=0;
	private double yVelocity=0;
	private double[] keys=new double[Main.LASTKEYACTION];
	private int jumpTimer=MAX_JUMP_TIMER;
	private boolean doubleJump;
	private boolean allowDoubleJump;
	private boolean allowTaunt = true;
	private boolean allowMove = true;
	private int vDirection=1;//up=0;none=1;down=2;
	private boolean allowAttack=true;
	private boolean hDirection=LEFT;//left=false;right=true;
	
	
	private static final int MAX_JUMP_TIMER=10;
	public static final long serialVersionUID = 3485620288954336434L;
	
	public static final double MAX_X_VELOCITY=6;
	
	public static final boolean LEFT=false;
	public static final boolean RIGHT=true;
}
