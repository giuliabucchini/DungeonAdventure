
package it.unicam.cs.mpgc.rpg125904.presentation;

import java.util.List;

/**
 * Contiene la disposizione degli hotspot nelle stanze.
 */
public final class RoomHotspotLayout {

    private RoomHotspotLayout() {
    }

    public static List<HotspotDefinition> forRoom(int roomIndex) {
        return switch (roomIndex) {
            case 0 -> entranceRoom();
            case 1 -> inscriptionsRoom();
            case 2 -> puzzleRoom();
            case 3 -> secretRoom();
            case 4 -> sanctuaryRoom();
            default -> List.of();
        };
    }

    private static List<HotspotDefinition> entranceRoom() {
        return List.of(
                HotspotDefinition.item(
                        "Tavoletta dei geroglifici",
                        "Una tavoletta ricoperta di simboli antichi. "
                                + "Le incisioni potrebbero rivelare informazioni "
                                + "sui misteri custoditi nel tempio.",
                        0.22, 0.73, 60
                ),
                HotspotDefinition.detail(
                        "Il bassorilievo del faraone",
                        "Il bassorilievo raffigura il sovrano che fece costruire il tempio. "
                                + "Un'iscrizione racconta che il santuario fu nascosto "
                                + "per proteggere un antico segreto.",
                        0.72, 0.38
                ),
                HotspotDefinition.detail(
                        "Il braciere antico",
                        "Il braciere è spento da molto tempo. Sul bordo si riconosce "
                                + "un simbolo legato ai rituali di protezione del tempio.",
                        0.84, 0.69
                ),
                HotspotDefinition.detail(
                        "La pietra incisa",
                        "Una pietra sporge dal pavimento. Il simbolo inciso sembra "
                                + "avere la stessa origine di quello presente sulla tavoletta.",
                        0.43, 0.78
                ),
                HotspotDefinition.detail(
                        "I simboli sulla colonna",
                        "Sulla colonna sono incisi alcuni simboli consumati dal tempo. "
                                + "Uno di essi ricorda il segno del sole, infatti viene considerato "
                                + "come primo custode del tempio.",
                        0.30, 0.37
                )
        );
    }

    private static List<HotspotDefinition> inscriptionsRoom() {
        return List.of(
                HotspotDefinition.item(
                        "Scheggia del sigillo",
                        "Una scheggia di pietra, con il suo bagliore bluastro "
                                + "potrebbe appartenere al sigillo "
                                + "che protegge il tempio.",
                        0.13, 0.90, 43
                ),
                HotspotDefinition.item(
                        "Pergamena antica",
                        "Una pergamena con annotazioni su un antico rituale.",
                        0.48, 0.84, 82
                ),
                HotspotDefinition.item(
                        "Anfora rituale",
                        "Un'anfora decorata con simboli egizi.",
                        0.86, 0.90, 88
                ),
                HotspotDefinition.item(
                        "Scarabeo dorato",
                        "Un piccolo scarabeo d'oro, simbolo di rinascita e protezione.",
                        0.45, 0.65, 46
                ),
                HotspotDefinition.puzzle(
                        "Iscrizioni sulla parete",
                        "Le iscrizioni nascondono un indovinello. "
                                + "Osserva attentamente i simboli.",
                        0.73, 0.34
                )
        );
    }

    private static List<HotspotDefinition> puzzleRoom() {
        return List.of(
                HotspotDefinition.puzzle(
                        "Altare centrale",
                        "Sulla superficie dell'altare è inciso un enigma. "
                                + "Sembra reagire alla Scheggia del sigillo.",
                        0.51, 0.57
                )
        );
    }

    private static List<HotspotDefinition> secretRoom() {
        return List.of(
                HotspotDefinition.item(
                        "Chiave antica",
                        "Una chiave di metallo consumata dal tempo. Potrebbe aprire qualcosa nel tempio.",
                        0.28, 0.90, 68
                ),
                HotspotDefinition.item(
                        "Corda",
                        "Una corda robusta, apparentemente ancora utilizzabile.",
                        0.96, 0.94, 70
                ),
                HotspotDefinition.item(
                        "Lanterna",
                        "Una lanterna antica che potrebbe illuminare gli angoli più bui.",
                        0.66, 0.76, 80
                ),
                HotspotDefinition.detail(
                        "La stele inclinata",
                        "La stele racconta che l'ultimo custode affidò all'Ankh "
                                + "il compito di mantenere chiuso il santuario. ",
                        0.78, 0.40
                ),
                HotspotDefinition.detail(
                        "Il tavolo degli strumenti",
                        "Restano vasi e strumenti usati durante le cerimonie. "
                                + "La polvere attorno a un piccolo spazio circolare "
                                + "sembra essere stata rimossa di recente.",
                        0.62, 0.57
                )
        );
    }

    private static List<HotspotDefinition> sanctuaryRoom() {
        return List.of(
                HotspotDefinition.item(
                        "Ankh",
                        "Il simbolo egizio della vita eterna, scolpito in un piccolo oggetto.",
                        0.19, 0.89, 70
                ),
                HotspotDefinition.item(
                        "Pugnale cerimoniale",
                        "Un antico pugnale decorato. Sembra avere un significato rituale.",
                        0.82, 0.87, 68
                ),
                HotspotDefinition.finale(
                        "L'altare del sigillo",
                        "Al centro dell'altare c'è un incavo a forma di Ankh. "
                                + "Il simbolo della vita eterna deve essere collocato qui "
                                + "per risvegliare il sigillo.",
                        0.31, 0.67
                )
        );
    }
}
