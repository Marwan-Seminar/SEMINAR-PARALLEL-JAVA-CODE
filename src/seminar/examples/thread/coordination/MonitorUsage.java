// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.coordination;

public class MonitorUsage {

	boolean condition = false;
	
	synchronized void myWaitingMethod() throws InterruptedException{
		while(!condition){
			this.wait();
		}
	}
		
	synchronized void myNotifyingMethod(){
		condition = true;
		this.notify();
	}
	
	public static void main(String[] args) throws InterruptedException{
		final MonitorUsage instance = new MonitorUsage();
		
		new Thread(){
			public void run(){
				System.out.println("Calling waiting method");
				try {
					instance.myWaitingMethod();
				} catch (InterruptedException e) {
					throw new Error(e);
				}
				System.out.println("Returned from waiting method");
			}
		}.start();
		
		Thread.sleep(100);
		
		new Thread(){
			public void run(){
				System.out.println("Calling notifying method");
				instance.myNotifyingMethod();
				System.out.println("Returned from notifying method");
			}
		}.start();
	}
}
