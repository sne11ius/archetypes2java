package es.archetyp.archetypes2.backend.archetype.control;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "archetypes")
class MavenConfig {

	private List<String> mavenRepos = new ArrayList<>();

	private String mavenRootDir;

	private String mavenCommand;

	public List<String> getMavenRepos() {
		return mavenRepos;
	}

	public void setMavenRepos(final List<String> mavenRepos) {
		this.mavenRepos = mavenRepos;
	}

	public String getMavenRootDir() {
		return mavenRootDir;
	}

	public void setMavenRootDir(final String mavenRootDir) {
		this.mavenRootDir = mavenRootDir;
	}

	public String getMavenCommand() {
		return mavenCommand;
	}

	public void setMavenCommand(final String mavenCommand) {
		this.mavenCommand = mavenCommand;
	}

}
