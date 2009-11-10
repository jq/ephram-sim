import java.util.Date;
import java.util.List;

import webdb.Util;

public class Crawler extends Event
{
	static final Date startTime = Util.toDate("19 Oct 2007 00:00");
	static final Date endTime = Util.toDate("29 Oct 2007 00:00");
	
	static long interval = 1800000;	//30min
	
	static double relDeadline = 90000;//qod metric
	static double beta = 0.1;
	
	static int totalCrawlTime = 0;
	
	Crawler(long time)
	{
		timestamp = time;
	}
	
	public void run(Cache c)
	{	
		
		/**
		 *  problem 2B: Select a copy to refresh cache
		 *  algo1: beta*Qos+（1-beta）*Qod
		 */
		for(int i=0;i<c.cacheItems.size();i++)
		{
			Data d = c.cacheItems.get(i);
			//fresh data
			if(d.cacheUnappliedUpdate==0)
				continue;
			
			int selectedServerAccTime = d.src.getRecordAccessTime();
			double serverQod = 1.0;
			double serverQos = d.src.getRecordAccessTime()>relDeadline? 0.0:(relDeadline-d.src.getRecordAccessTime())/relDeadline;
			double serverQosQod = serverQod*beta + serverQos*(1-beta);
			//which replica to select;-1 means src
			int replicaNumber = -1;
			double[] replicasQod = new double[Data.replicaNum];
			double[] replicasQos = new double[Data.replicaNum];
			double[] replicasQosQod = new double[Data.replicaNum];
			for(int j=0;j<Data.replicaNum;j++)
			{
				replicasQod[j] = d.getUnappliedUpdates()[j]>10? 0.0:(10-d.getUnappliedUpdates()[j])/10.0;
				replicasQos[j] = d.replicas.get(j).getRecordAccessTime()>relDeadline? 0.0:(relDeadline-d.replicas.get(j).getRecordAccessTime())/relDeadline;		
				replicasQosQod[j] = replicasQod[j]*beta + replicasQos[j]*(1-beta);
				if(replicasQosQod[j]>serverQosQod)
				{
					serverQosQod = replicasQosQod[j];
					replicaNumber = j;
					//记录更新服务器的access time
					selectedServerAccTime = d.replicas.get(replicaNumber).getRecordAccessTime();
					totalCrawlTime += selectedServerAccTime;
				}
			}
			if(replicaNumber == -1)
			{
				d.cacheUnappliedUpdate = 0;
				System.out.println("Crawl from src.............................");
			}
			else//refresh 
			{
				d.cacheUnappliedUpdate = d.getUnappliedUpdates()[replicaNumber]<d.cacheUnappliedUpdate? d.getUnappliedUpdates()[replicaNumber]:d.cacheUnappliedUpdate;
				System.out.println("Crawl from other copy.............................................");
			}		
		}
		
		/**
		 * problem 2B: Select a copy to refresh cache
		 * algo2: select source server
		 */
//		for(int i=0;i<c.cacheItems.size();i++)
//		{
//			Data d = c.cacheItems.get(i);
//			//fresh data
//			if(d.cacheUnappliedUpdate==0)
//				continue;
//			d.cacheUnappliedUpdate = 0;
//			
//			totalCrawlTime += d.src.accessTime;
//		}
		
		/**
		 * problem 2B: Select a copy to refresh cache
		 * baseline algo3: maximize Qos
		 */
//		for(int i=0;i<c.cacheItems.size();i++)
//		{
//			Data d = c.cacheItems.get(i);
//			//skip fresh data
//			if(d.cacheUnappliedUpdate==0)
//				continue;
//			int selectedServerAccTime = d.src.getRecordAccessTime();
//			double serverQos = d.src.getRecordAccessTime()>relDeadline? 0.0:(relDeadline-d.src.getRecordAccessTime())/relDeadline;
//			//which replica to select;-1 means src
//			int replicaNumber = -1;
//			double[] replicasQos = new double[Data.replicaNum];
//			for(int j=0;j<Data.replicaNum;j++)
//			{
//				replicasQos[j] = d.replicas.get(j).getRecordAccessTime()>relDeadline? 0.0:(relDeadline-d.replicas.get(j).getRecordAccessTime())/relDeadline;		
//				if(replicasQos[j]<serverQos)
//				{
//					serverQos = replicasQos[j];
//					replicaNumber = j;
//					//记录更新服务器的access time
//					selectedServerAccTime = d.replicas.get(replicaNumber).getRecordAccessTime();
//					totalCrawlTime += selectedServerAccTime;
//				}
//			}
//			if(replicaNumber == -1)
//			{
//				d.cacheUnappliedUpdate = 0;
//				System.out.println("Crawl from src.............................");
//			}
//			else//refresh 
//			{
//				d.cacheUnappliedUpdate = d.getUnappliedUpdates()[replicaNumber]<d.cacheUnappliedUpdate? d.getUnappliedUpdates()[replicaNumber]:d.cacheUnappliedUpdate;
//				System.out.println("Crawl from other copy.............................................");
//			}
//		}
		
		/**
		 * problem 2B: Select a copy to refresh cache
		 * baseline algo4: maximize Qod
		 */
//		for(int i=0;i<c.cacheItems.size();i++)
//		{
//			Data d = c.cacheItems.get(i);
//			//skip fresh data
//			if(d.cacheUnappliedUpdate==0)
//				continue;
//			int selectedServerAccTime = d.src.getRecordAccessTime();
//			double serverQod = 1.0;
//			double serverQos = d.src.getRecordAccessTime()>relDeadline? 0.0:(relDeadline-d.src.getRecordAccessTime())/relDeadline;
//			//which replica to select;-1 means src
//			int replicaNumber = -1;
//			double[] replicasQod = new double[Data.replicaNum];
//			double[] replicasQos = new double[Data.replicaNum];
//			for(int j=0;j<Data.replicaNum;j++)
//			{
//				replicasQod[j] = d.getUnappliedUpdates()[j]>10? 0.0:(10-d.getUnappliedUpdates()[j])/10.0;
//				replicasQos[j] = d.replicas.get(j).getRecordAccessTime()>relDeadline? 0.0:(relDeadline-d.replicas.get(j).getRecordAccessTime())/relDeadline;		
//				if(replicasQod[j]>=serverQod && replicasQos[j]<serverQos)
//				{
//					serverQod = replicasQod[j];
//					replicaNumber = j;
//					//记录更新服务器的access time
//					selectedServerAccTime = d.replicas.get(replicaNumber).getRecordAccessTime();
//					totalCrawlTime += selectedServerAccTime;
//				}
//			}
//			if(replicaNumber == -1)
//			{
//				d.cacheUnappliedUpdate = 0;
//				System.out.println("Crawl from src.............................");
//			}
//			else//refresh 
//			{
//				d.cacheUnappliedUpdate = d.getUnappliedUpdates()[replicaNumber]<d.cacheUnappliedUpdate? d.getUnappliedUpdates()[replicaNumber]:d.cacheUnappliedUpdate;
//				System.out.println("Crawl from other copy.............................................");
//			}		
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
