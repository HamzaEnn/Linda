Tester est une application faite pour tester Linda.
L'application lit une description minimaliste depuis un fichier texte puis lance les tests.

Le fichier texte contient :
	- Appel aux m�thodes offertes par Linda (read, write, take, readAll ...).
	- un temps de pause (sleep) fait just par indiquer le nombre de ms dans la pause.
	- Et chaque ligne est un thread � part.

Utilisation : Tester [fileName] y|n
	Le 1er param�tre : le nom du fichier texte.
	Le 2�me param�tre (optionnel) : sp�cifier si on veut une description d�taill� des m�thodes appel�es.

Fonctionnement :
	Les tuples pouvant etre �crits sont sp�cifi�s dans la classe "Actions" et peuvent etre modifi�s. 