import denaro.nick.core.Focusable;
import denaro.nick.core.Sprite;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;


public class MenuCursor extends Entity implements Focusable, ControllerListener
{
	public MenuCursor(Sprite sprite,double x,double y, int maxIndex)
	{
		super(sprite,x,y);
		this.maxIndex=maxIndex;
		index=0;
	}

	@Override
	public void actionPerformed(ControllerEvent event)
	{
		if(event.code()==Main.UP)
			index--;
		else if(event.code()==Main.DOWN)
			index++;
		
		index=(index+maxIndex)%maxIndex;
	}

	@Override
	public void tick()
	{
		// TODO Auto-generated method stub
		
	}
	
	public int index()
	{
		return(index);
	}
	
	private int index;
	private int maxIndex;
}
