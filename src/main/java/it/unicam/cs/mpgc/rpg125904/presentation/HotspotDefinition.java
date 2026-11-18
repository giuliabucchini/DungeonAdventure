
package it.unicam.cs.mpgc.rpg125904.presentation;

/**
 * Descrive un elemento interattivo presente in una stanza.
 */
public record HotspotDefinition(
        Type type,
        String name,
        String description,
        double x,
        double y,
        double size
) {
    public enum Type {
        ITEM,
        DETAIL,
        PUZZLE,
        FINALE
    }

    public static HotspotDefinition item(
            String name, String description,
            double x, double y, double size) {
        return new HotspotDefinition(
                Type.ITEM, name, description, x, y, size
        );
    }

    public static HotspotDefinition detail(
            String name, String description,
            double x, double y) {
        return new HotspotDefinition(
                Type.DETAIL, name, description, x, y, 0
        );
    }

    public static HotspotDefinition puzzle(
            String name, String description,
            double x, double y) {
        return new HotspotDefinition(
                Type.PUZZLE, name, description, x, y, 0
        );
    }

    public static HotspotDefinition finale(
            String name, String description,
            double x, double y) {
        return new HotspotDefinition(
                Type.FINALE, name, description, x, y, 0
        );
    }
}
