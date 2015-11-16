package probeersels;

import java.io.PrintStream;
import java.lang.reflect.Method;

import network.analysis.Debug;

public class CallWithoutCalling {
    @SuppressWarnings("serial")
	public static class StrangeException extends RuntimeException {
        @Override
        public void printStackTrace(PrintStream s) {
            for (Method m : CallWithoutCalling.class.getMethods()) {
                if ("main".equals(m.getName())) continue;
                try {
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void secretMethodNotCalledInMain() {
        Debug.out("Congratulations, you won a million dollars!");
    }

    public static void main(String[] args) {
        throw new StrangeException();
    }
}
