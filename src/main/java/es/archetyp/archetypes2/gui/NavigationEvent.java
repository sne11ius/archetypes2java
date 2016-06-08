package es.archetyp.archetypes2.gui;

public class NavigationEvent {

	private final String navigateTo;

	public NavigationEvent(final String navigateTo) {
		this.navigateTo = navigateTo;
	}

	public String getNavigateTo() {
		return navigateTo;
	}

}
