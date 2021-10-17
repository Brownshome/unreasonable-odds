package brownshome.unreasonableodds.gdx.screen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import browngu.logging.Logger;
import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.session.GdxLobbySession;
import brownshome.unreasonableodds.session.ClientLobbySession;
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
						class GdxClientLobbySession extends ClientLobbySession implements GdxLobbySession {
							final ClientLobbyScreen ui = new ClientLobbyScreen(resources, this);

							GdxClientLobbySession() throws IOException {
								super(name.getText(), new InetSocketAddress(address.getText(), Integer.decode(port.getText())), Gdx.app::postRunnable);
								markThreadAsSessionThread();
							}

							@Override
							public void onPlayersChanged() {
								super.onPlayersChanged();
								ui.players(players());
							}

							@Override
							public void sessionLeft(InetSocketAddress address) {
								// This must be the host leaving
								super.sessionLeft(address);
								ui.nextScreen(new TopMenuScreen(resources));
							}

							/*@Override
							public void startGame(Instant startTime, List<Entity> entities) {
								assert rules() instanceof GdxRules;

								var multiverse = rules().createMultiverse(entities, ui.player(), new MultiverseNetwork.Builder(this, rules()).build(), startTime, new Random());

								ui.disposeSession(false);
								ui.nextScreen(new MultiverseScreen(resources, multiverse, ui.player()));
							}*/

							@Override
							public ApplicationResources applicationResources() {
								return resources;
							}
						}
						var session = new GdxClientLobbySession();

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
