package brownshome.unreasonableodds.gradle;

import javax.inject.Inject;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.provider.*;
import org.gradle.api.tasks.*;

public abstract class PackTextureTask extends DefaultTask {
	@Inject
	protected abstract ProviderFactory getProviders();

	@Inject
	protected abstract ProjectLayout getLayout();

	public final Provider<TexturePacker.Settings> settings = getProviders().provider(TexturePacker.Settings::new);

	@Input
	public abstract Property<String> getPackFileName();

	@InputDirectory
	public abstract DirectoryProperty getInputDir();

	@OutputDirectory
	public abstract DirectoryProperty getOutputDir();

	public PackTextureTask() {
		setDescription("Packs textures into an atlas");
		setGroup(BasePlugin.BUILD_GROUP);

		getPackFileName().convention("packed-textures");
		getOutputDir().convention(getLayout().getBuildDirectory().dir(getName()));
		getInputDir().convention(getProject().getLayout().getProjectDirectory().dir("image").dir("unpacked"));
	}

	@TaskAction
	public void run() {
		getLogger().info("Running texture packer");

		TexturePacker.process(settings.get(),
				getInputDir().get().getAsFile().getAbsolutePath(),
				getOutputDir().get().getAsFile().getAbsolutePath(),
				getPackFileName().get());
	}
}
