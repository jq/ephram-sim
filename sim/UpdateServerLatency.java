import java.util.Date;
import java.util.List;

import webdb.Util;


public class UpdateServerLatency extends Event
{
	static final Date startTime = Util.toDate("18 Oct 2007 23:49");
	static final Date endTime = Util.toDate("29 Oct 2007 00:00");
	
	static long interval = 600000;	//10min
	
	UpdateServerLatency(long time)
	{
		timestamp = time;
	}
	
	public void run(Cache c)
	{
		for(int i=0;i<c.s.length;i++)
		{
			c.s[i].setRecordAccessTime(c.s[i].getAccessTime());
		}
	}
	
	static void getUpdateServerLatency(List<UpdateServerLatency> UpdateServerLatencyList)
	{		
		for(Date updateDate = (Date) startTime.clone(); updateDate.getTime() < endTime.getTime(); updateDate.setTime(updateDate.getTime()+interval))
		{
			UpdateServerLatency c = new UpdateServerLatency(updateDate.getTime());
			UpdateServerLatencyList.add(c);
		}	
	}
}
