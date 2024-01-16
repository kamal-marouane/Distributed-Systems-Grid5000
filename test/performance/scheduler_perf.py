import matplotlib.pyplot as plt
import pandas as pd

# Path to the CSV file
file_path = './test/scheduler.csv'  # Make sure the file is in the same directory as your script

# Read the CSV file
data = pd.read_csv(file_path, header=None, names=["Nodes", "Duration_ms"])

# Convert duration from nanoseconds to seconds
data["Duration_s"] = data["Duration_ms"] / 1000

# Plot
plt.figure(figsize=(10, 6))
plt.plot(data["Nodes"], data["Duration_s"], marker='o')
plt.title("Execution Time vs Number of Nodes")
plt.xlabel("Number of Nodes Reserved")
plt.ylabel("Execution Time (seconds)")
plt.grid(True)
plt.show()