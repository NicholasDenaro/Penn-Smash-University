import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import denaro.nick.controllertest.XBoxController;
import denaro.nick.core.FixedTickType;
import denaro.nick.core.Focusable;
import denaro.nick.core.GameEngine;
import denaro.nick.core.GameEngineException;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameMap;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.core.controller.Controller;
import denaro.nick.core.controller.ControllerListener;
import denaro.nick.core.entity.Entity;
import denaro.nick.core.view.GameView2D;
import denaro.nick.editor.Editor;


public class Main
{
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, GameEngineException
	{
		try
		{
			createAssets();
		}
		catch(IOException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GameEngine engine=GameEngine.instance(new FixedTickType(60),false);
		
		GameView2D view=new GameView2D(800, 500, 1, 1);
		engine.view(view);
		
		
		GameFrame frame=new GameFrame("Game frame! =D",engine);
		
		
		Location fd=Location.readFromStream(new ObjectInputStream(new FileInputStream(new File("FD.loc"))));
		
		engine.location(fd);
		
		frame.setVisible(true);
		
		engine.start();
		
		GamePadController controller=new GamePadController();
		engine.controller(controller);
		
		engine.requestFocus((Focusable)engine.location().entityList(Character.class).get(0));
		engine.addControllerListener((ControllerListener)engine.location().entityList(Character.class).get(0));
	}
	
	public static void createAssets() throws IOException, ClassNotFoundException
	{
		new Sprite("Player","Sprite.png",48,64,new Point(24,64));
		
		new Sprite("Wall","Wall.png",480,16,new Point(0,0));
		
		
		GameMap<Entity> gm=new GameMap<Entity>();
		
		Entity wall=new Entity(null,0,0)
		{
			public static final long serialVersionUID = -4057178112391429871L;
			@Override
			public void tick()
			{
			}
		};
		gm.add(wall);
				
		Character player=new Character(Sprite.sprite("Player"),0,0);
				
		gm.add(player);
		
		File f=new File("Player.ent");
		ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(f));
		Entity.writeToStream(out,player);
		out.close();
		
		f=new File("Wall.ent");
		out=new ObjectOutputStream(new FileOutputStream(f));
		Entity.writeToStream(out,wall);
		out.close();
	}
	
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
