import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class MyTreeMap extends TreeMap<MyArrayList, Integer> 
{   
    public boolean contains(String key)
    {                
        MyArrayList temp;                
        Set<MyArrayList> s=this.keySet();
        Iterator<MyArrayList> iter=s.iterator();
        while(iter.hasNext())
        {
            temp=iter.next();
            if(temp.contains(key))
            return true;                   
        }
        return false;
    }        
}


