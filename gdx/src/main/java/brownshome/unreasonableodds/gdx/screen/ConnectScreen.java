package brownshome.unreasonableodds.gdx.screen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

import browngu.logging.Logger;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.gdx.*;
import brownshome.unreasonableodds.gdx.session.GdxLobbySession;
import brownshome.unreasonableodds.player.NetworkGamePlayer;
import brownshome.unreasonableodds.session.*;
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
							public void sessionLeft(SessionId sessionId) {
								// This must be the host leaving
								super.sessionLeft(sessionId);
								ui.nextScreen(new TopMenuScreen(resources));
							}

							@Override
							protected NetworkGameSession.Builder gameSessionBuilder(Map<InetSocketAddress, SessionId> sessionIds) {
								// TODO james.brown [19-10-2021] This is quite gross, maybe this needs a refactor
								return new NetworkGameSession.Builder(connectionManager(), rules(), sessionId(), sessionIds) {
									@Override
									protected NetworkGameSession build(UDPConnectionManager connectionManager,
									                                   Rules rules,
									                                   Map<Id, NetworkGamePlayer> players,
									                                   Map<InetSocketAddress, SessionId> sessionIds) {
										return new NetworkGameSession(connectionManager,
												rules,
												players,
												new HashMap<>(),
												sessionIds) {
											@Override
											public void startGame(Multiverse multiverse) {
												assert rules() instanceof GdxRules;

												ui.disposeSession(false);
												ui.nextScreen(new MultiverseScreen(resources, multiverse, localController()));
											}
										};
									}
								};
							}

							@Override
							public GdxCharacterController localController() {
								return ui.controller();
							}

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
