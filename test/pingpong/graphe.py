import csv
import matplotlib.pyplot as plt
import numpy as np

# Le chemin complet du fichier CSV
csv_file_path = './test/pingpong/results_moyennes.csv'

# Lire les données du fichier CSV
villes = []
moyennes_arg1 = []
moyennes_arg2 = []

with open(csv_file_path, 'r') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        villes.append(row['Ville'])
        # Convertir les valeurs de nanosecondes en millisecondes
        moyennes_arg1.append(float(row['Moyenne du Premier Argument']) / 1e6)
        moyennes_arg2.append(float(row['Moyenne du Deuxième Argument']) / 1e6)

# Largeur des barres
bar_width = 0.35
index = np.arange(len(villes))

# Créer le graphique avec des barres côte à côte
fig, ax1 = plt.subplots(figsize=(14, 8))

# Barres pour l'argument 1 (bleu)
bar1 = ax1.bar(index - bar_width/2, moyennes_arg1, bar_width, label="Temps de recherche d'objet dans le registre", color='tab:blue')

# Configuration du premier axe des abscisses
ax1.set_xlabel('Villes')
ax1.set_ylabel('Temps (ms)', color='tab:blue')
ax1.tick_params(axis='y', labelcolor='tab:blue')
ax1.set_xticks(index)
ax1.set_xticklabels(villes, rotation=45, ha='right')

# Ajuster l'échelle pour une meilleure visibilité
ax1.set_ylim(0, max(moyennes_arg1) * 1.2)

# Deuxième axe des abscisses pour l'argument 2 (rouge)
ax2 = ax1.twinx()
bar2 = ax2.bar(index + bar_width/2, moyennes_arg2, bar_width, label="Temps d'invocation de la méthode ", color='tab:red')

# Configuration du deuxième axe des abscisses
ax2.set_ylabel('Temps (ms)', color='tab:red')
ax2.tick_params(axis='y', labelcolor='tab:red')

# Ajuster l'échelle pour une meilleure visibilité
ax2.set_ylim(0, max(moyennes_arg2) * 1.2)

# Titre du graphique
plt.title("Mésure d'échange ping-pong")

# Légende
legend1 = ax1.legend(loc='upper left', bbox_to_anchor=(0.05, 1.2))
legend2 = ax2.legend(loc='upper left', bbox_to_anchor=(0.50, 1.2))

# Afficher le graphique
plt.tight_layout()
plt.show()
