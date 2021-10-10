package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.session.Session;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class LobbyScreen extends StageScreen {
	protected final Session session;
	private final List<String> playerList;
	private final Table rightPanel;

	public LobbyScreen(ApplicationResources resources, Session session) {
		super(resources);

		this.session = session;

		var root = new Table(resources.skin());
		root.setFillParent(true);
		stage().addActor(root);

		playerList = new List<>(resources.skin());
		playerList.setItems(session.players().toArray(String[]::new));

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
					leave();
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

	protected final void names(java.util.List<String> players) {
		playerList.setItems(players.toArray(String[]::new));
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

	/**
	 * Called when the user has pressed the leave-lobby button
	 */
	protected abstract void leave();
}
