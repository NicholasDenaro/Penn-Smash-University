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
import denaro.nick.core.view.GameView2D;
import denaro.nick.core.view.GameViewListener;

public class KeyboardController extends Controller implements KeyListener
{
	public KeyboardController()
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
		//defaultKeymap.put(ControllerComponentConstants.DPAD,-1);
		
		defaultKeymap.put(KeyEvent.VK_UP,Main.UP);
		defaultKeymap.put(KeyEvent.VK_LEFT,Main.LEFT);
		defaultKeymap.put(KeyEvent.VK_RIGHT,Main.RIGHT);
		defaultKeymap.put(KeyEvent.VK_DOWN,Main.DOWN);
		defaultKeymap.put(KeyEvent.VK_A,Main.A);
		defaultKeymap.put(KeyEvent.VK_B,Main.B);
		defaultKeymap.put(KeyEvent.VK_ENTER,Main.START);
		defaultKeymap.put(KeyEvent.VK_BACK_SPACE,Main.BACK);
		defaultKeymap.put(KeyEvent.VK_X,Main.X);
		defaultKeymap.put(KeyEvent.VK_Y,Main.Y);
		defaultKeymap.put(KeyEvent.VK_E,Main.LB);
		defaultKeymap.put(KeyEvent.VK_Q,Main.RB);
	}
	
	@Override
	public boolean init(GameEngine engine)
	{
		//System.out.println("adding...");
		/*XBoxController controller=new XBoxController();
		controller.addXBoxControllerListener(this);*/
		
		((GameView2D)engine.view()).addKeyListener(this);
		this.addControllerListener(engine);
		
		
		return(true);
		//System.out.println("added...?");
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
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// TODO Auto-generated method stub
		if(!keymap().containsKey(event.getKeyCode()))
			return;
		int key=keymap().get(event.getKeyCode());
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(this,ControllerEvent.PRESSED,key));
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// TODO Auto-generated method stub
		if(!keymap().containsKey(event.getKeyCode()))
			return;
		int key=keymap().get(event.getKeyCode());
		for(ControllerListener listener:listeners())
			listener.actionPerformed(new ControllerEvent(this,ControllerEvent.RELEASED,key,0));
	}
	
	
	private int wait;
	private long waitTime;
	private Timer timer;
	
	private static final long defaultWaitTime=500;
}