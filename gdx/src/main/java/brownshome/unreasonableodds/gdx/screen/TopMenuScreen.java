package brownshome.unreasonableodds.gdx.screen;

import brownshome.unreasonableodds.gdx.ApplicationResources;
import brownshome.unreasonableodds.gdx.GdxRules;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TopMenuScreen extends StageScreen {
	public TopMenuScreen(ApplicationResources resources) {
		super(resources);

		var table = new Table(resources.skin());
		table.setFillParent(true);
		stage().addActor(table);

		TextButton createMultiverse = new TextButton("Create Multiverse", resources.skin());
		createMultiverse.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				nextScreen(new MultiverseScreen(resources, new GdxRules(resources)));
			}
		});

		TextButton connect = new TextButton("Connect", resources.skin());
		connect.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				nextScreen(new ConnectScreen(resources));
			}
		});

		TextButton host = new TextButton("Host", resources.skin());
		host.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				nextScreen(new HostScreen(resources));
			}
		});

		table.defaults().height(80.0f).fill();
		table.add(createMultiverse).colspan(2).spaceBottom(10.0f);
		table.row();
		table.add(connect).width(200.0f);
		table.add(host).width(200.0f);
	}
}
