import java.util.ArrayList;

import denaro.nick.core.Focusable;
import denaro.nick.core.GameEngine;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;


public class Character extends Entity implements ControllerListener
{

	public Character(Sprite sprite,double x,double y)
	{
		super(sprite,x,y);
	}

	@Override
	public void tick()
	{
		
		moveDelta(0,1);
		checkWallCollision();
		
		if(keys[Main.DOWN]!=0)
		{
			yVelocity+=0.1;
			imageIndex(1);
		}
		else
		{
			imageIndex(0);
			if(keys[Main.LEFT]!=0)
			{
				//moveDelta(keys[Main.LEFT],0);
				xVelocity+=keys[Main.LEFT];
			}
			if(keys[Main.RIGHT]!=0)
			{
				//moveDelta(keys[Main.RIGHT],0);
				xVelocity+=keys[Main.RIGHT];
			}
		}
		
		
		
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
		
		if(keys[Main.X]==1||keys[Main.Y]==1)
		{
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
