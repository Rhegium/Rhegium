package org.rhegium.api.modules;

public class IllegalCyclicDepedency extends RuntimeException {

	private static final long serialVersionUID = -5186279868116796470L;

	private final String cyclic1;
	private final String cyclic2;

	public IllegalCyclicDepedency(final String cyclic1, final String cyclic2) {
		this.cyclic1 = cyclic1;
		this.cyclic2 = cyclic2;
	}

	@Override
	public String getMessage() {
		return new StringBuilder("Illegal cycle detected (").append(cyclic1).append(" => ").append(cyclic2)
				.append(" => ").append(cyclic1).append(")").toString();
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

}
