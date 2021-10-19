package brownshome.unreasonableodds.gdx.screen;

import java.util.Random;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.session.HostLobbySession;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class HostLobbyScreen extends LobbyScreen {
	public HostLobbyScreen(ApplicationResources resources, HostLobbySession session) {
		super(resources, session);

		var startGameButton = new TextButton("Start Game", resources.skin());
		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				session.startGame(new Random());
			}
		});

		mainSlot(startGameButton).width(200.0f).height(80.0f);
	}
}
