import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;


public class Server {
    private int accessTime;
    //recently recorded access time
    private int recordAccessTime;
    //varying access time
    private int accessTimes[];
    
    //server bandwidth(unit:KB/ms = MB/s)    
    private double bandwidth;
    //recently recorded bandwidth
    private double recordBandwidth;
    //varying bandwidth
    private double bandwidths[];
    
    Server(int time,double bandwidth_) {
    	accessTime = time;
    	accessTimes = new int[5];
    	for(int i=0;i<5;i++)
    	{
    		accessTimes[i] = accessTime + 1000*(i-2);
    	}
    	bandwidth = bandwidth_;
    	bandwidths = new double[5];
    	for(int i=0;i<5;i++)
    	{
    		bandwidths[i] = bandwidth + 0.1*(i-2);
    	}
    }

    static Server[] getServers(int size) {
        Server[] s = new Server[size];
        for (int i = 0; i<size; ++i) {
        	s[i] = new Server (1000,0.1);
        }

        return s;
    }
    public int getAccessTime()
    {
    	return accessTime;
    }
    public void setAccessTime(int time)
    {
    	this.accessTime = time;
    }
    public int getRecordAccessTime()
    {
    	return recordAccessTime;
    }
    public void setRecordAccessTime(int time)
    {
    	this.recordAccessTime = time;
    }
    public int[] getAccessTimes()
    {
    	return accessTimes;
    }
	public Server() {
		// TODO Auto-generated constructor stub
	}
	
	static String sconfig = "serverConfig.txt";
	static ArrayList<Server> getServerFromConfig(String sconfig) {
		
		ArrayList<Server> s = new ArrayList<Server>();
		Random ran = new Random(); 
		String line, idRange, timeRange, bandwidthRange;
		StringTokenizer line_tokenizer;
		int time;
		double bandwidth;
		int idMin, idMax, tMin, tMax, idNum = 0;
		double bwMin, bwMax = 0.0;
        try {                                                                                                                                
            BufferedReader brConfig = new BufferedReader (new FileReader (sconfig));    
			
            line = brConfig.readLine();                                                                                                                
//          skip comment lines and empty lines
        	while (line.startsWith("#") || line.equals("")) {
        		line = brConfig.readLine();
        	}
        	
            while (line != null) {                                                                                                           
            	try {                                                                                                                        
            		// format: 0	0-2		9-11
            		line_tokenizer = new StringTokenizer (line);
	            	//remove class
	            	line_tokenizer.nextToken();
	            	
	            	idRange = line_tokenizer.nextToken();
	            	timeRange = line_tokenizer.nextToken();                                                                                                                      
                    bandwidthRange = line_tokenizer.nextToken();
	            	
                    /*
                     * all fields: assume range is (min, max)
                    */
                    idMin = Integer.parseInt(idRange.substring( 0, idRange.indexOf('-')));
                    idMax = Integer.parseInt(idRange.substring(idRange.indexOf('-')+1, idRange.length()));
                    idNum += idMax - idMin + 1;
                    
                    tMin = Integer.parseInt(timeRange.substring( 0, timeRange.indexOf('-')));
                    tMax = Integer.parseInt(timeRange.substring(timeRange.indexOf('-')+1, timeRange.length()));
                    
                    bwMin = Double.parseDouble(bandwidthRange.substring(0, bandwidthRange.indexOf('-')));
                    bwMax = Double.parseDouble(bandwidthRange.substring(bandwidthRange.indexOf('-')+1, bandwidthRange.length()));
                    
                    
                    for (int i=idMin; i<=idMax; i++){
                    	if (tMax == tMin){
                    		time = tMin;
                    	} else {
                    		time = tMin + ran.nextInt(tMax - tMin +1);
                    	}
                    	if (bwMax == bwMin){
                    		bandwidth = bwMin;
                    	} else {
                    		bandwidth = bwMin + ran.nextDouble()*(bwMax - bwMin);
                    	}
                    	Server svr = new Server(time * 1000, bandwidth);
                    	s.add(i, svr); 
                    }
                     
                }                                                                                                                           
                catch (NumberFormatException exception)                                                                                     
                {                                                                                                                           
                    System.out.println ("FormatException. Line ignored:");                                                                  
                    System.out.println (line);                                                                                              
                    //System.exit(0);                                                                                                       
                }                                                                                                                           
                catch (NoSuchElementException exception)                                                                                    
                {                                                                                                                           
                    System.out.println ("NoSuchElementException. Line ignored:");                                                           
                    System.out.println (line);                                                                                              
                    //System.exit(0);                                                                                                       
                }    
                line = brConfig.readLine();     
            }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
            brConfig.close(); 
        }                                                                                                                                   
        catch (FileNotFoundException exception){                                                                                            
            System.out.println ("The file " + sconfig + " was not found.");                                                              
        }                                                                                                                                   
        catch (IOException exception){                                                                                                      
            System.out.println (exception);                                                                                                 
        }
        if (s.size() != idNum) {
        	throw new RuntimeException();
        } 
        return s;
	}
	
	public static void main(String[] args) throws IOException {
		
		ArrayList<Server> s = getServerFromConfig(sconfig);
		
		Writer output = new BufferedWriter(new FileWriter(new File("serverProfile.txt"))); 
        //PrintStream out_p = new PrintStream( new FileOutputStream("serverProfile.txt") ); 
		StringBuilder b = new StringBuilder(1024);
		int len = s.size();
		for (int i=0; i<len; i++) {
			Server svr = s.get(i);
			b.append(i);
			b.append("\t");
			b.append(svr.accessTime);
			b.append("\n");
		}
		output.write(b.toString());	
		output.close();
	}

}
