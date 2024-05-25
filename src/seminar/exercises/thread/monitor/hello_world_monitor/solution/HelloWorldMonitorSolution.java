// Copyright Marwan Abu-Khalil 2012

package seminar.exercises.thread.monitor.hello_world_monitor.solution;

/*
  Ü 2.5 Hello-World mit zwei Threads immer genau im Wechsel
  
  Schreiben Sie ein Programm mit zwei Threads. Ein Thread schreibt 
  fortwährend das Wort „Hello“ auf die Konsole, der andere fortwährend das Wort „World“.

  Stellen Sie folgendes sicher:
 	-	Die Wörter werden immer genau abwechselnd geschrieben.
 	-	Kein Busy-Waiting! (Wenn ein Thread nicht schreibt verbraucht er auch keine CPU-Zyklen)

 */
public class HelloWorldMonitorSolution {

	String lastWord = WORLD;

	static final String HELLO = "Hello";
	static final String WORLD = "World";

	public static void main(String[] args) throws InterruptedException {

		HelloWorldMonitorSolution instance = new HelloWorldMonitorSolution();
		instance.startThreads();
	}

	void printHelloLoop() {

		while (true) {
			synchronized (this) {
				while (lastWord == HELLO) {
					
					try {
						this.wait();
					} catch (InterruptedException e) {
						throw new Error(e);
					}
					
				}
				// now, lastWord should not be HELLO, i.e. it is WORLD

				System.out.println(HELLO);
				lastWord = HELLO;

				this.notify();
			}
		}
	}

	void printWorldLoop() {

		while (true) {
			synchronized (this) {
				while (lastWord == WORLD) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						throw new Error(e);
					}
				}
				
				// Now lastWord should not be HELLO
				System.out.println(WORLD);
				lastWord = WORLD;

				this.notify();
			}
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
