package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.session.ClientLobbySession;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ClientLobbyScreen extends LobbyScreen {
	public ClientLobbyScreen(ApplicationResources resources, ClientLobbySession session) {
		super(resources, session);

		var readyButton = new TextButton("Ready", resources.skin());
		readyButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				session.localPlayer().ready(readyButton.isChecked());
				readyButton.setText(session.localPlayer().ready() ? "Un-Ready" : "Ready");
			}
		});

		mainSlot(readyButton).width(200.0f).height(80.0f);
	}
}
