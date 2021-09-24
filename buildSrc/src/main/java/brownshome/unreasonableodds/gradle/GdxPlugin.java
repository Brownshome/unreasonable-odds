package brownshome.unreasonableodds.gradle;

import java.util.List;
import java.util.Map;

import de.jjohannes.gradle.javamodules.ExtraModuleInfoPlugin;
import de.jjohannes.gradle.javamodules.ExtraModuleInfoPluginExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.*;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSetContainer;

public abstract class GdxPlugin implements Plugin<Project> {
	public static final String NATIVES_CONFIGURATION_NAME = "gdx-natives";
	public static final String ASSETS_SOURCE_SET_NAME = "assets";
	public static final String TEXTURE_PACKING_INPUT = "textures";

	@Override
	public void apply(Project project) {
		var gdx = project.getExtensions().create("gdx", GdxExtension.class);

		project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
			configureExtraModuleInfo(project, gdx);
			addRequiredDependencies(project, gdx);

			project.getExtensions().getByType(SourceSetContainer.class).register(ASSETS_SOURCE_SET_NAME);
			configurePackTextureTask(project);
			configureRunTask(project, gdx);
		});
	}

	private void configurePackTextureTask(Project project) {
		var packTextureTask = project.getTasks().register("packTextures", PackTextureTask.class);

		project.getExtensions().configure(SourceSetContainer.class, sourceSets -> {
			sourceSets.named(ASSETS_SOURCE_SET_NAME, sourceSet -> sourceSet.getResources().srcDir(packTextureTask.get().getOutputDir()));
		});
	}

	private void configureRunTask(Project project, GdxExtension gdx) {
		var assets = project.getExtensions().getByType(SourceSetContainer.class).named(ASSETS_SOURCE_SET_NAME);
		var gdxNatives = createNativeConfiguration(project, gdx);

		project.getTasks().named(ApplicationPlugin.TASK_RUN_NAME, JavaExec.class, run -> {
			var gdxPatchArg = project.getObjects().newInstance(GdxPatchArgumentProvider.class);
			gdxPatchArg.getModuleName().set("com.badlogicgames.gdx");
			gdxPatchArg.getPatchFiles().from(gdxNatives.get(), assets.get().getOutput());

			var gdxBackendPatchArg = project.getObjects().newInstance(GdxPatchArgumentProvider.class);
			gdxBackendPatchArg.getModuleName().set("com.badlogicgames.gdx.backend");
			gdxBackendPatchArg.getPatchFiles().from(gdxNatives.get());

			run.getJvmArgumentProviders().addAll(List.of(gdxPatchArg, gdxBackendPatchArg));
			run.getJvmArgs().addAll(List.of("--add-modules", "jdk.unsupported"));
		});
	}

	private Provider<Configuration> createNativeConfiguration(Project project, GdxExtension gdx) {
		return project.getConfigurations().register(NATIVES_CONFIGURATION_NAME, c -> {
			c.setVisible(false);
			c.setCanBeConsumed(false);
			c.setCanBeResolved(true);
			c.setDescription("Natives that need to be loaded by gdx");
			c.defaultDependencies(d -> {
				for (var nativeType : gdx.getLwjglNatives().get()) {
					d.add(project.getDependencies().create("org.lwjgl.lwjgl:lwjgl-platform:%s:natives-%s".formatted(gdx.getLwjglVersion().get(), nativeType)));
				}

				d.add(project.getDependencies().create("com.badlogicgames.gdx:gdx-platform:%s:natives-desktop".formatted(gdx.getVersion().get())));
			});
		});
	}

	private void addRequiredDependencies(Project project, GdxExtension gdx) {
		project.getConfigurations().named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, compile -> {
			compile.getDependencies().addLater(gdx.getVersion().map(version -> project.getDependencies().create("com.badlogicgames.gdx:gdx:%s".formatted(version))));
			compile.getDependencies().addLater(gdx.getVersion().map(version -> project.getDependencies().create("com.badlogicgames.gdx:gdx-backend-lwjgl:%s".formatted(version))));
		});
	}

	private void configureExtraModuleInfo(Project project, GdxExtension gdx) {
		project.getPlugins().apply(ExtraModuleInfoPlugin.class);
		project.getExtensions().configure(ExtraModuleInfoPluginExtension.class, extraModuleInfo -> {
			extraModuleInfo.getFailOnMissingModuleInfo().set(false);

			extraModuleInfo.getAutomaticModules().putAll(
					gdx.getVersion().zip(gdx.getGdxModuleName(),
							(version, moduleName) -> Map.of("gdx-%s.jar".formatted(version), moduleName)));

			extraModuleInfo.getAutomaticModules().putAll(
					gdx.getVersion().zip(gdx.getGdxBackendModuleName(),
							(version, moduleName) -> Map.of("gdx-backend-lwjgl-%s.jar".formatted(version), moduleName)));
		});
	}
}

