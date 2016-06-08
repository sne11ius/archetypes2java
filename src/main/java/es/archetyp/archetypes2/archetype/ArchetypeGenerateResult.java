package es.archetyp.archetypes2.archetype;

import java.util.List;

public class ArchetypeGenerateResult {

	private final int exitValue;
	private final String output;
	private final List<String> additionalProps;

	public ArchetypeGenerateResult(final int exitValue, final String output, final List<String> additionalProps) {
		this.exitValue = exitValue;
		this.output = output;
		this.additionalProps = additionalProps;
	}

	public int getExitValue() {
		return exitValue;
	}

	public String getOutput() {
		return output;
	}

	public List<String> getAdditionalProps() {
		return additionalProps;
	}

	public boolean isSuccess() {
		return exitValue == 0;
	}

}
