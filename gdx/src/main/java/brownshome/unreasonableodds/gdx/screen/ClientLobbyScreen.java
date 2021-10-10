package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.session.ClientSession;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ClientLobbyScreen extends LobbyScreen {
	public ClientLobbyScreen( ApplicationResources resources, ClientSession session) {
		super(resources, session);

		var readyButton = new TextButton("Ready", resources.skin());
		mainSlot(readyButton).width(200.0f).height(80.0f);
	}
}
