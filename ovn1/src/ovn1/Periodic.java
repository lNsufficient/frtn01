package ovn1;

public class Periodic extends Thread{
	private int period;
	
	public static void main(String[] args){
		Periodic p = new Periodic(1000);
		p.run();
	}
	
	
	public Periodic(int period) {
		this.period = period;
	}
	  
	public void run() {
		System.out.print(period);
		System.out.print(", ");
	}

}