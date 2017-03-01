import SimEnvironment.*;

// BallAndBeamRegul class to be written by you
public class BallAndBeamRegul extends Thread {
     // Declarations
    private AnalogSource analogInAngle;
    private AnalogSource analogInPosition;
    private AnalogSink analogOut;
    private AnalogSink analogRef;
	
    private PI pi;
    private PID pid;

    private BallAndBeam bb;
    
    private ReferenceGenerator refgen;
    
    private double uMin, uMax;

    // Constructor
	public BallAndBeamRegul(ReferenceGenerator refgen, BallAndBeam bb, int priority) {
        this.refgen = refgen;
        this.pid = new PID("PID:outerLoop");
        this.pi = new PI("PI:innerLoop");
        this.bb = bb;
  
        // In Constructor
        analogInPosition = bb.getSource(0);
        analogInAngle = bb.getSource(1);
        analogOut = bb.getSink(0);
        analogRef = bb.getSink(1);  

        uMin = -10;
        uMax = 10;
        
        this.setPriority(priority);
    }

    private void setParameters() {
        double h = 0.1; //Should be 0.02 - 0.1! Should be the same in both!!!

        PIDParameters pidP = new PIDParameters();
        pidP.K = -0.015; //Should be between (-0.02) - (-0.01)
        pidP.Ti = 0; //Should be zero - no integrator 
        pidP.Tr = 0; //Should be between 
        pidP.Td = 2; //Should be between 0.5-4
        pidP.N = 7; //Should be between 5-10
        pidP.Beta = 1; //Should be between 1-0 I guess
        pidP.H = h; //Should be the same as in PI.java
        pidP.integratorOn = false; //Should be off. 


        PIParameters piP = new PIParameters();
        piP.K = 4; //Should be 0.5 - 10;
        piP.Ti = 0; //Should be zero - no integration!
        piP.Tr = 1;
        piP.Beta = 1; //I guess it should be between 0 - 1;
        piP.H = h; //d be 0.02 - 0.1. Most importantly the same as in PID.java
        piP.integratorOn = false;

        this.pid.setParameters(pidP);
        this.pi.setParameters(piP);
    }

    //DONE
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
        long diff = 0;
        double yPosition;
        double yAngle;
        double yRef; 
        //double[] currentState = new double[3];
        //double[] currentInput = new double[1];
        //double ballVelocityAlongBeam;
        long h = pi.getHMillis();
        double uOuter, uInner;
        /*currentState[0] = 0;
        currentState[1] = 0;
        currentState[2] = 0;
		*/

        while(true) { //i en period ska man göra båda kontrollerna. 
            yPosition = analogInPosition.get();
            yAngle = analogInAngle.get();
            yRef = refgen.getRef();

            
            /* Av nån anledning är inte detta lika bra....
            currentState[0] = yPosition;
            ballVelocityAlongBeam = (yPosition - y_old)/h;
            currentState[1] = ballVelocityAlongBeam;
            currentState[2] = yAngle;
            */

            synchronized(pid) {
                uOuter = pid.calculateOutput(yPosition, yRef);
                pid.updateState(uOuter);
            }

            synchronized(pi) {
                uInner = saturate(pi.calculateOutput(yAngle, uOuter));
                pi.updateState(uInner);
            }
            
            //urrentInput[0] = uInner;
            //currentState = bb.updateState(currentState, currentInput, h);
            



            analogRef.set(yRef);
            analogOut.set(uInner);
            
            //Get the periods right.
            t = t + h;
            diff = t - System.currentTimeMillis();
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
