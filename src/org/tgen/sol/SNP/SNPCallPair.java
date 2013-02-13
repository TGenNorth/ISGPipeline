package org.tgen.sol.SNP;

public class SNPCallPair implements Cloneable {

	public SNPCallPair (CallType one, CallType two) {
		call1 = one;
		call2 = two;
	}

	public CallType call1;
	public CallType call2;

	@Override
	public Object clone () {
		return new SNPCallPair(call1, call2);
	}

	@Override
	public int hashCode () {
		return 0;
	}

	@Override
	public boolean equals (Object obj) {
		return (call1.equals(((SNPCallPair) obj).call1) && call2.equals(((SNPCallPair) obj).call2));
	}

	@Override
	public String toString () {
		return call1.toString() + "/" + call2.toString();
	}
}
