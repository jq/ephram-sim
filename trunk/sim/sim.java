import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;


public class sim {
	//get file full path of a directory
    static String[] getListFiles(String path) {
		java.io.File start = new java.io.File (path);
        String list[]=start.list();
        int total = (list == null)?0:list.length;
        for (int i = 0; i < total; i++) {
            String fullpath = path;
            if (!fullpath.endsWith (java.io.File.separator))
                fullpath += java.io.File.separator;
            fullpath += list[i];
            list[i] = fullpath;
        }
        return list;

    }

	@SuppressWarnings("unchecked")
// -c cache size -a access file -u user config file
	public static void main(String[] args) throws IOException {
    	int cacheSize = 50;
    	String asFile = "./as/query20k-data688-10days-uniform.txt.as";
    	String uFile = "./userProfile.txt";
    	String oFile = "./output";
    	int t = Cache.FIFO_ALL;
        for (int i = 0; i<args.length; ++i) {
        	String s = args[i];
        	if (s.compareTo("-c") == 0) {
        		++i;
        		cacheSize = Integer.parseInt(args[i]);
        	} else if (s.compareTo("-u") == 0) {
        		++i;
        		uFile = args[i];
        	} else if (s.compareTo("-a") == 0) {
        		++i;
        		asFile = args[i];
        	} else if (s.compareTo("-o") == 0) {
        		++i;
        		oFile = args[i];
        	} else if (s.compareTo("-t") == 0) {
        		++i;
        		t = Integer.parseInt(args[i]);
        	}
        }
		// Server
		int serverSize = 11;
		//Server[] s = Server.getServers(serverSize);
		String sconfig = "serverConfig.txt";
		ArrayList<Server> s = Server.getServerFromConfig(sconfig);

		// Access
		// Data can't be cloned, since data's location changed during running
		Data[] d = Data.getDatas((Server[])s.toArray(new Server[s.size()]));
		// update
    	ArrayList<Event> e = new ArrayList<Event>(40000);
    	Update.getUpdate(d, e);
		// avoid the .svn folder
		// read access from disk
    	ArrayList<Access> a = new ArrayList<Access>(40000);
		Access.getAccess(d, a, asFile);
		
		//data crawler
		ArrayList<Crawler> crawlerList = new ArrayList<Crawler>(40000);
		Crawler.getCrawler(crawlerList);
		
		//Server accessTime change
		ArrayList<ServerChange> ServerChangeList = new ArrayList<ServerChange>(40000);
		ServerChange.getServerChange(ServerChangeList);
		//auto update server accesstime
		ArrayList<UpdateServerLatency> UpdateServerLatencyList = new ArrayList<UpdateServerLatency>(40000);
		UpdateServerLatency.getUpdateServerLatency(UpdateServerLatencyList);
		

		e.addAll(a);
		e.addAll(crawlerList);
		
		e.addAll(ServerChangeList);
		e.addAll(UpdateServerLatencyList);
		
		Collections.sort(e);
		ArrayList<User> u = User.addUser(a, uFile, "query.txt");

		//User[] u = User.getUsers();
		Writer output = new BufferedWriter(new FileWriter(new File(oFile)));

        Cache c = Cache.getCache(t);
        c.init(cacheSize, e, d, (Server[])s.toArray(new Server[s.size()]), output, u);
        c.run();
        System.out.println("finish!");
        System.out.println("inCacheFresh :"+Cache.inCacheFreshCount+" "+Cache.inCacheFreshCount/(Cache.inCacheFreshCount+Cache.inCacheStaleCount+Cache.notinCacheCount+0.0));
        System.out.println("inCacheStale :"+Cache.inCacheStaleCount+" "+Cache.inCacheStaleCount/(Cache.inCacheFreshCount+Cache.inCacheStaleCount+Cache.notinCacheCount+0.0));
        System.out.println("notInCache :"+Cache.notinCacheCount+" "+Cache.notinCacheCount/(Cache.inCacheFreshCount+Cache.inCacheStaleCount+Cache.notinCacheCount+0.0));
        
        System.out.println("Total Crawl Time: "+Crawler.totalCrawlTime);
        
        int totalQuery=0;
        int successQuery=0;
        for(int i=0;i<u.size();i++)
        {
        	totalQuery += u.get(i).totalQuery;
        	successQuery += u.get(i).successQuery;
        }
        System.out.println("totalQuery:   "+totalQuery);
        System.out.println("successQuery:   "+successQuery);
        System.out.println("SuccessRate:   "+successQuery/(double)totalQuery);
	}

}
