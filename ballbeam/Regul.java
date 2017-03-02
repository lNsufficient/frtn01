import se.lth.control.DoublePoint;
import se.lth.control.realtime.AnalogIn;
import se.lth.control.realtime.AnalogOut;
import se.lth.control.realtime.IOChannelException;
import se.lth.control.realtime.Semaphore;

public class Regul extends Thread {
	public static final int OFF = 0;
	public static final int BEAM = 1;
	public static final int BALL = 2;
	
    private int uMin = -10, uMax = 10;
	private PI inner = new PI("PI");
	private PID outer = new PID("PID");
	
	private AnalogIn analogInAngle; 
	private AnalogIn analogInPosition; 
	private AnalogOut analogOut;
	
	private ReferenceGenerator referenceGenerator;
	private OpCom opcom;
	
	private int priority;
	private boolean WeShouldRun = true;
	private long starttime;
	private Semaphore mutex; // used for synchronization at shut-down
	
	private ModeMonitor modeMon;
	
	// Inner monitor class
	class ModeMonitor {
		private int mode;
		
		// Synchronized access methods
		public synchronized void setMode(int newMode) {
			mode = newMode;
			inner.reset();
			outer.reset();
		}
		
		public synchronized int getMode() {
			return mode;
		}
	}
	
	public Regul(int pri) {
		priority = pri;
		mutex = new Semaphore(1);
		try {
			analogInAngle = new AnalogIn(0);
			analogInPosition = new AnalogIn(1);
			analogOut = new AnalogOut(0);
		} catch (IOChannelException e) { 
			System.out.print("Error: IOChannelException: ");
			System.out.println(e.getMessage());
		}
		modeMon = new ModeMonitor();
	}
	
	public void setOpCom(OpCom opcom) {
        this.opcom = opcom;
	}
	
	public void setRefGen(ReferenceGenerator referenceGenerator){
        this.referenceGenerator = referenceGenerator; 
	}
	
	// Called in every sample in order to send plot data to OpCom
	private void sendDataToOpCom(double yref, double y, double u) {
		double x = (double)(System.currentTimeMillis() - starttime) / 1000.0;
		DoublePoint dp = new DoublePoint(x,u);
		PlotData pd = new PlotData(x,yref,y);
		opcom.putControlDataPoint(dp);
		opcom.putMeasurementDataPoint(pd);
	}
	
	public synchronized void setInnerParameters(PIParameters p) {
        inner.setParameters(p);
	}
	
	public synchronized PIParameters getInnerParameters() {
		return inner.getParameters();
		// Written by you
	}
	
	public synchronized void setOuterParameters(PIDParameters p) {
        outer.setParameters(p);
	}
	
	public synchronized PIDParameters getOuterParameters(){
		return outer.getParameters();
	}
	
	public void setOFFMode(){
		modeMon.setMode(OFF);
	}
	
	public void setBEAMMode(){
		modeMon.setMode(BEAM);
	}
	
	public void setBALLMode(){
		modeMon.setMode(BALL);
	}
	
	public int getMode(){
        return modeMon.getMode();
	}
	
	// Called from OpCom when shutting down
	public synchronized void shutDown() {
		WeShouldRun = false;
		mutex.take();
		try {
			analogOut.set(0.0);
		} catch (IOChannelException x) {
		}
	}
	
	private double limit(double v, double min, double max) {
		if (v < min) {
			v = min;
		} else if (v > max) {
			v = max;
		}
		return v;
	}
	
	public void run() {
		long duration;
		long t = System.currentTimeMillis();
		starttime = t;
		double y, yref, u, yAngle=0, yAngleRef, uold, phiff;
		setPriority(priority);
		mutex.take();
		while (WeShouldRun) {
            y = 0;
            yref = 0;
            u = 0;
			switch (modeMon.getMode()) {
			case OFF: {
                try {
                    analogOut.set(u);
                } catch (Exception e) {
                    System.out.println(e);
                }
				// Code for the OFF mode. 
				// Written by you.
				

                // Should include resetting the controllers
				
				//inner.reset();
                //outer.reset();
                //
				// Should include a call to sendDataToOpCom  
				break;
			}
			case BEAM: {
                yref = referenceGenerator.getRef();
                try {
                    y = analogInAngle.get();
                } catch (Exception e) {
                    System.out.println(e);
                }
                synchronized (inner) {
                    u = limit(inner.calculateOutput(y, yref) + referenceGenerator.getUff(), uMin, uMax);
                    try {
                        analogOut.set(u);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    inner.updateState(u);
                }
				// Code for the BEAM mode
				// Written by you.
				// Should include a call to sendDataToOpCom
				break;
			}
			case BALL: {
                yref = referenceGenerator.getRef();
                try {
                    y = analogInPosition.get();
                    yAngle = analogInAngle.get();
                } catch (Exception e) {
                    System.out.println(e);
                }
                synchronized (outer) {
                    yAngleRef = outer.calculateOutput(y, yref);
                    synchronized (inner) {
                        phiff = referenceGenerator.getPhiff();
                        yAngleRef = yAngleRef + phiff;
                        yAngleRef = limit(yAngleRef, uMin, uMax);
                        u = inner.calculateOutput(yAngle, yAngleRef);
                        double uff = referenceGenerator.getUff();
                        uold = u+uff;

                        u = limit(uold, uMin, uMax);
                        try {
                            analogOut.set(u);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        inner.updateState(u-uff);
                    }
                    if (uold == u) {
                       outer.updateState(yAngleRef-phiff);
                    } else { 
                        outer.updateState(yAngle-phiff);
                    }
                }
				// Code for the BALL mode
				// Written by you.
				// Should include a call to sendDataToOpCom 
				break;
			}
			default: {
				System.out.println("Error: Illegal mode.");
				break;
			}
			}

           sendDataToOpCom(yref, y, u);

            
			
			// sleep
			t = t + inner.getHMillis();
			duration = t - System.currentTimeMillis();
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (InterruptedException x) {
				}
			}
		}
		mutex.give();
	}
}
