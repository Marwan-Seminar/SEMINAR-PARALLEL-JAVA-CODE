// Copyright Marwan Abu-Khalil 2012

package  seminar.exercises.thread.parallel.hello_world_threads.solution;

/*
  Loesung zur Uebung: Hello-World mit Monitor:  
  
  Dieses Programm demonstriert die Benutzung der Monitor API am Beispiel zweier 
  Threads, die im stetigen Wechsel "Hello" und "World" auf die Konsole schreiben.
  
  Durch die Benutzung der Monitor-API wird sichergestellt, dass kein Busy-Waiting stattfindet.
*/
public class HelloWorldThreads_Solution_c_Monitor {

	String lastWord = WORLD;

	static final String HELLO = "Hello";
	static final String WORLD = "World";

	public static void main(String[] args) throws InterruptedException {

		HelloWorldThreads_Solution_c_Monitor instance = new HelloWorldThreads_Solution_c_Monitor();
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
					;
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
					// Now lastWord should not be WORLD
					System.out.println(WORLD);
					lastWord = WORLD;

					this.notify();
				}
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
