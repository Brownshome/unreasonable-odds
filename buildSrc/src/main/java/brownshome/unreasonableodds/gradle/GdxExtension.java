package brownshome.unreasonableodds.gradle;

import java.util.List;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * An extension for configuring the gdx plugin
 */
public abstract class GdxExtension {
	/**
	 * The Gdx version to use. This property must be set, as it has no default
	 * @return a string property
	 */
	public abstract Property<String> getVersion();

	/**
	 * The module name to use for the generated gdx modules. This defaults to <code>"com.badlogicgames.gdx"</code>
	 * @return a string property
	 */
	public abstract Property<String> getGdxModuleName();

	/**
	 * The module name to use for the backend gdx module. This defaults to <code>"${gdxModuleName}.backend"</code>
	 * @return a string property
	 */
	public abstract Property<String> getGdxBackendModuleName();

	/**
	 * The LWJGL version to use. This defaults to <code>"2.9.3"</code>
	 * @return a string property
	 */
	public abstract Property<String> getLwjglVersion();

	/**
	 * The list of LWJGL native types to package. Defaults to <code>[ "windows", "linux", "osx" ]</code>
	 * @return a string list property
	 */
	public abstract ListProperty<String> getLwjglNatives();

	public GdxExtension() {
		getGdxModuleName().convention("com.badlogicgames.gdx");
		getGdxBackendModuleName().convention(getGdxModuleName().map(s -> s + ".backend"));
		getLwjglVersion().convention("2.9.3");
		getLwjglNatives().convention(List.of("windows", "linux", "osx"));
	}
}
