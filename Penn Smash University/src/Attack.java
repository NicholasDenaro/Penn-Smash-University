import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import denaro.nick.core.GameEngine;
import denaro.nick.core.Identifiable;
import denaro.nick.core.Location;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.entity.Mask;


public class Attack extends Identifiable
{
	public Attack(double damage, int castTime, int cooldown, Mask mask)
	{
		this.damage=damage;
		this.castTime=castTime;
		this.cooldown=cooldown;
		this.mask=mask;
	}
	
	public void execute(Character attacker)
	{
		//System.out.println("I'm attacking!!!");
		boolean hdir=attacker.hDirection();
		//System.out.println("direction: "+hdir);
		
		Location loc=GameEngine.instance().location();
		ArrayList<Entity> characters=loc.entityList(Character.class);
		AffineTransform t=new AffineTransform();
		t.translate(attacker.x()+(hdir?1:-1)*8,attacker.y()-24);
		
		t.scale(hdir?1:-1,1);
		
		/*ParticleMask part=new ParticleMask(0,0,new Mask(mask.area().createTransformedArea(t)));
		GameEngine.instance().addEntity(part,GameEngine.instance().location());
		*/
		
		
		Mask m=new Mask(mask.area().createTransformedArea(t));
		
		ParticleMask part=new ParticleMask(attacker.x()+(hdir?8:-32-8),attacker.y()-24,new Mask(mask.area()));
		GameEngine.instance().addEntity(part,GameEngine.instance().location());
		
		for(int i=0;i<characters.size();i++)
		{
			if(characters.get(i)!=attacker)
			{
				if(characters.get(i).collision(null,m.area()))
				{
					//System.out.println("I'm hit!!!");
					Character ch=(Character)characters.get(i);
					//bandaid uppermovement
					if(attacker.direction(ch)>3*Math.PI/2 || attacker.direction(ch)<Math.PI/2)
						ch.setVelocity(attacker.direction(ch)+Math.PI/8,damage);
					else
						ch.setVelocity(attacker.direction(ch)-Math.PI/8,damage);
				}
			}
		}
	}
	
	private int castTime;
	private int cooldown;
	private double damage;
	private Mask mask;
}
