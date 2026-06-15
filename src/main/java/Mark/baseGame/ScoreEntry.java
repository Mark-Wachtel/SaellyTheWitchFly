package Mark.baseGame;

public class ScoreEntry
{

    private String name;
    private  int score;
    private int place;
    private boolean synced = true;

    public ScoreEntry(String name, int score, int place)
    {
        this.name = name;
        this.score = score;
        this.place = place;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public int getScore()
    {
        return score;
    }
    public int getPlace()
    {
        return place;
    }
    public boolean isSynced()
    {
        return synced;
    }
    public void setPlace(int place)
    {
        this.place = place;
    }
    public void setSynced(boolean synced)
    {
        this.synced = synced;
    }

}
