Tester est une application faite pour tester Linda.
L'application lit une description minimaliste depuis un fichier texte puis lance les tests.

Le fichier texte contient :
	- Appel aux méthodes offertes par Linda (read, write, take, readAll ...).
	- un temps de pause (sleep) fait just par indiquer le nombre de ms dans la pause.
	- Et chaque ligne est un thread à part.

Utilisation : Tester [fileName] y|n
	Le 1er paramètre : le nom du fichier texte.
	Le 2éme paramètre (optionnel) : spécifier si on veut une description détaillé des méthodes appelées.

Fonctionnement :
	Les tuples pouvant etre écrits sont spécifiés dans la classe "Actions" et peuvent etre modifiés. 