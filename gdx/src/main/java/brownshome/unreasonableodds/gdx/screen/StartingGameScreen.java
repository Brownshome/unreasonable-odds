package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class StartingGameScreen extends StageScreen {

	public StartingGameScreen(ApplicationResources resources) {
		super(resources);

		Label label = new Label("Starting game", resources.skin());
		label.setFillParent(true);
		label.setAlignment(Align.center);

		stage().addActor(label);
	}
}
