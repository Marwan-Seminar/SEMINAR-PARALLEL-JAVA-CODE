// Copyright Marwan Abu-Khalil 2012

package seminar.examples.thread.synchronisation;

// Simple example for inconsistencies
// due to lack of synchronization
public class AccountInconsistent {

	int balance;
	
	void addAmount(int amount){
		int newBalance = balance + amount;
		this.balance = newBalance;
	}
}
