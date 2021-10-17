package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.GdxCharacterController;
import brownshome.unreasonableodds.player.LobbyPlayer;
import brownshome.unreasonableodds.session.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class LobbyScreen extends StageScreen {
	private final LocalPlayerLobbySession session;
	private final List<LobbyPlayer> playerList;
	private final Table rightPanel;
	private final GdxCharacterController player;

	private boolean disposeSession = true;

	public LobbyScreen(ApplicationResources resources, LocalPlayerLobbySession session) {
		super(resources);

		this.session = session;
		this.player = new GdxCharacterController();

		var root = new Table(resources.skin());
		root.setFillParent(true);
		stage().addActor(root);

		playerList = new List<>(resources.skin());
		playerList.setItems(session.players().toArray(LobbyPlayer[]::new));

		root.row().expand();

		rightPanel = new Table(resources.skin());
		rightPanel.add((Actor) null).pad(Value.percentWidth(0.1f)).colspan(2).spaceBottom(10.0f);

		var nameField = new TextField(session.localPlayer().name(), resources.skin());
		nameField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode != Input.Keys.ENTER && keycode != Input.Keys.NUMPAD_ENTER) {
					return false;
				}

				var newName = nameField.getText();

				name(newName);
				nameField.setText(session.localPlayer().name());

				stage().setKeyboardFocus(null);
				return true;
			}
		});

		var leaveButton = new TextButton("Leave", resources.skin());
		leaveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (leaveButton.isPressed()) {
					nextScreen(new TopMenuScreen(resources));
				}
			}
		});

		rightPanel.row();
		rightPanel.add(nameField, leaveButton);

		root.add(new ScrollPane(playerList, resources.skin()), rightPanel);
	}

	@SuppressWarnings("unchecked")
	protected final <T extends Actor> Cell<T> mainSlot(T actor) {
		var cell = (Cell<T>) rightPanel.getCells().first();
		cell.setActor(actor);
		return cell;
	}

	protected final void players(java.util.Collection<? extends LobbyPlayer> players) {
		playerList.setItems(players.toArray(LobbyPlayer[]::new));
	}

	protected final GdxCharacterController player() {
		return player;
	}

	/**
	 * Called when the user changes their name.
	 * @param newName the new name
	 */
	protected void name(String newName) {
		session.localPlayer().name(newName);
	}

	protected final LocalPlayerLobbySession session() {
		return session;
	}

	protected final void disposeSession(boolean shouldDispose) {
		disposeSession = shouldDispose;
	}

	@Override
	public void dispose() {
		if (disposeSession) {
			((NetworkSession) session).close();
		}
	}
}
