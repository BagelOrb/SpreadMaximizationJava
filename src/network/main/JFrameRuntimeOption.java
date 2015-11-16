package network.main;

import javax.swing.JOptionPane;

import network.analysis.Debug;

public class JFrameRuntimeOption {

	public Integer answer = null;
	final String msg;
	final int jOptionPaneOption;
	
	public JFrameRuntimeOption(String msg2, int optionType) {
		msg = msg2;
		jOptionPaneOption = optionType;
	}
	
	public int getAnser() {
		if (answer==null) 
			answer = JOptionPane.showOptionDialog
			(
	                JFrameExperiment.frame,
	                "Complete the sentence:\n"
	                + "\"Green eggs and...\"",
	                "Customized Dialog",
	                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		return answer.intValue();
	}
	public static void test() {
		JFrameExperiment.run();

		JFrameRuntimeOption o = new JFrameRuntimeOption("and?", JOptionPane.YES_NO_OPTION);
        Debug.out(o.getAnser());
        Debug.out(o.getAnser());
        Debug.out(o.getAnser());
	}
}
