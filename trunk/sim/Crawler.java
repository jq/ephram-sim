import java.util.Date;
import java.util.List;

import webdb.Util;

public class Crawler extends Event
{
	static final Date startTime = Util.toDate("19 Oct 2007 00:00");
	static final Date endTime = Util.toDate("29 Oct 2007 00:00");
	
	static long interval = 1800000;	//30min
	
	
	Crawler(long time)
	{
		timestamp = time;
	}
	
	public void run(Cache c)
	{	
		
		for(int i=0;i<c.stale.size();i++)
		{
			Data d = c.stale.get(i);
			d.cacheUnappliedUpdate = 0;
			c.stale.remove(d);
			c.fresh.add(d);
		}	
		
		//problem 2B: Select a copy to refresh cache
//		for(int i=0;i<c.stale.size();i++)
//		{
//			Data d = c.stale.get(i);
//			
//		}
	}
	
	static void getCrawler(List<Crawler> crawlerList)
	{		
		for(Date crawlDate = (Date) startTime.clone(); crawlDate.getTime() < endTime.getTime(); crawlDate.setTime(crawlDate.getTime()+interval))
		{
			Crawler c = new Crawler(crawlDate.getTime());
			crawlerList.add(c);
		}	
	}

}
