// PI class to be written by you
public class PI {
	// Current PI parameters
	private PIParameters p;

    private double v; //Desired control signal
    private double e; //Current control error
    private double I; //Current integrator state
	
	// Constructor
	public PI(String name) {
        p = new PIParameters();
        p.K = 0.2;
        p.Ti = 0;
        p.Tr = 1;
        p.Beta = 1;
        p.H = 0.1;
        p.integratorOn = (p.Ti != 0);


        PIGUI piGUI = new PIGUI(this, p, name);

        //The following should be unneccesary since double is a primitive parameter
        this.I = 0;
        this.e = 0;
        this.v = 0;
    }
	
	// Calculates the control signal v.
	// Called from BeamRegul.
	public synchronized double calculateOutput(double y, double yref) {
        this.e = yref-y; //This needs to be saved to updateState; //save yref and -y instead?
        this.v = p.K*(p.Beta*yref-y)+this.I;
        return this.v;
    }
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BeamRegul.
	public synchronized void updateState(double u) {
        if (p.integratorOn) {
            this.I = this.I + p.K*p.H*this.e/p.Ti+ p.H*p.Tr*(u-this.v);
        } else {
            this.I = 0; //So the calculateOutput method will give correct answer.
        }
    }
	
	// Returns the sampling interval expressed as a long.
	// Note: Explicit type casting needed
	public synchronized long getHMillis() {
        return (long)(p.H*1000);//Multiply by 1000 to transfer s to ms.
    }
	
	// Sets the PIParameters.
	// Called from PIGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIParameters newParameters) {
        p = (PIParameters)newParameters.clone(); //Has to cast PIParameters since return type is object. 
    }
}
