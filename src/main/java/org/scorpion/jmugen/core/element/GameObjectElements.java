package org.scorpion.jmugen.core.element;

import org.scorpion.jmugen.core.data.GroupedContent;
import org.scorpion.jmugen.core.format.Sprite;

public class GameObjectElements {

    public final GroupedContent<Sprite> sprites;

    public GameObjectElements(GroupedContent<Sprite> sprites) {
        this.sprites = sprites;
    }

}
