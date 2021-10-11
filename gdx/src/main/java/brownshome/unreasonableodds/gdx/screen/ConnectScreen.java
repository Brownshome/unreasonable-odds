package brownshome.unreasonableodds.gdx.screen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import browngu.logging.Logger;
import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.session.ClientSession;
import brownshome.unreasonableodds.session.SessionPlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ConnectScreen extends StageScreen {
	public ConnectScreen(ApplicationResources resources) {
		super(resources);

		var table = new Table(resources.skin());
		table.setFillParent(true);
		stage().addActor(table);

		TextButton connect = new TextButton("Connect", resources.skin());
		TextField name = new TextField("", resources.skin());

		TextField address = new TextField(InetAddress.getLoopbackAddress().getCanonicalHostName(), resources.skin());
		TextField port = new TextField("", resources.skin());
		port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

		connect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (connect.isPressed()) {
					try {
						var session = new ClientSession(name.getText(), new InetSocketAddress(address.getText(), Integer.decode(port.getText())), Gdx.app::postRunnable) {
							final ClientLobbyScreen ui = new ClientLobbyScreen(resources, this);

							{ markThreadAsSessionThread(); }

							@Override
							public void players(List<SessionPlayer> players) {
								super.players(players);
								ui.players(players);
							}

							@Override
							public void hostLeft() {
								super.hostLeft();
								ui.nextScreen(new TopMenuScreen(resources));
							}
						};

						nextScreen(session.ui);
					} catch (IOException | NumberFormatException | SecurityException e) {
						Logger.logger().log(e, "Update to create client connection");
						nextScreen(new ErrorScreen(e, resources));
					}
				}
			}
		});

		table.defaults().height(80.0f).width(200.0f);
		table.add(name, connect);
		table.row().spaceTop(10.0f);
		table.add(address, port);
	}
}
