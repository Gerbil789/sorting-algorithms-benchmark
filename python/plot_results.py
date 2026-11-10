import json
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# 1. Load benchmark results
with open('results.json', 'r') as f:
    results = json.load(f)

df = pd.DataFrame(results)

# --- 2. COMPUTE FIVE-NUMBER SUMMARY ---
summary_overall = df.groupby('algorithm')['timeMs'].agg(
    Min=lambda x: np.percentile(x, 0),
    Q1=lambda x: np.percentile(x, 25),
    Median=lambda x: np.percentile(x, 50),
    Q3=lambda x: np.percentile(x, 75),
    Max=lambda x: np.percentile(x, 100)
).reset_index()

summary_by_size = df.groupby(['size', 'algorithm'])['timeMs'].agg(
    Min=lambda x: np.percentile(x, 0),
    Q1=lambda x: np.percentile(x, 25),
    Median=lambda x: np.percentile(x, 50),
    Q3=lambda x: np.percentile(x, 75),
    Max=lambda x: np.percentile(x, 100)
).reset_index()

print("=== FIVE-NUMBER SUMMARY (OVERALL BY ALGORITHM) ===")
print(summary_overall.to_markdown(index=False, floatfmt=".4f"))
print("\n")

print("=== FIVE-NUMBER SUMMARY (BY ARRAY SIZE & ALGORITHM) ===")
print(summary_by_size.to_markdown(index=False, floatfmt=".4f"))

# Save summary tables to markdown file
with open('five_number_summary.md', 'w') as f:
    f.write("# Descriptive Statistics: Five-Number Summary\n\n")
    f.write("## Overall Summary by Algorithm\n")
    f.write(summary_overall.to_markdown(index=False, floatfmt=".4f"))
    f.write("\n\n## Summary by Array Size & Algorithm\n")
    f.write(summary_by_size.to_markdown(index=False, floatfmt=".4f"))
    f.write("\n")

# --- 3. GENERATE VISUALIZATIONS ---
data = {}
algorithms = sorted(df['algorithm'].unique().tolist())
data_types = sorted(df['dataType'].unique().tolist())
sizes = sorted(df['size'].unique().tolist())
input_types = ["Random (Unpredictable)", "Already Sorted (Best Case)", "Reverse Sorted (Worst Case)", "Almost Sorted (Few Out of Place)"]

for _, row in df.iterrows():
    data.setdefault(row['dataType'], {}).setdefault(row['size'], {}).setdefault(row['inputType'], {})[row['algorithm']] = row['timeMs']

plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')

# Multi-panel Log Bar Chart
fig, axes = plt.subplots(len(data_types), len(sizes), figsize=(16, 5 * len(data_types)), squeeze=False)

for i, dt in enumerate(data_types):
    for j, size in enumerate(sizes):
        ax = axes[i, j]
        if size not in data[dt]:
            ax.set_visible(False)
            continue
            
        inp_dict = data[dt][size]
        available_inputs = [inp for inp in input_types if inp in inp_dict]
        
        x = np.arange(len(available_inputs))
        width = 0.25
        multiplier = 0
        
        for algo in algorithms:
            times = [inp_dict.get(inp, {}).get(algo, 1e-4) for inp in available_inputs]
            times = [max(t, 1e-4) for t in times]
            
            offset = width * multiplier
            ax.bar(x + offset, times, width, label=algo)
            multiplier += 1
            
        ax.set_yscale('log')
        ax.set_title(f'{dt} — Size: {size}', fontsize=11, fontweight='bold')
        ax.set_ylabel('Time (ms) [Log Scale]', fontsize=9)
        ax.set_xticks(x + width)
        ax.set_xticklabels([inp.split(' ')[0] for inp in available_inputs], fontsize=9, rotation=15)
        ax.grid(axis='y', which='both', linestyle='--', alpha=0.5)
        if i == 0 and j == 0:
            ax.legend(fontsize=8, title='Algorithms')

fig.suptitle('Bubble Sort Algorithms Performance Benchmark (Array Sizes: 100, 1,000, 10,000)', fontsize=14, fontweight='bold', y=0.98)
fig.tight_layout(rect=[0, 0, 1, 0.95])
plt.savefig('benchmark_comprehensive_log.png', dpi=300)
plt.close()

# Scalability Line Plot
fig, axes = plt.subplots(1, len(data_types), figsize=(18, 5), squeeze=False)

for i, dt in enumerate(data_types):
    ax = axes[0, i]
    size_list = sorted(data[dt].keys())
    
    for algo in algorithms:
        avg_times = []
        for size in size_list:
            rand_data = data[dt][size].get("Random (Unpredictable)", {})
            avg_times.append(rand_data.get(algo, 0))
            
        ax.plot(size_list, avg_times, marker='o', linewidth=2, label=algo)
        
    ax.set_xscale('log')
    ax.set_yscale('log')
    ax.set_title(f'Scalability on Random Input: {dt}', fontsize=11, fontweight='bold')
    ax.set_xlabel('Array Size (log scale)', fontsize=10)
    ax.set_ylabel('Execution Time (ms, log scale)', fontsize=10)
    ax.grid(True, which="both", ls="--", alpha=0.5)
    ax.legend(fontsize=8)

fig.suptitle('Algorithmic Scalability Analysis (O(n²) Growth Rate)', fontsize=14, fontweight='bold', y=1.02)
fig.tight_layout()
plt.savefig('benchmark_scalability.png', dpi=300)
plt.close()

print("Plots and summary tables successfully generated!")