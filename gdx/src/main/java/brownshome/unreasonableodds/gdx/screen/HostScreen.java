package brownshome.unreasonableodds.gdx.screen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.session.HostSession;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HostScreen extends StageScreen {
	private static final System.Logger LOGGER = System.getLogger(HostScreen.class.getModule().getName());

	public HostScreen(ApplicationResources resources) {
		super(resources);

		var table = new Table(resources.skin());
		table.setFillParent(true);
		stage().addActor(table);

		TextButton host = new TextButton("Host", resources.skin());

		String defaultPort = "";

		try (var socket = new ServerSocket(0)) {
			defaultPort = Integer.toString(socket.getLocalPort());
		} catch (IOException ignored) { }

		TextField port = new TextField(defaultPort, resources.skin());
		port.setMessageText("Enter port number");
		port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

		var name = new TextField("", resources.skin());

		host.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int p = Integer.decode(port.getText());
				try {
					var session = new HostSession(name.getText(), p, Gdx.app::postRunnable) {
						final HostLobbyScreen ui = new HostLobbyScreen(resources, this);

						{
							markThreadAsSessionThread();
						}

						@Override
						public void setPlayerName(InetSocketAddress address, String name) {
							super.setPlayerName(address, name);
							ui.names(players());
						}

						@Override
						public void name(String name) {
							super.name(name);
							ui.names(players());
						}
					};

					nextScreen(session.ui);
				} catch(IOException e) {
					LOGGER.log(System.Logger.Level.ERROR, "Unable to host game", e);
					nextScreen(new ErrorScreen(e, resources));
				}
			}
		});

		table.defaults().height(80.0f).spaceBottom(10.0f).minWidth(200.0f).fill();

		table.add("Port: ");
		table.add(port);

		table.row();
		table.add("Name: ");
		table.add(name);

		table.row();
		table.add(host).colspan(2);
	}
}
