// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.api;

// 100%-utilization of 4-Core CPU 
public class MyBusyThread extends Thread {

	

	public void run() {int data = 2;
		//while (true) {
			//data = data * data;
			//Thread.yield();
		//}
	}

	public static void main(String[] args) throws InterruptedException {
		for (int core = 0; core < 4; ++core) {
			new MyBusyThread().start();
			Thread.sleep(20000);
		}
	}
}
