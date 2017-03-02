import se.lth.control.*;

/** Main class used only for initialization */
public class Main {

  /** main method called when application starts */
  public static void main(String[] args) {
    Opcom opcom;
    Sinus sinus;

    opcom = new Opcom();
    sinus = new Sinus();

    // Symmetric reference exchange
    opcom.setSinus(sinus);
    sinus.setOpcom(opcom);

    // GUI initialization. OK to do from the main thread.
    opcom.initializeGUI();

    // Starting of the two threads
    opcom.start();
    sinus.start();
  }
}
            
