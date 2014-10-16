import java.io.IOException;

import denaro.nick.editor.Editor;


public class MainEditor
{
	public static void main(String[] args)
	{
		try
		{
			Main.createAssets();
		}
		catch(IOException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Editor().setVisible(true);
	}
}
