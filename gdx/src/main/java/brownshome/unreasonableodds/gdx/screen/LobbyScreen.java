package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.GdxPlayer;
import brownshome.unreasonableodds.network.Session;

import brownshome.unreasonableodds.network.SessionPlayer;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class LobbyScreen extends StageScreen {
	private final Session session;
	private final List<SessionPlayer> playerList;
	private final Table rightPanel;
	private final GdxPlayer player;

	private boolean disposeSession = true;

	public LobbyScreen(ApplicationResources resources, Session session) {
		super(resources);

		this.session = session;
		this.player = new GdxPlayer();

		var root = new Table(resources.skin());
		root.setFillParent(true);
		stage().addActor(root);

		playerList = new List<>(resources.skin());
		playerList.setItems(session.players().toArray(SessionPlayer[]::new));

		root.row().expand();

		rightPanel = new Table(resources.skin());
		rightPanel.add((Actor) null).pad(Value.percentWidth(0.1f)).colspan(2).spaceBottom(10.0f);

		var nameField = new TextField(session.name(), resources.skin());
		nameField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode != Input.Keys.ENTER && keycode != Input.Keys.NUMPAD_ENTER) {
					return false;
				}

				var newName = nameField.getText();

				name(newName);
				nameField.setText(session.name());

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

	protected final void players(java.util.List<SessionPlayer> players) {
		playerList.setItems(players.toArray(SessionPlayer[]::new));
	}

	protected final GdxPlayer player() {
		return player;
	}

	/**
	 * Called when the user changes their name.
	 * @param newName the new name
	 */
	protected void name(String newName) {
		session.name(newName);
	}

	protected final Session session() {
		return session;
	}

	protected final void disposeSession(boolean shouldDispose) {
		disposeSession = shouldDispose;
	}

	@Override
	public void dispose() {
		if (disposeSession) {
			session.close();
		}
	}
}
