Dungeon Adventure

Dungeon Adventure è un gioco di ruolo ed esplorazione point-and-click ambientato in un antico tempio dimenticato, il giocatore esplora cinque stanze interattive, esamina l'ambiente, raccoglie oggetti nell'inventario,
risolve enigmi per sbloccare i passaggi fino alla stanza finale.
Sviluppato in Java e JavaFX, il giocatore può salvare o riprendere la partita in formato JSON.

Come eseguire il progetto:

Prerequisiti:

    Java 25 (LTS)

    Gradle 

Istruzioni:

    git clone https://github.com/giuliabucchini/DungeonAdventure.git
    cd DungeonAdventure


Build del progetto:

    ./gradlew build


Esecuzione:

    ./gradlew run


Uso di strumenti di AI:

Durante lo sviluppo del progetto sono stati utilizzati strumenti di Intelligenza Artificiale (ChatGPT / Gemini) come supporto per:

 -Contenuti narrativi e asset grafici: supporto nella rifinitura dei testi degli enigmi e nella generazione delle immagini delle stanze e degli oggetti di gioco.

 -Chiarezza su errori e librerie: risolvere alcuni errori di compilazione e dubbi sull'uso di JavaFX (come l'adattamento dell'immagine della stanza e il posizionamento dei punti cliccabili) e di Gson per il salvataggio in JSON
 -Test unitari: supporto nella stesura iniziale delle suite di test JUnit 5, successivamente verificate e corrette tramite compilazione ed esecuzione.


Per una descrizione più dettagliata dell’uso dell’AI e dell'architettura del progetto, consultare la Wiki del repository.