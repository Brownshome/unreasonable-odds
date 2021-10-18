package brownshome.unreasonableodds.player;

import java.net.InetSocketAddress;

import brownshome.unreasonableodds.session.Id;

/**
 * A player in a networked session (game or lobby). All such players are assigned an ID on creation. The following
 * terminology is used for networked players:
 * <ul>
 *     <li><code>Local</code> players are hosted and controlled on this session</li>
 *     <li><code>Imported</code> players are hosted in this session but controlled by another session</li>
 *     <li><code>Exported</code> players are hosted in another session but controlled by this session</li>
 *     <li>Players that are neither hosted nor-controlled by this session are given no special term</li>
 * </ul>
 */
public interface NetworkPlayer extends Player {
	Id id();
}
