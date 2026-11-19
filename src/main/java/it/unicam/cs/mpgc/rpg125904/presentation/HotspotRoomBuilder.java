package it.unicam.cs.mpgc.rpg125904.presentation;

public class HotspotRoomBuilder {

    private final HotspotManager hotspotManager;

    public HotspotRoomBuilder(HotspotManager hotspotManager) {
        this.hotspotManager = hotspotManager;
    }

    public void build(int roomIndex) {

        switch (roomIndex) {

            // STANZA 1: INGRESSO DEL TEMPIO.
            case 0 -> {
                hotspotManager.addItemHotspot(
                        "Tavoletta dei geroglifici",
                        "Una tavoletta ricoperta di simboli antichi. "
                                + "Le incisioni potrebbero rivelare informazioni "
                                + "sui misteri custoditi nel tempio.",
                        0.22,
                        0.73,
                        60
                );

                hotspotManager.addDetailHotspot(
                        "Il bassorilievo del faraone",
                        "Il bassorilievo raffigura il sovrano che fece costruire il tempio. "
                                + "Un'iscrizione racconta che il santuario fu nascosto "
                                + "per proteggere un antico segreto.",
                        0.72,
                        0.38
                );

                hotspotManager.addDetailHotspot(
                        "Il braciere antico",
                        "Il braciere è spento da molto tempo. Sul bordo si riconosce "
                                + "un simbolo legato ai rituali di protezione del tempio.",
                        0.84,
                        0.69
                );

                hotspotManager.addDetailHotspot(
                        "La pietra incisa",
                        "Una pietra sporge dal pavimento. Il simbolo inciso sembra "
                                + "avere la stessa origine di quello presente sulla tavoletta.",
                        0.43,
                        0.78
                );

                hotspotManager.addDetailHotspot(
                        "I simboli sulla colonna",
                        "Sulla colonna sono incisi alcuni simboli consumati dal tempo. "
                                + "Uno di essi ricorda il segno del sole, infatti viene considerato "
                                + "come primo custode del tempio.",
                        0.30,
                        0.37
                );
            }

            // STANZA 2: SALA DELLE ISCRIZIONI.
            case 1 -> {
                hotspotManager.addItemHotspot(
                        "Scheggia del sigillo",
                        "Una scheggia di pietra, con il suo bagliore bluastro "
                                + "potrebbe appartenere al sigillo "
                                + "che protegge il tempio.",
                        0.13,
                        0.90,
                        43
                );

                hotspotManager.addItemHotspot(
                        "Pergamena antica",
                        "Una pergamena con annotazioni su un antico rituale.",
                        0.48,
                        0.84,
                        82
                );

                hotspotManager.addItemHotspot(
                        "Anfora rituale",
                        "Un'anfora decorata con simboli egizi.",
                        0.86,
                        0.90,
                        88
                );

                hotspotManager.addItemHotspot(
                        "Scarabeo dorato",
                        "Un piccolo scarabeo d'oro, simbolo di rinascita e protezione.",
                        0.45,
                        0.65,
                        46
                );

                hotspotManager.addPuzzleHotspot(
                        "Iscrizioni sulla parete",
                        "Le iscrizioni nascondono un indovinello. "
                                + "Osserva attentamente i simboli.",
                        0.73,
                        0.34
                );
            }

            // STANZA 3: CAMERA DEGLI ENIGMI.
            case 2 -> {
                hotspotManager.addPuzzleHotspot(
                        "Altare centrale",
                        "Sulla superficie dell'altare è inciso un enigma. "
                                + "Sembra reagire alla Scheggia del sigillo.",
                        0.51,
                        0.57
                );
            }

            // STANZA 4: CAMERA SEGRETA BLU.
            case 3 -> {
                hotspotManager.addItemHotspot(
                        "Chiave antica",
                        "Una chiave di metallo consumata dal tempo. Potrebbe aprire qualcosa nel tempio.",
                        0.28,
                        0.90,
                        68
                );

                hotspotManager.addItemHotspot(
                        "Corda",
                        "Una corda robusta, apparentemente ancora utilizzabile.",
                        0.96,
                        0.94,
                        70
                );

                hotspotManager.addItemHotspot(
                        "Lanterna",
                        "Una lanterna antica che potrebbe illuminare gli angoli più bui.",
                        0.66,
                        0.76,
                        80
                );

                hotspotManager.addDetailHotspot(
                        "La stele inclinata",
                        "La stele racconta che l'ultimo custode affidò all'Ankh "
                                + "il compito di mantenere chiuso il santuario. ",
                        0.78,
                        0.40
                );

                hotspotManager.addDetailHotspot(
                        "Il tavolo degli strumenti",
                        "Restano vasi e strumenti usati durante le cerimonie. "
                                + "La polvere attorno a un piccolo spazio circolare "
                                + "sembra essere stata rimossa di recente.",
                        0.62,
                        0.57
                );
            }

            // STANZA 5: CORRIDOIO DEL SANTUARIO.
            case 4 -> {
                hotspotManager.addItemHotspot(
                        "Ankh",
                        "Il simbolo egizio della vita eterna, scolpito in un piccolo oggetto.",
                        0.19,
                        0.89,
                        70
                );

                hotspotManager.addItemHotspot(
                        "Pugnale cerimoniale",
                        "Un antico pugnale decorato. Sembra avere un significato rituale.",
                        0.82,
                        0.87,
                        68
                );

                hotspotManager.addFinaleHotspot(
                        "L'altare del sigillo",
                        "Al centro dell'altare c'è un incavo a forma di Ankh. "
                                + "Il simbolo della vita eterna deve essere collocato qui "
                                + "per risvegliare il sigillo.",
                        0.31,
                        0.67
                );
            }

            default -> {
            }
        }
    }
}