package Mark.baseGame;

public final class ExitCoordinator 
{

	private final Runnable saveAction;
	private final Runnable exitAction; 
	
	public ExitCoordinator(Runnable saveAction, Runnable exitAction) 
	{
		this.exitAction = exitAction;
		this.saveAction = saveAction;
	}
	
	public void requestExit()
	{
		try
		{
			//Später Settings und Highscore speichern.
			if(saveAction != null) saveAction.run();
		}
		catch(Exception e)
		{
			System.err.println(Settings.getErrorMsgSaveFailed());
			System.err.println("Details: " + e.getMessage());
		}
		finally
		{
			if(exitAction != null) exitAction.run();
		}
	}
}
