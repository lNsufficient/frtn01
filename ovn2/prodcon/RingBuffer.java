public class RingBuffer {
    private final int bufSize;
    private int numel;
    private int readInd;
    private int writeInd;
    private Object[] elements;

    public RingBuffer(int bufSize){
        this.elements = new Object[bufSize];
        this.bufSize = bufSize;
        this.writeInd = 0;
        this.readInd = 0;
        this.numel = 0;
    }

    public synchronized Object get() {
		try {
			while (!Thread.interrupted() && numel == 0) {
				wait();
			}
            Object getOb = elements[readInd];
            elements[readInd] = null;
            readInd = (readInd + 1)%bufSize;
            numel--;
            notifyAll();
            return getOb;
		} catch (InterruptedException e) {
			// Requested to stop
            return null;
		}
    }

    public synchronized void put(Object o) {
        try {
            while (!Thread.interrupted() && numel == bufSize) {
                wait();
            }
            elements[writeInd] = o;
            writeInd = (writeInd + 1)%bufSize;
            numel++;
            notifyAll();
        } catch (InterruptedException e) {
            //Requested to stop
        }
    }
}
