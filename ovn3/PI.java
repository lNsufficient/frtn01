// PI class to be written by you
public class PI {
	// Current PI parameters
	private PIParameters p;

//    private double v; //Desired control signal
//    private double e; //Current control error
//    private double I; //Current integrator state
	
	// Constructor
	public PI(String name) {
        p = new PIParameters();
        p.K = 0.2;
        p.Ti = 1;
        p.Tr = 1;
        p.Beta = 1;
        p.H = 0.1;
        p.integratorOn = (Ti != 0);

        PIGUI piGUI = new PIGUI(this, p, name);

        //The following should be unneccesary since double is a primitive parameter
    }
	
	// Calculates the control signal v.
	// Called from BeamRegul.
	public synchronized double calculateOutput(double y, double yref) {
        
    }
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BeamRegul.
	public synchronized void updateState(double u);
	
	// Returns the sampling interval expressed as a long.
	// Note: Explicit type casting needed
	public synchronized long getHMillis();
	
	// Sets the PIParameters.
	// Called from PIGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIParameters newParameters);
}
