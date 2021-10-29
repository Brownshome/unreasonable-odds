package brownshome.unreasonableodds.gdx;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Queue;

import brownshome.unreasonableodds.CharacterController;
import brownshome.unreasonableodds.entites.PlayerCharacter;
import brownshome.unreasonableodds.session.Id;
import brownshome.vecmath.Vec2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class GdxCharacterController implements CharacterController {
	@FunctionalInterface
	private interface QueuedAction {
		boolean performAction(PlayerCharacter.PlayerActions actions);
	}

	private final Queue<QueuedAction> queuedActions = new ArrayDeque<>();

	@Override
	public void performActions(PlayerCharacter.PlayerActions actions) {
		for (var next = queuedActions.poll(); next != null; next = queuedActions.poll()) {
			if (next.performAction(actions)) {
				return;
			}
		}

		actions.finaliseMove(directionOfMovement());
	}

	private Vec2 directionOfMovement() {
		var movementDirection = Vec2.ZERO.copy();

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			movementDirection.add(0, 1);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			movementDirection.add(-1, 0);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			movementDirection.add(0, -1);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			movementDirection.add(1, 0);
		}

		return movementDirection;
	}

	public void timeTravel() {
		queuedActions.add(a -> {
			Instant earliestLocation = a.earliestTimeTravelLocation();

			var distance = Duration.between(earliestLocation, a.now()).dividedBy(2);

			if (distance.isZero()) {
				return false;
			}

			a.timeTravel(earliestLocation.plus(distance));

			return true;
		});
	}

	public void jumpLeft() {
		queuedActions.add(a -> {
			Id id = a.leftJumpId();

			if (id != null) {
				a.jumpUniverse(id);
				return true;
			}

			return false;
		});
	}

	public void jumpRight() {
		queuedActions.add(a -> {
			Id id = a.rightJumpId();

			if (id != null) {
				a.jumpUniverse(id);
				return true;
			}

			return false;
		});
	}
}
