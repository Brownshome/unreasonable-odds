package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.session.HostSession;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class HostLobbyScreen extends LobbyScreen {
	public HostLobbyScreen(ApplicationResources resources, HostSession session) {
		super(resources, session);

		var startGameButton = new TextButton("Start Game", resources.skin());
		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				session.startGame(new GdxRules(resources))
						.thenAccept(multiverse -> {
							disposeSession(false);
							nextScreen(new MultiverseScreen(resources, multiverse, player()));
						});
			}
		});

		mainSlot(startGameButton).width(200.0f).height(80.0f);
	}
}
