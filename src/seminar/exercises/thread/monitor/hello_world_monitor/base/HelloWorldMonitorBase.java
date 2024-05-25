// Copyright Marwan Abu-Khalil 2019

package seminar.exercises.thread.monitor.hello_world_monitor.base;

/*
  Ü 2.5 Hello-World mit zwei Threads immer genau im Wechsel
  
  Schreiben Sie ein Programm mit zwei Threads. Ein Thread schreibt 
  fortwährend das Wort „Hello“ auf die Konsole, der andere fortwährend das Wort „World“.

  Stellen Sie folgendes sicher:
 	-	Die Wörter werden immer genau abwechselnd geschrieben.
 	-	Kein Busy-Waiting! (Wenn ein Thread nicht schreibt verbraucht er auch keine CPU-Zyklen)

 */
public class HelloWorldMonitorBase {

	volatile String lastWord = WORLD;

	static final String HELLO = "Hello";
	static final String WORLD = "World";

	public static void main(String[] args) throws InterruptedException {

		HelloWorldMonitorBase instance = new HelloWorldMonitorBase();
		instance.startThreads();
	}

	void printHelloLoop() {

		while (true) {
			 
			while (lastWord == HELLO) {
				// busy waiting
			}
			// now, lastWord should not be HELLO, i.e. it is WORLD

			System.out.println(HELLO);
			lastWord = HELLO;
			
		}
	}

	void printWorldLoop() {

		while (true) {
			
			while (lastWord == WORLD) {
				// busy waiting
			}
				
			// Now lastWord should not be HELLO
			System.out.println(WORLD);
				
			lastWord = WORLD;
		}
	}

	void startThreads() throws InterruptedException {
		Thread worldThread = new Thread() {
			public void run() {
				printWorldLoop();
			}
		};
		Thread helloThread = new Thread() {
			public void run() {
				printHelloLoop();
			}
		};

		worldThread.start();
		helloThread.start();

		worldThread.join();
		helloThread.join();
	}
}
