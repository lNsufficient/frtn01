import SimEnvironment.*;

// BeamRegul class to be written by you
public class BeamRegul extends Thread {
	// IO interface declarations
	private AnalogSource analogIn;
	private AnalogSink analogOut;
	private AnalogSink analogRef;

    private PI pi;
    private ReferenceGenerator refgen;

    //Define max and min:
    private double uMin = -10;
    private double uMax = 10;
	
	public BeamRegul(ReferenceGenerator refgen, Beam beam, int priority) {
		this.refgen = refgen;
        this.pi = new PI("PI:BeamRegul");
		// Code to initialize the IO
		analogIn = beam.getSource(0);
		analogOut = beam.getSink(0);
		analogRef = beam.getSink(1);
		//...
        this.setPriority(priority);
	}
    
    private double saturate(double u){
        if (u < uMin) {
            u = uMin;
        } else if (u > uMax) {
            u = uMax;
        }
        return u;
    }

	
	public void run() {
        long t = System.currentTimeMillis();
        while (true) {
            // ...
            // Code to perform IO
            double y = analogIn.get();
            double yref = refgen.getRef();
            synchronized (pi) {
                double u = saturate(pi.calculateOutput(y, yref));
                analogOut.set(u); //Why this here? Would be better not to sync it?
                pi.updateState(u);
            } 
                
            analogRef.set(yref);
            
            t = t + pi.getHMillis();
            long diff = t - System.currentTimeMillis();
            if (diff > 0) {
                try {
                    sleep(diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        }
	}
}
