import csv
from collections import defaultdict

# Fonction pour calculer la moyenne
def calculer_moyenne(ville_data):
    total_arg1 = 0
    total_arg2 = 0
    count = 0
    for valeur_arg1, valeur_arg2 in ville_data:
        total_arg1 += int(valeur_arg1)
        total_arg2 += int(valeur_arg2)
        count += 1
    moyenne_arg1 = total_arg1 / count if count > 0 else 0
    moyenne_arg2 = total_arg2 / count if count > 0 else 0
    return moyenne_arg1, moyenne_arg2

# Chemin complet du fichier d'entrée
with open('./test/pingpong/informations.txt', 'r') as fichier:
    lignes = fichier.readlines()

# Initialisation du dictionnaire pour stocker les données par ville
villes_data = defaultdict(list)

# Parcourir les lignes et stocker les données par ville
current_ville = None
for ligne in lignes:
    if '----' in ligne:
        current_ville = ligne.strip('-').strip()
    elif current_ville:
        _, valeur_arg1, valeur_arg2 = ligne.strip().split(',')
        current_ville = current_ville.rstrip('-').strip()
        villes_data[current_ville].append((valeur_arg1, valeur_arg2))

# Calculer la moyenne pour chaque ville
moyennes_villes = {ville: calculer_moyenne(data) for ville, data in villes_data.items()}

# Trier les villes par moyenne du premier argument de façon croissante
villes_triees = sorted(moyennes_villes.items(), key=lambda x: x[1][1])

# L'emplacement où on va enregistrer le fichier CSV de sortie
output_path = './test/pingpong/results_moyennes.csv'

# Écrire les résultats triés dans un fichier CSV
with open(output_path, 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    # Écrire l'en-tête du CSV
    writer.writerow(['Ville', 'Moyenne du Premier Argument', 'Moyenne du Deuxième Argument'])
    # Écrire les données triées
    for ville, (moyenne_arg1, moyenne_arg2) in villes_triees:
        writer.writerow([ville, moyenne_arg1, moyenne_arg2])

print(f"Le fichier '{output_path}' a été créé avec succès.")
