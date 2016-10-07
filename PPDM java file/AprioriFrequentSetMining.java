import java.util.*;
import java.io.*;

public class AprioriFrequentSetMining 
{

    int MIN_SUPPORT,MIN_CONF_PERCENT, NO_OF_TRANSC;
    final String LINE_DELIM="\n";
    final String COL_DELIM=" ";//space
    String filename;
    private HashMap<Integer,MyArrayList> originalData;
    ArrayList<MyTreeMap> freqSetBeforePruning;
    ArrayList<MyTreeMap> freqSetAfterPruning;
    ArrayList<MyArrayList> ruleLHS;
    ArrayList<MyArrayList> ruleRHS;
    ArrayList<Double> ruleConf;
    ArrayList<Integer> ruleSup;
    ArrayList<Integer> antfreq;
    
    
    public AprioriFrequentSetMining(int MIN_SUPPORT,int MIN_CONF_PERCENT,String dataFileName) 
    {
        this.MIN_SUPPORT=MIN_SUPPORT;
        this.MIN_CONF_PERCENT=MIN_CONF_PERCENT;
        filename=dataFileName;
        freqSetBeforePruning=new ArrayList<>();
        freqSetAfterPruning=new ArrayList<>();        
    }    
    
    private String readAprioriDataSetFromFile()
    {
        //String dataSetStr=RWFileUtility.readFile(filename);
        //return dataSetStr;  
        StringBuilder  stringBuilder = new StringBuilder();
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader (filename));
            String line = null;

            String ls = System.getProperty("line.separator");

            while( ( line = reader.readLine() ) != null ) 
            {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }

