import java.util.ArrayList;

public class MyArrayList extends ArrayList<String> implements Comparable<MyArrayList> 
{
    public MyArrayList()
    {        
    }
    public MyArrayList(MyArrayList o) {
        this.addAll(o);
    }
    
    @Override
    public int compareTo(MyArrayList o) 
    {
        int i,sz=this.size();
        if(sz==o.size())
        {
            for(i=0;i<sz;i++)
            {
//                if(!this.get(i).equals(o.get(i)))
//                {
//                    return this.get(i).compareTo(o.get(i));
//                }
                if(!this.contains(o.get(i)))
                {
                    return this.get(i).compareTo(o.get(i));
                }
                        
            }
           // System.out.println("yeppy");
            return 0;
        }
        else if(sz<o.size())
            return -1;
        else return 1;
           
    }
    /*
     * Method name : containsItemSet
     * Params: MyArrayList 
     * Returns: MyArrayList
     * 
     * Desc: This method checks the elements in sub, if they all appear in the calling MyArrayList or Not.
     * If Yes then return the elements other than those in Sub else return null.
     */
//    public MyArrayList containsItemSet(MyArrayList sub)
//    {
//        int i,sz=this.size();
//        MyArrayList rhsList=new MyArrayList();
//        if(sz==sub.size())
//        {
//            return null;
//        }
//        else
//        {
////            for(i=0;i<sz;i++)
////            {
////                if(this.containsAll(sub))
////            }
//        }
//        
//            for(i=0;i<sz;i++)
//            {
////                if(!this.get(i).equals(o.get(i)))
////                {
////                    return this.get(i).compareTo(o.get(i));
////                }
//                if(!this.contains(o.get(i)))
//                {
//                    return this.get(i).compareTo(o.get(i));
//                }
//                        
//            }
//           // System.out.println("yeppy");
//            return 0;
//        }
//        else if(sz<o.size())
//            return -1;
//        else return 1;
//          for(j=i+1;j<size;j++)
//            {
//                levelTreeMapToCheck=freqSetAfterPruning.get(j);
//                int szOfLevelTreeMapToCheck=levelTreeMapToCheck.size();
//            }
//    }
    
}
