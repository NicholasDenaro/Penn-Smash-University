
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import denaro.nick.controllertest.ControllerComponentConstants;
import denaro.nick.controllertest.XBoxAnalogEvent;
import denaro.nick.controllertest.XBoxController;
import denaro.nick.controllertest.XBoxControllerListener;
import denaro.nick.controllertest.XBoxButtonEvent;
import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.GameEngine;
import denaro.nick.core.view.GameView;
import denaro.nick.core.view.GameViewListener;

public class GamePadController extends Controller implements XBoxControllerListener
{
	public GamePadController()
	{
		super();
		if(defaultKeymap==null)
			createDefaultKeymap();
		keymap(defaultKeymap);
		
		wait=-1;
		waitTime=defaultWaitTime;
	}
	
	protected void createDefaultKeymap()
	{
		defaultKeymap=new HashMap<Integer,Integer>();
		defaultKeymap.put(ControllerComponentConstants.DPAD,-1);
		defaultKeymap.put(ControllerComponentConstants.A,Main.A);
		defaultKeymap.put(ControllerComponentConstants.B,Main.B);
		defaultKeymap.put(ControllerComponentConstants.START,Main.START);
		defaultKeymap.put(ControllerComponentConstants.BACK,Main.BACK);
		defaultKeymap.put(ControllerComponentConstants.X,Main.X);
		defaultKeymap.put(ControllerComponentConstants.Y,Main.Y);
		defaultKeymap.put(ControllerComponentConstants.LEFTBUMPER,Main.LB);
		defaultKeymap.put(ControllerComponentConstants.RIGHTBUMPER,Main.RB);
		defaultKeymap.put(ControllerComponentConstants.XBOXBUTTON,Main.XBOXBUTTON);
		}
	
	@Override
	public boolean init(GameEngine engine)
	{
		//System.out.println("adding...");
		XBoxController controller=new XBoxController();
		controller.addXBoxControllerListener(this);
		this.addControllerListener(engine);
		return(controller.isConnected());
		//System.out.println("added...?");
	}
	
	//private HashMap<Integer,Integer> keymap;
	
	//public HashMap<Integer,Integer> defaultKeymap;

	@Override
	public void buttonPressed(XBoxButtonEvent event)
	{
		if(!keymap().containsKey(event.getButtonCode()))
		{
			System.out.println("button not in map");
			return;
		}
		int key=keymap().get(event.getButtonCode());
		if(event.getButtonCode()==ControllerComponentConstants.DPAD)
		{
			if(event.pollData()==ControllerComponentConstants.DPAD_UP)
				key=Main.UP;
			else if(event.pollData()==ControllerComponentConstants.DPAD_RIGHT)
				key=Main.RIGHT;
			else if(event.pollData()==ControllerComponentConstants.DPAD_DOWN)
				key=Main.DOWN;
			else if(event.pollData()==ControllerComponentConstants.DPAD_LEFT)
				key=Main.LEFT;
		}
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,key));
	}

	@Override
	public void buttonReleased(XBoxButtonEvent event)
	{
		if(!keymap().containsKey(event.getButtonCode()))
			return;
		int key=keymap().get(event.getButtonCode());
		if(event.getButtonCode()==ControllerComponentConstants.DPAD)
		{
			if(event.pollData()==0.25)
				key=Main.UP;
			else if(event.pollData()==0.5)
				key=Main.RIGHT;
			else if(event.pollData()==0.75)
				key=Main.DOWN;
			else if(event.pollData()==1)
				key=Main.LEFT;
		}
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(this,ControllerEvent.RELEASED,key,0));
	}
	
	public void resetWait()
	{
		wait=-1;
		waitTime=defaultWaitTime;
		if(timer!=null)
		{
			timer.cancel();
			timer=null;
		}
	}

	@Override
	public void analogMoved(XBoxAnalogEvent event)
	{
		int key=-2;
		float value=0;
		if(event.getButtonCode()==ControllerComponentConstants.LEFTSTICK)
		{
			key=-1;
			
			if(event.pollDataX()>=0.2)
			{
				key=Main.RIGHT;
				value=event.pollDataX();
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,key,value));
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.LEFT,0));
			}
			else if(event.pollDataX()<-0.2)
			{
				key=Main.LEFT;
				value=Math.abs(event.pollDataX());
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,key,value));
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.RIGHT,0));
			}
			else
			{
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.LEFT,0));
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.RIGHT,0));
			}
			
			
			if(event.pollDataY()>=0.2)
			{
				key=Main.DOWN;
				value=event.pollDataY();
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,key,value));
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.UP,0));
			}
			else if(event.pollDataY()<-0.2)
			{
				key=Main.UP;
				value=Math.abs(event.pollDataY());
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,key,value));
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.DOWN,0));
			}
			else
			{
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.UP,0));
				for(ControllerListener listener:listeners())
					listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,Main.DOWN,0));
			}
		}
	}
	
	private int wait;
	private long waitTime;
	private Timer timer;
	
	private static final long defaultWaitTime=500;
}
