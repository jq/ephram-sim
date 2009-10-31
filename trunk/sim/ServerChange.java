import java.util.Date;
import java.util.List;

import webdb.Util;


public class ServerChange extends Event
{
	static final Date startTime = Util.toDate("19 Oct 2007 00:00");
	static final Date endTime = Util.toDate("29 Oct 2007 00:00");
	
	static long interval = 1800000;	//30min
	
	ServerChange(long time)
	{
		timestamp = time;
	}
	
	public void run(Cache c)
	{
		for(int i=0;i<c.s.length;i++)
		{
			int newTimeId = (int) ( Math.random()*5 );
			int newTime = c.s[i].getAccessTimes()[newTimeId];
			c.s[i].setAccessTime(newTime);
		}
	}
	
	static void getServerChange(List<ServerChange> ServerChangeList)
	{		
		for(Date ServerChangeDate = (Date) startTime.clone(); ServerChangeDate.getTime() < endTime.getTime(); ServerChangeDate.setTime(ServerChangeDate.getTime()+interval))
		{
			ServerChange c = new ServerChange(ServerChangeDate.getTime());
			ServerChangeList.add(c);
		}	
	}
}
