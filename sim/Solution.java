import java.util.LinkedList;


public class Solution {
	private int fresh;
	private int time;
	
	private double dataPrice;
	
	private LinkedList<Data> freshData;
	private LinkedList<Data> staleData;
	
	Solution(int f, int t) {
		fresh = f;
		time = t;
		freshData = new LinkedList<Data>();
		staleData = new LinkedList<Data>();
		dataPrice = 0.0;

	}
	
	Solution (int f, int t, Data d, boolean isStale, double dPrice) {
		fresh = f;
		time = t;
		freshData = new LinkedList<Data>();
		staleData = new LinkedList<Data>();
		if (isStale) {
			staleData.addFirst(d);
		} else {
			freshData.addFirst(d);
		}
		dataPrice = dPrice;
	}

	void AddFresh() {
		fresh++;
	}
	int getFresh() {
		return fresh;
	}
	void setTime(int t) {
		if (t > time) {
			time = t;
		}
	}
	int getTime() {
		return time;
	}
	
	double getDataPrice(){
		return dataPrice;
	}
	void setDataPrice(double price){
		dataPrice = price;
	}

	void addSolution(Solution s) {
		//fresh++;
		fresh += s.getFresh();
		
		setTime(s.time);
		freshData.addAll(s.freshData);
		staleData.addAll(s.staleData);
		this.setDataPrice(dataPrice + s.getDataPrice());
	}
	
    double tryPay(User u, float datalen) {
    	float stale = (datalen - fresh) / datalen;
    	return u.pay_linearPositive(time, stale);

    }
	double pay(User u, float datalen) {
    	float stale = (datalen - fresh) / datalen;
    	return u.pay(time, stale);

	}

//	void apply(Cache c) {
//		for (int i = 0; i< freshData.size(); ++i) {
//			//maybe in stale
//			Data data = freshData.get(i);
//			if(c.inCacheStale(data))
//				c.stale.remove(data);
//			
//			c.addToCache(data, false);
//		}
//		for (int i = 0; i< staleData.size(); ++i) {
//			Data data = staleData.get(i);
//			if (c.inCacheStale(data)) {
//				c.adjustCache(data, true);
//			}else if(c.inCacheFresh(data)){
//				
//			}else {
//			    c.addToCache(data, true);
//			}
//		}
//	}
	
	void apply(Cache c)
	{
		for (int i = 0; i< freshData.size(); ++i) 
		{
			Data data = freshData.get(i);
			if(c.inCacheFresh(data)||c.inCacheStale(data))
				break;
			c.addToCache(data, false);
		}
	}


    @SuppressWarnings("unchecked")
	Solution(int f, int t, LinkedList<Data> f_, LinkedList<Data> s_) {
		fresh = f;
		time = t;
		freshData = (LinkedList<Data>) f_.clone();
		staleData = (LinkedList<Data>) s_.clone();
    }

    public Solution clone() {
    	return new Solution(fresh, time, freshData, staleData);
    }

}