            //System.out.println("ls="+ls);
        }
        catch (Exception e) 
        {
            System.err.println(e);
        }
        //System.out.println("tringBuilder.toString="+stringBuilder.toString());    
        return stringBuilder.toString();
    }
    public void generateAprioriDataHashMap(String data, String lineDelim, String colDelim)
    {        
        String transacStr[],itemStr[];
        
        transacStr=data.split(lineDelim);
        NO_OF_TRANSC=transacStr.length;
        int i,j,noOfItems;        
        originalData=new HashMap();
        MyArrayList transaction;
        
        
        for(i=0;i<NO_OF_TRANSC;i++)
        {
            //System.out.println(transacStr[i]);
            itemStr=transacStr[i].split(colDelim);
            noOfItems=itemStr.length;
            transaction=new MyArrayList();
            for(j=0;j<noOfItems-1;j++)
            {
                transaction.add(itemStr[j].trim());
            }
            originalData.put(i, transaction);            
        }            
    }
   
    public void printHashMap(HashMap<Integer,MyArrayList> hm)
    {
        int i,len;
        MyArrayList items;
        len=hm.size();
        for(i=0;i<len;i++)
        {
            System.out.print("\nTransaction "+i+": ");
            items=hm.get(i);
            System.out.println(items);
        }
    }
     public HashMap<Integer,MyArrayList> getOriginalTransactionDataHashMap()
    {
        return originalData;
    }
    public int getFreqOfItemSetInTransactions(MyArrayList itemSet)
    {
        int szOriData=originalData.size(), szItemSet=itemSet.size();
        MyArrayList singleTransaction;
        int i,j, freq=0,cnt;
        for(i=0;i<szOriData;i++)
        {
            singleTransaction=originalData.get(i);
            if(singleTransaction.containsAll(itemSet))
                freq++;
        }
        return freq;
    }
     public void generate1stLevelOfApriori()
    {
        int size = originalData.size();
        int i,j,transacsize,tmpFreq;
        String tmpVal;
        MyArrayList singleTransac,temp,itemsTravesed;
        MyTreeMap tm=new MyTreeMap();        
        for(i=0;i<size;i++)
        {
            singleTransac=originalData.get(i);
            transacsize=singleTransac.size();
            itemsTravesed=new MyArrayList();
            for(j=0;j<transacsize;j++)
            {
                tmpVal=singleTransac.get(j);
                System.out.println("items traverses:"+itemsTravesed);
                if(!itemsTravesed.contains(tmpVal))
                {
                    temp=new MyArrayList();
                    temp.add(tmpVal);
                    //System.out.println("inside if");
                    if(tm.contains(tmpVal))                        
                    {
                        //System.out.println("key already there:"+tmpVal);
                        //System.out.println("temp="+temp+"tm="+tm);
                        tmpFreq=tm.get(temp);
                        //System.out.println("get done freq="+tmpFreq);
                        tm.put(temp,tmpFreq+1);                        
                    }
                    else
                    {                                           
                        tm.put(temp, 1);
                    } 
                    itemsTravesed.add(tmpVal);
                }                  
            }                            
        }
        freqSetBeforePruning.add(tm);
        System.out.println("freq set"+freqSetBeforePruning);
    }
    public void pruneAndGenerateNextLevels()
    {
        int freqSetsize=freqSetBeforePruning.size(),freqOfItem;
        MyTreeMap unPrunedTree,prunedTree=new MyTreeMap();
        Set<MyArrayList> s;
        Iterator<MyArrayList> iter,insideIter;
        MyArrayList temp;
        System.out.println("Pruning Level: "+freqSetsize);
        unPrunedTree=freqSetBeforePruning.get(freqSetsize-1);
        s=unPrunedTree.keySet();
        iter=s.iterator();
        
        while(iter.hasNext())
        {
            temp=iter.next();
            freqOfItem=unPrunedTree.get(temp);
            if(freqOfItem>=MIN_SUPPORT)
            {
                prunedTree.put(temp, freqOfItem);
            }            
        } 
        freqSetAfterPruning.add(prunedTree);
        System.out.println("Pruned tree:"+prunedTree);        
        if(prunedTree.size()<=1)
        {
            System.out.println("\n\nNo more levels possible!!!");
            return;
        }           
        System.out.println("\n********************************************************");
        System.out.println("Generating next level:"+(freqSetsize+1));
        int prunedTreeSz=prunedTree.size();
        s=prunedTree.keySet();
        iter=s.iterator();
        int i,j,k,level=freqSetsize+1;
        MyArrayList firstList,concatingList;
        String concatItem;
        int concatLstSz;
        //generating candidate nodes/itemsets for next level
        MyTreeMap nextLevelTM=new MyTreeMap();
        for(i=0;iter.hasNext();i++)
        {
            firstList=iter.next();
            insideIter=s.iterator();
            for(j=0;insideIter.hasNext();j++)
            {
                if(j<=i)
                    insideIter.next();
                else
                {
                    temp=new MyArrayList(firstList);
                    //temp.addAll(firstList);
                    concatingList=insideIter.next();
                    for(k=0;k<level-1;k++)
                    {
                        concatItem=concatingList.get(k);
                        if(!temp.contains(concatItem))
                            temp.add(concatItem);                        
                    }
                    if(temp.size()==level)  
                    {
                        Collections.sort(temp);
                      // System.out.println("value in Temp to be added="+temp);
                        if(nextLevelTM.containsKey(temp))
                        {
                            nextLevelTM.put(temp,nextLevelTM.get(temp)+1);
                        }
                        else
                        nextLevelTM.put(temp,1);
                    }
                        
                }
            }
        }
        System.out.println("Level "+level+" with subset count = "+nextLevelTM);
        //Code to check for all subsets of the superset candidate item
        int cVal;
        /// if subsets are say for 4th level then there must be 4 subsets of 3 members 
        ///and then 1st one combines with other3 , 2nd one with next 2 , 3rd one with 4th. therefore 3+2+1
        if(level==2)
            cVal=1;
        else cVal=(level)*(level-1)/2;
        Set<MyArrayList> candidateSet=nextLevelTM.keySet();
        //System.out.println("candidate set"+candidateSet);
        ArrayList<MyArrayList> candidatesToBeRemoved=new ArrayList<>();
        iter=candidateSet.iterator();
        //finding invalid candidates for which all the subsets do not exist        
        while(iter.hasNext())
        {
            temp=iter.next();
            //System.out.println("temp="+temp);
            if(nextLevelTM.get(temp) <cVal)
                candidatesToBeRemoved.add(temp);
        }
        //removing the invalid candidates
        int szOfInvalidCand=candidatesToBeRemoved.size();
        MyArrayList invalidCand;
        for(i=0;i<szOfInvalidCand;i++)
        {
            invalidCand=candidatesToBeRemoved.get(i);
            nextLevelTM.remove(invalidCand);
        }
        System.out.println("Level "+level+" pruned based on subset-supset check = "+nextLevelTM);
        //Counting freq of each candidate item set
        candidateSet=nextLevelTM.keySet();
        //System.out.println("candidate set after"+candidateSet);
        iter=candidateSet.iterator();
        int freq;
        while(iter.hasNext())
        {
            temp=iter.next();
            System.out.println("temp after="+temp);
            freq=getFreqOfItemSetInTransactions(temp);
            nextLevelTM.put(temp,freq);            
        }
        freqSetBeforePruning.add(nextLevelTM);
        System.out.println("Level "+level+" before freq pruning = "+nextLevelTM);
        pruneAndGenerateNextLevels();
    }
   
    
    private void displayFreqSetItems() 
    {
        System.out.println("****************************************************************\n\n");
        System.out.println("Frequent Items:\n"+freqSetAfterPruning);
        System.out.println("****************************************************************\n\n");
        try{
        FileWriter fstream = new FileWriter("frequent_item_set.dat", true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(freqSetAfterPruning.toString());
        out.write(" ");
    //Close the output stream
        out.close();
        }
        catch (Exception e){//Catch exception if any
        System.err.println("Error: " + e.getMessage());
        }
        
       
      
        
    }

    private void generateAssociationRules() 
    {
        int size=freqSetAfterPruning.size(),i,j,k,l,treeMapSz;
        ruleLHS=new ArrayList<>();
        ruleRHS=new ArrayList<>();
        ruleConf=new ArrayList<>();
        ruleSup=new ArrayList<>();
        antfreq=new ArrayList<>();
        MyTreeMap levelTreeMap,levelTreeMapToCheck;
        MyArrayList singleItemSet,checkItemSet,rhsItemSet,lhsItemSet;
        Set<MyArrayList> itemSets,tempItemSets;
        Iterator<MyArrayList> iter,tempIter;
        for(i=0;i<size;i++)
        {
            System.out.println("Generating rules for level "+(i+1));
            levelTreeMap=freqSetAfterPruning.get(i);                                 
            itemSets=levelTreeMap.keySet();
            iter=itemSets.iterator();
            while(iter.hasNext())
            {
                singleItemSet=iter.next();                                                        
                for(k=i+1;k<size;k++)
                {
                    levelTreeMapToCheck=freqSetAfterPruning.get(k);   
                    tempItemSets=levelTreeMapToCheck.keySet();
                    tempIter=tempItemSets.iterator();
                    while(tempIter.hasNext())
                    {
                        checkItemSet=tempIter.next();
                        if(checkItemSet.containsAll(singleItemSet))
                        {
                            //System.out.println("Item Set "+singleItemSet+" is present in "+checkItemSet);
                            rhsItemSet=new MyArrayList(checkItemSet);
                            rhsItemSet.removeAll(singleItemSet);
                            lhsItemSet=new MyArrayList(singleItemSet);
                            ruleLHS.add(lhsItemSet);
                            ruleRHS.add(rhsItemSet);
                           double confd=levelTreeMapToCheck.get(checkItemSet)*100.0/levelTreeMap.get(singleItemSet);
                            //double confd=levelTreeMap.get(singleItemSet);
                            ruleConf.add(confd);
                            //double support=levelTreeMapToCheck.get(checkItemSet)*100.0/NO_OF_TRANSC;
                            int support=levelTreeMapToCheck.get(checkItemSet);
                            ruleSup.add(support);
                            int ant_freq = levelTreeMap.get(singleItemSet);
                            antfreq.add(ant_freq);
                            System.out.println("Rule Generated: "+lhsItemSet+" -> "+rhsItemSet+" conf= "+confd+" support= "+support);
                        }
                        else
                        {
                            System.out.println("Item Set "+singleItemSet+" is not present in "+checkItemSet);
                        }
                    }
                }
            }            
        }
    }
    private void displayAssociationRules() 
    {
        
        int sz=ruleConf.size(),k=1,i;
        Double conf;
        int sup,ant_freq;
        System.out.println("Associtaion Rules:");
        for(i=0;i<sz;i++)
        {
            conf=ruleConf.get(i);  
            ant_freq=antfreq.get(i);
            if(conf>=MIN_CONF_PERCENT)
            {
                sup=ruleSup.get(i);
                System.out.println("Rule :"+(k++)+" =>\t"+ruleLHS.get(i) + " -> "+ruleRHS.get(i) + " \tConfidence="+conf+"% Support="+sup+"%");
            String rule;
            rule=ruleLHS.get(i) + "->" + ruleRHS.get(i) + " " + sup + " " + ant_freq;
            try{
        FileWriter fstream = new FileWriter("Mined_rules.dat", true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(rule);
        out.write("\n");
    //Close the output stream
        out.close();
        }
        catch (Exception e){//Catch exception if any
        System.err.println("Error: " + e.getMessage());
        }
            }            
        }
    }
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) 
    {
        // TODO code application logic here
        
       String dataFileName="C:\\Users\\Gulshan_Rajak\\Documents\\NetBeansProjects\\Sillyplay\\sillyplay\\web\\static\\file\\user22\\Test_Database.dat";
       AprioriFrequentSetMining afsm=new AprioriFrequentSetMining(3,40,dataFileName);
       String data=afsm.readAprioriDataSetFromFile();
       afsm.generateAprioriDataHashMap(data, afsm.LINE_DELIM, afsm.COL_DELIM);
       afsm.printHashMap(afsm.getOriginalTransactionDataHashMap());
       afsm.generate1stLevelOfApriori();
       afsm.pruneAndGenerateNextLevels();
       afsm.displayFreqSetItems();
       afsm.generateAssociationRules();
       afsm.displayAssociationRules();
    }  
}