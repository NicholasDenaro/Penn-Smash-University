import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import denaro.nick.controllertest.XBoxController;
import denaro.nick.core.FixedFPSType;
import denaro.nick.core.FixedTickType;
import denaro.nick.core.Focusable;
import denaro.nick.core.GameEngine;
import denaro.nick.core.GameEngineException;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameMap;
import denaro.nick.core.Location;
import denaro.nick.core.LocationAddEntityException;
import denaro.nick.core.Sprite;
import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.entity.Mask;
import denaro.nick.core.view.GameView2D;
import denaro.nick.editor.Editor;
import denaro.nick.server.Server;


public class Main
{
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, GameEngineException
	{
		try
		{
			createAssets();
		}
		catch(IOException | ClassNotFoundException | LocationAddEntityException e)
		{
			e.printStackTrace();
		}
		
		GameEngine engine=GameEngine.instance(new FixedTickType(60),false);
		
		GameView2D view=new GameView2D(800, 500, 1, 1);
		engine.view(view);
		
		
		GameFrame frame=new GameFrame("Game frame! =D",engine);
		
		
		setupEngine(engine);
		
		engine.location(locations.get(1));
		engine.requestFocus(0,(Focusable)entities.get(3));
		
		
		frame.setVisible(true);
		
		engine.start();

	}
	
	public static void setupEngine(GameEngine engine)
	{
		GamePadController controller=new GamePadController();
		engine.controller(controller);
		
		
		GamePadController controller2=new GamePadController();
		engine.controller(controller2);
	}
	
	public static void createAssets() throws IOException, ClassNotFoundException, LocationAddEntityException
	{
		createSprites();
		
		createAttacks();
		
		createEntities();
		
		createLocations();
	}
	
	public static void createSprites() throws IOException
	{
		new Sprite("Player","Sprite.png",48,64,new Point(24,64));
		new Sprite("Player2","Sprite2.png",48,64,new Point(24,64));
		
		new Sprite("Wall","Wall.png",480,16,new Point(0,0));
	}
	
	public static void createEntities() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		Entity wall=new Entity(null,0,0)
		{
			public static final long serialVersionUID = -4057178112391429871L;
			@Override
			public void tick()
			{
			}
		};
		entities.add(wall);
				
		Character player=new Character(Sprite.sprite("Player"),0,0);
		player.mask(new Area(new Rectangle2D.Double(-2,-48,8,48)));
		//System.out.println("player.mask(): "+player.mask());
				
		entities.add(player);
		
		Character player2=new Character(Sprite.sprite("Player2"),0,0);
		player2.mask(new Area(new Rectangle2D.Double(-2,-48,8,48)));
		entities.add(player2);
		
		File f=new File("Player.ent");
		ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(f));
		Entity.writeToStream(out,player);
		out.close();
		
		player.attack=attacks.get(0);
		
		f=new File("Player2.ent");
		out=new ObjectOutputStream(new FileOutputStream(f));
		Entity.writeToStream(out,player2);
		out.close();
		
		f=new File("Wall.ent");
		out=new ObjectOutputStream(new FileOutputStream(f));
		Entity.writeToStream(out,wall);
		out.close();
		
		MenuCursor mainMenuCursor=new MenuCursor(null,0,0,2)
		{
			@Override
			public void actionPerformed(ControllerEvent event)
			{
				if(action)
					return;
				super.actionPerformed(event);
				if(event.code()==Main.A)
				{
					Main.startLocal();
					action=true;
				}
				if(event.code()==Main.START)
				{
					try
					{
						Main.startOnline();
						action=true;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				if(event.code()==Main.BACK)
				{
					try
					{
						Main.connectOnline();
						action=true;
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public BufferedImage image()
			{
				if(img==null)
				{
					GameEngine engine=GameEngine.instance();
					GameView2D view=(GameView2D)engine.view();
					img=new BufferedImage(view.width(),view.height(),BufferedImage.TYPE_INT_ARGB);
					Graphics2D g=img.createGraphics();
					g.setFont(new Font("Courier New",Font.BOLD,20));
					g.setColor(Color.black);
					
					FontMetrics fm=g.getFontMetrics();
					g.drawString("Press A to begin local multiplayer",view.width()/2-fm.stringWidth("Press A to begin local multiplayer")/2,view.height()/3);
					g.drawString("Press START to start a server",view.width()/2-fm.stringWidth("Press START to start a server")/2,view.height()/2);
					g.drawString("Press BACK to connect to a server",view.width()/2-fm.stringWidth("Press BACK to connect to a server")/2,view.height()*2/3);
					
					g.dispose();
				}
				return img;
			}
			
			private boolean action=false;
			private BufferedImage img=null;
		};
		entities.add(mainMenuCursor);
	}
	
	public static void createAttacks()
	{
		Attack attack=new Attack(0.5,1,1,new Mask(new Area(new Rectangle.Double(0,0,32,32))));
		attacks.add(attack);
	}
	
	public static void createLocations() throws ClassNotFoundException, FileNotFoundException, IOException, LocationAddEntityException
	{
		Location loc=Location.readFromStream(new ObjectInputStream(new FileInputStream(new File("FD.loc"))));
		locations.add(loc);
		
		Location mainMenu=new Location();
		mainMenu.addEntityUnprotected(entities.get(3));
		locations.add(mainMenu);
	}
	
	public static void startLocal()
	{
		GameEngine engine=GameEngine.instance();
		engine.location(locations.get(0));
		
		engine.requestFocus(0,(Focusable)entities.get(1));
		engine.addControllerListener((ControllerListener)entities.get(1));
		
		engine.requestFocus(1,(Focusable)entities.get(2));
		engine.addControllerListener((ControllerListener)entities.get(2));
	}
	
	public static void startOnline() throws Exception
	{
		try
		{
			String port=JOptionPane.showInputDialog(null,"Enter the port","00000");
			SmashServer server=new SmashServer(getIp(),new Integer(port));
			Main.entities.get(1).addListener(server);
			
			server.start();
			
			GameEngine engine=GameEngine.instance();
			engine.location(locations.get(0));
			engine.requestFocus(0,(Focusable)entities.get(1));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void connectOnline() throws IOException
	{
		String ip=JOptionPane.showInputDialog(null,"Enter the ip",getIp()+":00000");
		try
		{
			SmashClient client=new SmashClient(new Socket(ip.substring(0,ip.indexOf(':')),new Integer(ip.substring(ip.indexOf(':')+1))));
			Main.entities.get(2).addListener(client);
			
			client.start();
			
			GameEngine engine=GameEngine.instance();
			engine.location(locations.get(0));
			engine.requestFocus(0,(Focusable)entities.get(2));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * http://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static String getIp() throws IOException
	{
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	public static GameMap<Entity> entities=new GameMap<Entity>();
	public static GameMap<Location> locations=new GameMap<Location>();
	public static GameMap<Attack> attacks=new GameMap<Attack>();
	
	public static final int A=0;
	public static final int B=1;
	public static final int START=2;
	public static final int BACK=3;
	public static final int X=4;
	public static final int Y=5;
	public static final int LB=6;
	public static final int RB=7;
	
	public static final int UP=8;
	public static final int DOWN=9;
	public static final int LEFT=10;
	public static final int RIGHT=11;
	public static final int LASTKEYACTION=12;
}
