import java.io.IOException;
import java.net.Socket;

import denaro.nick.core.entity.EntityEvent;
import denaro.nick.core.entity.EntityListener;
import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.Server;


public class SmashServer extends Server implements EntityListener
{

	public SmashServer(String hostname,int port) throws IOException
	{
		super(hostname,port);
	}

	@Override
	public Client newClient(Socket socket) throws IOException
	{
		if(opponent!=null)
			return null;
		opponent=new SmashServerClient(socket);
		opponent.start();
		return opponent;
	}

	@Override
	public void entityMove(EntityEvent event)
	{
		if(opponent==null)
			return;
		Message mes=new Message(2);
		mes.addDouble(event.movedTo().x);
		mes.addDouble(event.movedTo().y);
		opponent.addMessage(mes);
		opponent.sendMessages();
	}

	@Override
	public void entityDepthChange(EntityEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	private SmashServerClient opponent;
}
