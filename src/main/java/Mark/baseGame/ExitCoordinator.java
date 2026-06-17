package Mark.baseGame;

public final class ExitCoordinator 
{

	private final Runnable exitAction; 
	
	public ExitCoordinator(Runnable exitAction)
	{
		this.exitAction = exitAction;
	}
	
	public void requestExit()
	{
		if(exitAction != null) exitAction.run();
	}
}
