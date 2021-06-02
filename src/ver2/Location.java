package ver2;

/**
 *  ver1.Location - המחלקה מגדירה את הטיפוס מיקום בלוח
 *  17.11.2020
 *  ilan
 */
public class Location
{
    // תכונות הם משתנים שמגדירים איפה ישמרו הנתונים של הטיפוס
    private int row;    // נשמור את מספר השורה
    private int col;    // נשמור את מספר העמודה
    
    
    // פעולות
    
    // constructor פעולה בונה
    public Location(int r, int c)
    {
        row = r;
        col = c;
    }
    public Location(String str)
    {
        row = str.charAt(1)-'0'-1;
        col = str.charAt(0)-'a';
    }
    // default constractor פעולה בונה ריקה
    public Location()
    {
        row = 0;
        col = 0;
    }
    public boolean isEqual(Location compareTo)
    {
        return  row== compareTo.getRow()&&col==compareTo.getCol();
    }

    // פעולה מאחזרת את ערך השורה
    public int getRow()
    {
        return row;
    }
    
    // פעולה מאחזרת את ערך העמודה של המיקום
    public int getCol()
    {
        return col;
    }

    public void setCol(char num){col=num;}
    public void setRow(int num){row=num;}

    // הפעולה מחזירה מחרוזת המתארת את מצב העצם
    @Override
    public String toString()
    {
        return "row="+row+",col="+col;
    }
    
    
}
