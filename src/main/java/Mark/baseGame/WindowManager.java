package Mark.baseGame;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowManager 
{

	private final Stage stage;
	private final double titleBarHeight;
	private final double windowScale; //0.60
	
	private WindowMode windowMode = WindowMode.WINDOWED;
	private Screen currentScreen;
	
	public WindowManager(Stage stage)
	{
		this(stage,Settings.getWindowScale(),CustomTitleBar.TITLE_BAR_HEIGHT);
	}
	
	public WindowManager(Stage stage, double windowScale, double titleBarHeight)
	{
		this.stage = stage;
		this.windowScale = windowScale;
		this.titleBarHeight = titleBarHeight;
	}


	public void initAndApplyStartupMode(WindowMode startupMode)
	{
		
		currentScreen = getScreenUnderMouse().orElse(Screen.getPrimary());
		windowMode = startupMode != null ? startupMode : WindowMode.WINDOWED;
		
		applyMode(windowMode, currentScreen, true);
		
	}
	
	public WindowMode getCurrentWindowMode()
	{
		return windowMode;
	}
	
	public Screen getCurrentScreen()
	{
		return currentScreen;
	}
	
	public void switchMode(WindowMode mode)
	{
		Screen target = getBestScreenForCurrentStageCenter().orElse(currentScreen != null ? currentScreen : Screen.getPrimary());
		applyMode(mode, target, true);
	}
	
	public void onTitleBarDragReleased()
	{
		if(windowMode != WindowMode.WINDOWED) return;
		Screen target = getBestScreenForCurrentStageCenter().orElse(currentScreen != null ? currentScreen : Screen.getPrimary());
		if(!sameScreen(target,currentScreen))
		{
			currentScreen = target;
			applyWindowed(target, false);
		}
	}
	
	
	private void applyMode(WindowMode mode, Screen targetScreen, boolean centerWindow)
	{
		this.windowMode = mode;
		this.currentScreen = targetScreen != null ? targetScreen : Screen.getPrimary();
		
		switch(mode)
		{
		case WINDOWED: applyWindowed(this.currentScreen,centerWindow); break;
		case BORDERLESS: applyBorderless(this.currentScreen); break;
		case FULLSCREEN: applyFullscreen(this.currentScreen); break;
		}
	}

	private void applyFullscreen(Screen currentScreen) 
	{
		
		Rectangle2D vb = currentScreen.getVisualBounds();
		stage.setX(vb.getMinX());
		stage.setY(vb.getMinY());
		
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.setMaximized(false);
		stage.setFullScreen(true);
	}

	private void applyBorderless(Screen currentScreen) 
	{
		Rectangle2D vb = currentScreen.getVisualBounds();
		
		if(stage.isFullScreen()) stage.setFullScreen(false);
		
		stage.setMaximized(false);
		stage.setX(vb.getMinX());
		stage.setY(vb.getMinY());
		stage.setHeight(vb.getHeight());
		stage.setWidth(vb.getWidth());
	}

	private void applyWindowed(Screen currentScreen, boolean centerWindow) 
	{
		Rectangle2D vb = currentScreen.getVisualBounds();
		
		if(stage.isFullScreen()) stage.setFullScreen(false);
		
		double targetWeight = Math.round(vb.getWidth() * windowScale);
		
		double aspectRatio = Settings.getAspectRatio();
		double targetHeight = (targetWeight / aspectRatio) + titleBarHeight;
		
		//mindestgröße 1280x720 
		targetWeight = Math.max(targetWeight, Settings.getStandardWindowWidth());
		targetHeight = Math.max(targetHeight, Settings.getStandardWindowHeightWithoutTitlebar() + titleBarHeight);

		double tolerance = Settings.getWindowResizeTolerance();
		if(Math.abs(stage.getWidth() - targetWeight)>tolerance) stage.setWidth(targetWeight);
		if(Math.abs(stage.getHeight() - targetHeight)>tolerance) stage.setHeight(targetHeight);
		
		if(centerWindow)
		{
			double x = vb.getMinX() + (vb.getWidth() - targetWeight) / 2.0;
			double y = vb.getMinY() + (vb.getHeight() - targetHeight) / 2.0;
			stage.setX(x);
			stage.setY(y);
		}
		
		stage.setMaximized(false);
	}
	
	public Optional<Screen> getBestScreenForCurrentStageCenter()
	{
		double centerX = stage.getX() + stage.getWidth() / 2.0;
		double centerY = stage.getY() + stage.getHeight() / 2.0;
		
		return Screen.getScreens().stream().filter(s -> contains(s.getVisualBounds(), centerX, centerY) || contains(s.getBounds(), centerX, centerY)).findFirst().or(()-> nearestScreen(centerX,centerY));
	}
	
	public Optional<Screen> getScreenUnderMouse()
	{
		try 
		{
			Point p = MouseInfo.getPointerInfo().getLocation();
			double x = p.getX();
			double y = p.getY();
			
			return Screen.getScreens().stream().filter(s -> contains(s.getBounds(),x,y)).findFirst().or(()-> Screen.getScreens().stream().filter(s -> contains(s.getVisualBounds(),x,y)).findFirst());
		}
		catch(Exception e)
		{
			return Optional.empty();
		}
	}
	
	private Optional<Screen> nearestScreen(double x, double y)
	{
		List<Screen> screens = Screen.getScreens();
		if(screens.isEmpty()) return Optional.empty();
		
		return screens.stream().min(Comparator.comparingDouble(s -> distanceToRect(s.getBounds(),x,y)));
	}
	
	private boolean contains(Rectangle2D r, double x, double y)
	{
		return x >= r.getMinX() && x <= r.getMaxX() && y >= r.getMinY() && y <= r.getMaxY(); // nachfragen
	}
	
	private double distanceToRect(Rectangle2D r, double x, double y)
	{
		double dx = Math.max(Math.max(r.getMinX() - x, 0) , x - r.getMaxX());
		double dy = Math.max(Math.max(r.getMinY() - y, 0) , y - r.getMaxY());
		
		return Math.hypot(dx, dy);
	}
	
	private boolean sameScreen(Screen a, Screen b)
	{
		if(a==b) return true;
		if(a == null || b == null) return false;
		return a.getBounds().equals(b.getBounds()) && a.getVisualBounds().equals(b.getVisualBounds());
	}
	
}
