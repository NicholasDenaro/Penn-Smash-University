import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import denaro.nick.core.GameEngine;
import denaro.nick.core.Sprite;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.entity.Mask;


public class ParticleMask extends Entity
{

	public ParticleMask(double x,double y, Mask mask)
	{
		super(null,x,y);
		this.mask(mask.area());
		
		img=new BufferedImage(mask().getBounds().width,mask().getBounds().height,BufferedImage.TYPE_INT_ARGB);
		//System.out.println("image:"+img);
		Graphics2D g=img.createGraphics();
		g.setColor(new Color(0,0,0));
		g.fill(mask());
		g.dispose();
		depth(200);
	}

	@Override
	public void tick()
	{
		timealive--;
		if(timealive<0)
		{
			GameEngine engine=GameEngine.instance();
			engine.removeEntity(this,engine.location());
			//System.out.println("deleting the particle... =(");
		}
	}
	
	@Override
	public BufferedImage image()
	{
		return(img);
	}
	
	private BufferedImage img;
	
	private int timealive=60*2;
	
}
