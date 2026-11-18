package it.unicam.cs.mpgc.rpg125904.presentation;

import it.unicam.cs.mpgc.rpg125904.domain.Item;

final class Hotspot {

    enum Type {
        ITEM,
        PUZZLE,
        DETAIL,
        FINALE
    }

    final Type type;
    final String title;
    final String description;
    final Item item;
    final double x;
    final double y;
    final double size;

    Hotspot(
            Type type,
            String title,
            String description,
            Item item,
            double x,
            double y,
            double size
    ) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.item = item;
        this.x = x;
        this.y = y;
        this.size = size;
    }
}