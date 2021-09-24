package brownshome.unreasonableodds.gradle;

import java.util.Collections;
import java.util.List;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.process.CommandLineArgumentProvider;

/**
 * An argument provider that patches the given module name with files
 */
public abstract class GdxPatchArgumentProvider implements CommandLineArgumentProvider {
	/**
	 * The name of the module to patch
	 * @return a configurable file collection
	 */
	@Input
	public abstract Property<String> getModuleName();

	/**
	 * The files to patch the module with
	 * @return a configurable file collection
	 */
	@InputFiles
	public abstract ConfigurableFileCollection getPatchFiles();

	@Override
	public Iterable<String> asArguments() {
		if (!getPatchFiles().isEmpty()) {
			return List.of("--patch-module",
					"%s=%s".formatted(getModuleName().get(), getPatchFiles().getAsPath()));
		} else {
			return Collections.emptyList();
		}
	}
}
