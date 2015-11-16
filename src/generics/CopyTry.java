package generics;

import java.util.Arrays;

import network.analysis.Debug;
import util.basics.ArraysTK;

public class CopyTry implements Copyable<CopyTry>{

	int field = 1;
	
	@Override
	public CopyTry copy() {
		CopyTry ret = new CopyTry();
		ret.field = field +1;
		return ret;
	}

	public static void main(String[] args) {
		CopyTry a = new CopyTry();
		CopyTry b = a.copy();
        Debug.out(a.field);
        Debug.out(b.field);
		
		CopyTry[] q = new CopyTry[2];
        Debug.out(Arrays.toString(ArraysTK.deepCopy(q)));
	}
}

