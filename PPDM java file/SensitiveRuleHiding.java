
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SensitiveRuleHiding {
       
        Map<String ,Integer>Sensitivity = new HashMap<>();
        Map<String ,Integer>IS = new HashMap<>();
        ArrayList<SensitivitySet> TransactionSet = new ArrayList();
        ArrayList<RulesBean> rulesBean = new ArrayList();
        ArrayList<RulesBean> sensitiveRules = new ArrayList();
        int minedRuleCount = 0;
        int sensitiveRuleCount=0;
        int TransactionCount = 0;
        int MIN_SUPPORT = 500;
        int MIN_CONFIDENCE = 90;
        public void read()
        {
            
            
            
            StringBuilder  stringBuilder = new StringBuilder();
            String filename = "Mined_rules.dat";
            
            try
            {
                BufferedReader reader = new BufferedReader( new FileReader (filename));
                String line = null;

                String ls = System.getProperty("line.separator");

                while( ( line = reader.readLine() ) != null ) 
                {
                    RulesBean obj = new RulesBean();
                    String []lines = line.split("] ");
                    lines[0]+="]";
                    String [] frequency = lines[1].split(" ");
                    obj.rule = lines[0];
                    obj.support = Integer.parseInt(frequency[0]);
                    obj.sup_antecedent = Integer.parseInt(frequency[1]);
                    rulesBean.add(obj);
                    minedRuleCount++;
                }
                System.out.println(minedRuleCount);
            }
            catch (Exception e) 
            {
                System.err.println(e);
            }
                
//----------------------------------------------------------------------------------------------------------            
            
            filename = "SensitiveRules.dat";
            
            try
            {
                BufferedReader reader = new BufferedReader( new FileReader (filename));
                String line = null;

                String ls = System.getProperty("line.separator");
                while( ( line = reader.readLine() ) != null ) 
                {
                    
                    //CreateIS(line);
                    RulesBean obj1 = new RulesBean();
                    obj1.rule = line;
                    
                    sensitiveRules.add(obj1);
                    String r = sensitiveRules.get(sensitiveRuleCount).rule;
                    r=r.trim();
                    sensitiveRuleCount++;
                    for(int i=0;i<minedRuleCount;i++)
                    {
                        if(r.equals(rulesBean.get(i).rule))
                        {
                            sensitiveRules.get(sensitiveRuleCount-1).support = rulesBean.get(i).support;
                            sensitiveRules.get(sensitiveRuleCount-1).sup_antecedent = rulesBean.get(i).sup_antecedent;
                            break;
                        }

                    }
                }
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
            for(int j=0;j<sensitiveRuleCount;j++)
            {
                System.out.println(sensitiveRules.get(j).rule+ " "+sensitiveRules.get(j).support+" "+sensitiveRules.get(j).sup_antecedent);
            }
            CreateIS();
            displayHashMap();
        }
        
        public void displayHashMap()
        {
            Set set = Sensitivity.entrySet();
            Iterator i = set.iterator();
            while(i.hasNext())
            {
                Map.Entry me = (Map.Entry)i.next();
                 
                System.out.print(me.getKey() + ": ");
                System.out.println(me.getValue());
            }
        }
        
        public void CreateIS()
        {
            Sensitivity.clear();
            IS.clear();
            for(int j=0;j<sensitiveRuleCount;j++)
            {
                String rule = sensitiveRules.get(j).rule;
                String sensitiveItems[] = rule.split("->");
                //System.out.println(sensitiveItems[0] + " " + sensitiveItems[1]);
         //-----------------------------------------------------------------------------------------
                sensitiveItems[0] = sensitiveItems[0].substring(1, sensitiveItems[0].length()-1);
                String itemInAntecedent[] = sensitiveItems[0].split(", ");
                for(int i = 0;i<itemInAntecedent.length;i++)
                {
                    //System.out.println(itemInAntecedent[i]);
                    if(Sensitivity.containsKey(itemInAntecedent[i]))
                    {
                        int val = Sensitivity.get(itemInAntecedent[i]);
                        val++;
                        Sensitivity.put(itemInAntecedent[i],val);
                    }
                    else
                        Sensitivity.put(itemInAntecedent[i],1);
                    //System.out.println("value" + " = " + Sensitivity.get(itemInAntecedent[i]));
                }
        //--------------------------------------------------------------------------------------------
                String Cons = sensitiveItems[1].trim();
                //System.out.println(Cons);

                Cons = Cons.substring(1, Cons.length()-1);
                //System.out.println(Cons);
                String itemInConsequent[] = Cons.split(", ");
                for(int i = 0;i<itemInConsequent.length;i++)
                {
                    //System.out.println(itemInConsequent[i]);
                    if(Sensitivity.containsKey(itemInConsequent[i]))
                    {
                        int val = Sensitivity.get(itemInConsequent[i]);
                        val++;
                        Sensitivity.put(itemInConsequent[i],val);
                    }
                    else
                        Sensitivity.put(itemInConsequent[i],1);
                    if(IS.containsKey(itemInConsequent[i]))
                    {
                        int val = IS.get(itemInConsequent[i]);
                        val++;
                        IS.put(itemInConsequent[i],val);
                    }
                    else
                        IS.put(itemInConsequent[i],1);
                    //System.out.println("value" + " = " + Sensitivity.get(itemInConsequent[i]));
                }
            }
            
        }
        
        public void sortHashMap()
        {
            
            
            
        } 
        
        public void SanitizeDatabase()
        {
            /*Set<Entry<String, Integer>> set = IS.entrySet();
            List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() 
            {
                public int compare(Map.Entry<String, Integer> o1,Map.Entry<String, Integer> o2) 
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            for (Entry<String, Integer> entry : list) 
            {
                System.out.println(entry.getKey()+" "+entry.getValue());
            }*/
            int count = sensitiveRuleCount;
            count=2;
            while(count!=0)
            {
                CreateIS();
                UpdateTransactionSensitivity();
                System.out.println("sensitiveRuleCount = "+sensitiveRuleCount);
                Set set = IS.entrySet();
                Iterator i = set.iterator();
                int highlySensitiveItem = 0;
                String item="";
                while(i.hasNext())
                {
                    Map.Entry me = (Map.Entry)i.next();
                    int val = (int) me.getValue();
                    if(val >= highlySensitiveItem)
                    {
                        highlySensitiveItem = val;
                        item = (String) me.getKey();
                    }

                    System.out.print(me.getKey() + ": ");
                    System.out.println(me.getValue());
                }
                System.out.println(highlySensitiveItem + " " + item);
                System.out.println("Sensitivity :");
                set = Sensitivity.entrySet();
                i = set.iterator();
                while(i.hasNext())
                {
                    Map.Entry me = (Map.Entry)i.next();
                    
                    System.out.print(me.getKey() + ": ");
                    System.out.println(me.getValue());
                }
                System.out.println("");
                int vall = IS.get(item);
                vall = vall-1;
                IS.put(item,vall);
                int getMaxSensitivity = 0;
                String getMaxRule="";
                int Klength = 0;
                for(int j=0;j<TransactionCount;j++)
                {
                    String rules = TransactionSet.get(j).rule;
                    String line[] = rules.split(" ");
                    for(int k=0;k<line.length;k++)
                    {
                        if(line[k].equals(item))
                        {
                            if(getMaxSensitivity<TransactionSet.get(j).sensitive)
                            {
                                getMaxSensitivity = TransactionSet.get(j).sensitive;
                                getMaxRule = rules;
                                Klength = rules.length();
                            }
                            else if(getMaxSensitivity == TransactionSet.get(j).sensitive && Klength>rules.length())
                            {
                                getMaxSensitivity = TransactionSet.get(j).sensitive;
                                getMaxRule = rules;
                                Klength = rules.length();
                            }
                        }
                    }
                }
                //System.out.println(getMaxRule + " " + getMaxSensitivity);
                String lines[] = getMaxRule.split(" ");
                String maxRuleAfterChange ="";
                for(int j=0;j<lines.length;j++)
                {
                    if(!lines[j].equals(item))
                    {
                        maxRuleAfterChange+=lines[j];
                        maxRuleAfterChange+=" ";
                    }

                }
                maxRuleAfterChange = maxRuleAfterChange.trim();
                //System.out.println(maxRuleAfterChange);
                for(int j = 0;j<TransactionCount;j++)
                {
                    System.out.println(TransactionSet.get(j).sensitive + "--" + TransactionSet.get(j).rule);
                }
                System.out.println("");
                System.out.println("After change in support");
                for(int j = 0;j<TransactionCount;j++)
                {
                    if(TransactionSet.get(j).rule.equals(getMaxRule))
                    {
                        TransactionSet.get(j).rule = maxRuleAfterChange;
                        TransactionSet.get(j).sensitive -=Sensitivity.get(item); 
                    }
                }
                /*for(int j = 0;j<TransactionCount;j++)
                {
                    System.out.println(TransactionSet.get(j).rule + " " + TransactionSet.get(j).sensitive);
                }*/
                for(int j=0;j<sensitiveRuleCount;j++)
                {
                    String rules = sensitiveRules.get(j).rule;
                    String line[];
                    line = rules.split("->");
                    //System.out.println(sensitiveItems[0] + " " + sensitiveItems[1]);
                    line[0] = line[0].substring(1, line[0].length()-1);
                    String itemInAntecedent[] = line[0].split(", ");
                    int flag = 0;
                    for(int k = 0;k<itemInAntecedent.length;k++)
                    {
                        if(itemInAntecedent[k].equals(item))
                        {
                            sensitiveRules.get(j).support-=1;
                            sensitiveRules.get(j).sup_antecedent-=1;
                            flag = 1;
                            break;
                        }
                    }
                    if(flag==0)
                    {
                        String Cons = line[1].trim();
                        Cons = Cons.substring(1, Cons.length()-1);
                        //System.out.println("jana " + Cons);
                        String itemInConsequent[] = Cons.split(", ");
                        for(int k = 0;k<itemInConsequent.length;k++)
                        {
                            if(itemInConsequent[k].equals(item))
                            {
                                sensitiveRules.get(j).support-=1;
                                break;
                            }
                        }
                    }            
                }
                int support;
                double confidence;
                Iterator itr = sensitiveRules.iterator();
                while(itr.hasNext()) 
                {           
                    RulesBean obj = (RulesBean) itr.next();
                    System.out.println(obj.rule+ " "+ obj.support+" "+obj.sup_antecedent);
                    confidence = (obj.support*100)/obj.sup_antecedent;
                    //System.out.println(confidence);
                    if(obj.support<MIN_SUPPORT || confidence<MIN_CONFIDENCE)
                    {
                        itr.remove();
                        sensitiveRuleCount--;
                    }
                }
                System.out.println();
                System.out.println("After delete of rule");
                for(int j=0;j<sensitiveRuleCount;j++)
                {
                    System.out.println(sensitiveRules.get(j).rule+" "+sensitiveRules.get(j).support+" "+sensitiveRules.get(j).sup_antecedent);
                }
                System.out.println("");
                for(int j = 0;j<TransactionCount;j++)
                {
                    System.out.println(TransactionSet.get(j).rule);
                }
                count--;
            }
        }
        
        public void UpdateTransactionSensitivity()
        {
            for(int i=0;i<TransactionCount;i++)
            {
                String lines []= TransactionSet.get(i).rule.split(" ");
                TransactionSet.get(i).sensitive = 0;
                for(int j=0;j<lines.length;j++)
                {
                    if(Sensitivity.containsKey(lines[j]))
                    {
                        TransactionSet.get(i).sensitive+=Sensitivity.get(lines[j]);
                    }
                }
            }
        }
        
        public void GetTransaction() 
        {
            
            
            StringBuilder  stringBuilder = new StringBuilder();
            String filename = "D_P4_100items.dat";
            
            try
            {
                BufferedReader reader = new BufferedReader( new FileReader (filename));
                String line = null;

                String ls = System.getProperty("line.separator");

                while( ( line = reader.readLine() ) != null ) 
                {
                    SensitivitySet obj = new SensitivitySet();
                    
                    obj.rule = line;
                    obj.sensitive = 0;
                    
                    String lines []= line.split(" ");
                    for(int i=0;i<lines.length;i++)
                    {
                        if(IS.containsKey(lines[i]))
                        {
                            obj.sensitive+=Sensitivity.get(lines[i]);
                        }
                    }
                    TransactionSet.add(obj);
                    
                    TransactionCount++;
                }
                //TransactionSet.sort();
                /*for(int i=0;i<TransactionCount;i++)
                {
                    System.out.println(TransactionSet.get(i).rule + " " + TransactionSet.get(i).sensitive);
                }*/
                
                
                
                
            }
            catch (Exception e) 
            {
                System.err.println(e);
            }
        }
        
        public void WriteSanitizedDatabase()
        {
            for(int i=0;i<TransactionCount;i++)
            {
                String transaction="";
                transaction+=TransactionSet.get(i).rule;
                try
                {
                    FileWriter fstream = new FileWriter("ModifiedDatabase.dat", true);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(transaction);
                    out.write("\n");
                    out.close();
                }
                catch (Exception e)
                {//Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        public static void main(String args[])
        {
            SensitiveRuleHiding obj = new SensitiveRuleHiding(); 
            obj.read();
            obj.GetTransaction();
            obj.SanitizeDatabase();
            obj.WriteSanitizedDatabase();
        }

    
    
}
