import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# List of file paths for each city
file_paths = {
    'Grenoble': './test/latency/latency_results_from_grenoble.csv',
    'Nancy': './test/latency/latency_results_from_nancy.csv',
    'Rennes': './test/latency/latency_results_from_rennes.csv',
    'Nantes': './test/latency/latency_results_from_nantes.csv',
    'Luxembourg': './test/latency/latency_results_from_luxembourg.csv',
    'Lyon': './test/latency/latency_results_from_lyon.csv',
    'Lille': './test/latency/latency_results_from_lille.csv'
}

# Function to calculate average latency for each method (scp and rsync)
def calculate_average_latency(df):
    return df.groupby('Method')['Latency'].mean()

# Plotting the histogram
plt.figure(figsize=(14, 8))

bar_width = 0.35  # Width of each bar
bar_spacing = 1  # Spacing between cities

colors = {'scp': 'skyblue', 'rsync': 'salmon'}

for i, (city, file_path) in enumerate(file_paths.items()):
    # Read the CSV file
    df = pd.read_csv(file_path)

    # Calculate average latency for each method
    averages = calculate_average_latency(df) * 0.1

    # Plot the bar chart for scp
    plt.bar(i * bar_spacing, averages['scp'], bar_width, color=colors['scp'], label='scp' if i == 0 else '')

    # Plot the bar chart for rsync
    plt.bar(i * bar_spacing + bar_width, averages['rsync'], bar_width, color=colors['rsync'], label='rsync' if i == 0 else '')

# Adding labels and title
plt.xlabel('City')
plt.ylabel('Average Latency (in seconds)')
plt.title('Average Latency of scp and rsync for Each City')
plt.xticks(np.arange(len(file_paths)) * bar_spacing + bar_width / 2, file_paths.keys())
plt.legend()
plt.grid(axis='y')

# Show the plot
plt.show()