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
	protected abstract ProjectLayout getLayout();

	public final TexturePacker.Settings settings = new TexturePacker.Settings();

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

		settings.scale = new float[] { 0.5f };
		settings.maxHeight = 4096;
		settings.maxWidth = 4096;
	}

	@TaskAction
	public void run() {
		getLogger().info("Running texture packer");

		TexturePacker.process(settings,
				getInputDir().get().getAsFile().getAbsolutePath(),
				getOutputDir().get().getAsFile().getAbsolutePath(),
				getPackFileName().get());
	}
}
