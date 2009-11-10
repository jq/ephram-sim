import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;


public class Data implements Comparable<Data>{
	static final int dataNum = 688;
	// when src updated, how many cache server will update at the same time
	static int updateNum = 1;
//	static int cacheNum = 2;
	//number of cache server
	static int replicaNum = 4;
	// data object access num & update num
	private int dataAccessNum = 0;
	private int dataUpdateNum = 0;
	static final double alpha = 0.2;
	
	int cacheUnappliedUpdate = 0;
	
	//size if data object(unit: KB)
	private int size;
	static final int dataObjectSize = 50;
	
	//src data price
	double priceRandom = Math.random();
	double priceLinear = 1.0;
	
    Server src;
    int seed = 0;
    Long time;
//    ArrayList<Server> fresh = new ArrayList<Server>(cacheNum);
//    ArrayList<Server> stale = new ArrayList<Server>(cacheNum);
    
    //replicas stored on other servers
    ArrayList<Server> replicas = new ArrayList<Server>(replicaNum);
    private int[] unappliedUpdates = new int[replicaNum]; 
    //replicas data price
    private double[] replicaPriceRandom = new double[replicaNum];
    private double[] replicaPriceLinear = new double[replicaNum];
    
    final static String DataConfig = "dataConfig.txt";
    
    Data(Server s,int dataSize) {
    	src = s;
    	time = new Long(0);
    	
    	for(int i=0;i<replicaNum;i++)
    	{
    		replicaPriceRandom[i] = Math.random();
    	}
    	
    	size = dataSize;
    }

    public Server getRandomCacheServer() {
    	seed++;
    	//Server s = stale.get(seed % stale.size());
    	Server s = replicas.get(seed % replicas.size());
    	if (s==null) {
    		throw new RuntimeException();
    	}
    	return s;
    }

//    public ArrayList<Solution> getSolutions() {
//    	ArrayList<Solution> slist = new ArrayList<Solution>(fresh.size() + stale.size()+1);
//    	slist.add(new Solution(1, src.accessTime, this, false));
//
//    	for (int j = 0; j<fresh.size(); ++j) {
//    		slist.add(new Solution(1, fresh.get(j).accessTime, this, false));
//    	}
//    	for (int j = 0; j<stale.size(); ++j) {
//    		slist.add(new Solution(0, stale.get(j).accessTime, this, true));
//    	}
//    	return slist;
//    }
    public ArrayList<Solution> getSolutions()
    {
    	ArrayList<Solution> sList = new ArrayList<Solution>(unappliedUpdates.length+1);
    	sList.add(new Solution(1,src.getRecordAccessTime(),this,false,priceLinear));
    	for (int j=0;j<replicas.size();++j)
    	{
    		if(unappliedUpdates[j]==0)//fresh data
    		{
    			sList.add(new Solution(1,replicas.get(j).getRecordAccessTime(),this,true,replicaPriceLinear[j]));
    		}
    		else
				sList.add(new Solution(0,replicas.get(j).getRecordAccessTime(),this,false,replicaPriceLinear[j])); 			
    	}
    	return sList;
    }

    //把688个数据分布到n个server上
//    static Data[] getDatas(Server[] s) {
//        Data[] d = new Data[dataNum];
//        int serverSize = s.length;
//        for (int i = 0; i<dataNum; ++i) {
//        	int srcNum = i%serverSize;
//        	d[i] = new Data(s[srcNum],dataObjectSize);
//
//        	// save cache
//        	for (int j = 1; j<=replicaNum; ++j) {
//        		int cacheNum = (srcNum + j) % serverSize;
//        		//d[i].stale.add(s[cacheNum]);
//        		d[i].replicas.add(s[cacheNum]);
//        	}
//        }
//        return d;
//    }
    static Data[] getDatas(Server[] s) {
        Data[] d = new Data[dataNum];
        int serverSize = s.length;
        
        Random ran = new Random(); 
        
        String line;
        StringTokenizer tokens;
        try{
        	BufferedReader brConfig = new BufferedReader(new FileReader(DataConfig));
        	line = brConfig.readLine();
        	while(line.startsWith("#")||line.equals(""))
        	{
        		line = brConfig.readLine();
        	}
        	while(line != null)
        	{
        		//format:  small	0-299	0-10
        		tokens = new StringTokenizer(line);
        		tokens.nextToken();//skip class
        		String idRange = tokens.nextToken();
        		String sizeRange = tokens.nextToken();
        		int idMin = Integer.parseInt(idRange.substring(0, idRange.indexOf('-')));
        		int idMax = Integer.parseInt(idRange.substring(idRange.indexOf('-')+1,idRange.length()));
        		int sizeMin = Integer.parseInt(sizeRange.substring(0, sizeRange.indexOf('-')));
        		int sizeMax = Integer.parseInt(sizeRange.substring(sizeRange.indexOf('-')+1,sizeRange.length()));
        		
        		for(int i=idMin; i<=idMax; i++)
        		{
        			int srcNum = i%serverSize;
        			//d[i] = new Data(s[srcNum],dataObjectSize);  
        			d[i] = new Data(s[srcNum],sizeMin+ran.nextInt(sizeMax-sizeMin+1));       			
        			// save replica
        			for (int j = 1; j<=replicaNum; ++j) {
        				int cacheNum = (srcNum + j) % serverSize;
        				d[i].replicas.add(s[cacheNum]);
        			}
        		}
        		line = brConfig.readLine();
        	}
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        return d;
    }

//    public void update(Server s) {
//    	if (s == src) {
//    		stale.addAll(fresh);
//    		fresh.clear();
//    		//change dataUpdateNum 
//    		dataUpdateNum++;
//    	} else {
//    		stale.remove(s);
//    		fresh.add(s);
//    	}
//    }
    
    public void update(Server s)
    {
    	if(s == src)
    	{
    		cacheUnappliedUpdate++;
    		for(int i=0;i<unappliedUpdates.length;i++)
    		{
    			unappliedUpdates[i]++;
    		}
    		dataUpdateNum++;
    	}
    	else
    	{
    		for(int i=0;i<replicas.size();i++)
    		{
    			if(replicas.get(i).equals(s))
    				unappliedUpdates[i]=0;
    		}
    	}
    }
    
    public void access()
    {
    	dataAccessNum++;
    }
    public double getAccessFreq()
    {
    	if(dataAccessNum==0||Access.totalAccessNum==0)
    		return 0.0;
    	return dataAccessNum/(double)Access.totalAccessNum;
    }
    public double getUpdateFreq()
    {
    	if(dataUpdateNum==0||Update.totalUpdateNum==0)
    		return 0.0;
    	return dataUpdateNum/(double)Update.totalUpdateNum;
    }
    public double computeM()
    {
    	return alpha*(1.0-getUpdateFreq())+(1.0-alpha)*getAccessFreq();
    }
    
    public int[] getUnappliedUpdates()
    {
    	return unappliedUpdates;
    }
    
    public double[] getReplicaPriceRandom()
    {
    	return replicaPriceRandom;
    }
    
    public double[] getReplicaPriceLinear()
    {
    	for(int i = 0;i < replicaNum; i++)
    	{
    		replicaPriceLinear[i] = unappliedUpdates[i]>10? 0.0 : (10-unappliedUpdates[i])/10.0;
    	}
    	return replicaPriceLinear;
    }

	public int compareTo(Data arg0) {
		// TODO Auto-generated method stub
		return time.compareTo(arg0.time);
	}

}
