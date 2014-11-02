import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import denaro.nick.core.entity.EntityEvent;
import denaro.nick.core.entity.EntityListener;
import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;


public class SmashServerClient extends Client
{

	public SmashServerClient(Socket socket) throws IOException
	{
		super(socket);
	}

	@Override
	public int maxMessageSize()
	{
		return 1024*10;//send 10kb? i think?
	}

	@Override
	public void handleMessages(MyInputStream in,int messageid) throws IOException
	{
		switch(messageid)
		{
			case 0:
				//Connect...?
				double posx=in.readDouble();
				double posy=in.readDouble();
				Main.entities.get(2).move(posx,posy);
				Message mes=new Message(0);
				mes.addDouble(Main.entities.get(1).x());
				mes.addDouble(Main.entities.get(1).y());
				addMessage(mes);
				sendMessages();
			return;
			
			case 1:
				//disconnect...?
				JOptionPane.showInternalMessageDialog(null,"The other player disconnected");
				System.exit(1);
			return;
			
			case 2:
				//move...?
				posx=in.readDouble();
				posy=in.readDouble();
				Main.entities.get(2).move(posx,posy);
			return;
			
			case 3:
				//attack...?
			return;
		}
	}
}
