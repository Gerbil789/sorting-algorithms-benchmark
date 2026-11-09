import json
import matplotlib.pyplot as plt
import numpy as np

# 1. Load the benchmark JSON results
with open('results.json', 'r') as f:
    results = json.load(f)

# 2. Organize data by data type and size
organized = {}
algorithms = set()
input_types_order = ["Random (Unpredictable)", "Already Sorted (Best Case)", "Reverse Sorted (Worst Case)", "Almost Sorted (Few Out of Place)"]

for entry in results:
    dt = entry['dataType']
    size = entry['size']
    inp = entry['inputType']
    algo = entry['algorithm']
    time = entry['timeMs']

    algorithms.add(algo)
    organized.setdefault(dt, {}).setdefault(size, {}).setdefault(inp, {})[algo] = time

algorithms = sorted(list(algorithms))

# Collect all (dataType, size) pairs to determine grid dimensions
subplots_data = []
for dataType, sizes in organized.items():
    for size in sorted(sizes.keys()):
        subplots_data.append((dataType, size))

num_plots = len(subplots_data)
if num_plots == 0:
    print("No data found to plot!")
    exit()

# 3. Create a master figure with a dynamic grid layout
cols = 2 if num_plots > 1 else 1
rows = (num_plots + cols - 1) // cols

fig, axes = plt.subplots(rows, cols, figsize=(16, 6 * rows), squeeze=False)
axes_flat = axes.flatten()

for idx, (dataType, size) in enumerate(subplots_data):
    ax = axes_flat[idx]
    inputTypes = organized[dataType][size]
    
    available_inputs = [inp for inp in input_types_order if inp in inputTypes]
    if not available_inputs:
        continue

    x = np.arange(len(available_inputs))
    width = 0.25
    multiplier = 0

    for algo in algorithms:
        times = [inputTypes.get(inp, {}).get(algo, 0) for inp in available_inputs]
        offset = width * multiplier
        ax.bar(x + offset, times, width, label=algo)
        multiplier += 1

    ax.set_ylabel('Time (ms)', fontsize=10)
    ax.set_title(f'{dataType} (Size: {size})', fontsize=12, fontweight='bold')
    ax.set_xticks(x + width)
    
    # Shorten x-axis labels for readability in subplots
    short_labels = [inp.split(' ')[0] for inp in available_inputs]
    ax.set_xticklabels(short_labels, fontsize=9)
    ax.legend(title='Algorithms', fontsize=8, title_fontsize=9)
    ax.grid(axis='y', linestyle='--', alpha=0.7)

# Hide any unused subplot axes if grid cells exceed data plots
for idx in range(num_plots, len(axes_flat)):
    fig.delaxes(axes_flat[idx])

fig.suptitle('Sorting Algorithms Performance Benchmark', fontsize=16, fontweight='bold', y=0.98)
fig.tight_layout(rect=[0, 0, 1, 0.96])

# Save the single master image
output_filename = "benchmark_summary.png"
plt.savefig(output_filename, dpi=300)
print(f"Generated master summary chart: {output_filename}")

plt.close()