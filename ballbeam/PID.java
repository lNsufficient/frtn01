// PID class to be written by you
public class PID {
	// Current PID parameters
	private PIDParameters p;

    //D term:
    private double ad, bd, D;
    private double y, yold;

    /*integrator stuff
    private double I, e, v;
    */	

	// Constructor
	public PID(String name) {
        p = new PIDParameters();
        p.K = -0.015; //Should be between (-0.02) - (-0.01)
        p.Ti = 0; //Should be zero - no integrator 
        p.Tr = 0; //Should be between 
        p.Td = 2; //Should be between 0.5-4
        p.N = 7; //Should be between 5-10
        p.Beta = 1; //Should be between 1-0 I guess
        p.H = 0.1; //Should be the same asin PI.java, set to 0.1
        p.integratorOn = false; //Should be off. 
        
        System.out.println("\nWARNING: (Inside PID.java):"); 
        System.out.println("All integrator action is turned off (by commenting out) inside "+name +" controller.\n");
        
        this.ad = p.Td/(p.Td + p.N*p.H);
        this.bd = p.K*this.ad*p.N;

        PIDGUI pidGUI = new PIDGUI(this, p, name);
    }


	
	// Calculates the control signal v.
	// Called from BallAndBeamRegul.
	public synchronized double calculateOutput(double y, double yref) {
        this.y = y;
        //Note that there is no I term implemented
        this.D = ad*this.D - bd*(y - this.yold);//Would be more efficient not do declare this variable and just put it inside v?
        
        //this is only necessary if there is an integrator...should be removed I guess
        //for now I keep it here but commented out...
        /*
        this.e = yref-y; //This need to be saved to updateState
        this.v = p.K*(p.Beta*yref-y) + this.D + this.I; //First term P term
        return this.v;
        */
		//The following line was commented out in order to keep I part in this class.
        return p.K*(p.Beta*yref-y) + this.D;
    }
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BallAndBeamRegul.
	public synchronized void updateState(double u) {
        this.yold = this.y;
        
        /* This thing here should not be needed in this class for BallBeam
        however I will keep it here if it is needed for some reason...
        if (p.integratorOn) {
            this.I = this.I + p.K*p.H*this.e/p.Ti+ p.H*p.Tr*(u-this.v);
        } else {
            this.I = 0; //So the calculateOutput method will give correct answer.
        }
        */

    }
	
	// Returns the sampling interval expressed as a long.
	// Explicit type casting needed.
	public synchronized long getHMillis() {
        return (long)(p.H*1000); //Multiply seconds by 1000 to get milliseconds
    }
	
	// Sets the PIDParameters.
	// Called from PIDGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIDParameters newParameters) {
        p = (PIDParameters)newParameters.clone(); //Has to cast PIDParameters since return type is object.
		this.ad = p.Td/(p.Td + p.N*p.H);
        this.bd = p.K*this.ad*p.N;
    }
}
