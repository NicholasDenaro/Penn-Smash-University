import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import denaro.nick.core.entity.EntityEvent;
import denaro.nick.core.entity.EntityListener;
import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;


public class SmashClient extends Client implements EntityListener
{

	public SmashClient(Socket socket) throws IOException
	{
		super(socket);
		Message connectMessage=new Message(0);
		connectMessage.addDouble(Main.entities.get(2).x());
		connectMessage.addDouble(Main.entities.get(2).y());
		addMessage(connectMessage);
		sendMessages();
	}

	@Override
	public int maxMessageSize()
	{
		return 1024*10;//send 10kb, I think?
	}

	@Override
	public void handleMessages(MyInputStream in,int messageid) throws IOException
	{
		switch(messageid)
		{
			case 0:
				//Connect...?
			return;
			
			case 1:
				//disconnect...?
				JOptionPane.showInternalMessageDialog(null,"The other player disconnected");
				System.exit(1);
			return;
			
			case 2:
				//move...?
				double posx=in.readDouble();
				double posy=in.readDouble();
				Main.entities.get(1).move(posx,posy);
			return;
			
			case 3:
				//attack...?
			return;
		}
	}

	@Override
	public void entityMove(EntityEvent event)
	{
		Message mes=new Message(2);
		mes.addDouble(event.movedTo().x);
		mes.addDouble(event.movedTo().y);
		addMessage(mes);
		sendMessages();
	}

	@Override
	public void entityDepthChange(EntityEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
}
