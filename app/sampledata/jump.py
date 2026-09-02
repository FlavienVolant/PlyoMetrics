import csv
import math
import statistics
import matplotlib.pyplot as plt

def parse_float(s):
    s = s.strip()
    return float(s.replace('E-', 'e-').replace('E+', 'e+'))

def main():
    data = []
    with open('jump_01.csv', newline='') as f:
        reader = csv.DictReader(f)
        for row in reader:
            ts = parse_float(row['timestamp'])
            ax = parse_float(row[' x'])
            ay = parse_float(row[' y'])
            az = parse_float(row[' z'])
            data.append((ts, ax, ay, az))

    # Temps et accélération verticale brute (axe Z du capteur)
    times = [ts/1000.0 for ts, _, _, _ in data]
    a_z = [az for _, _, _, az in data]

    # Intervalle
    dts = [data[i+1][0] - data[i][0] for i in range(len(data)-1)]
    dt = statistics.median(dts) / 1000.0
    print(f"dt estimé: {dt*1000:.2f} ms")

    # Estimation de g sur 200 ms au début
    n_static = max(1, int(0.2 / dt))
    g_est = statistics.median(a_z[:n_static])
    print(f"g estimé: {g_est:.3f} m/s²")

    # Accélération nette (sans gravité)
    a_net = [az - g_est for az in a_z]

    # Détection phase en l'air (sur a_z brute)
    threshold = 0.5 * g_est
    in_air = [abs(a) < threshold for a in a_z]

    # Trouver la plus longue séquence en l'air
    best_start = None
    best_end = None
    best_len = 0
    i = 0
    while i < len(in_air):
        if in_air[i]:
            start = i
            while i < len(in_air) and in_air[i]:
                i += 1
            end = i - 1
            length = end - start + 1
            if length > best_len:
                best_len = length
                best_start = start
                best_end = end
        else:
            i += 1

    if best_start is not None:
        t0 = times[best_start]
        t1 = times[best_end]
        T = t1 - t0
        print(f"Phase en l'air détectée : t0={t0:.3f} s, t1={t1:.3f} s, T={T:.3f} s")
        h = 9.81 * T**2 / 8.0
        print(f"Hauteur estimée: {h:.3f} m")

    # Plot
    plt.figure(figsize=(10, 5))
    plt.plot(times, a_z, label='a_z brute (incl. g)', color='blue')
    plt.plot(times, a_net, label='a_z - g_est', color='orange', alpha=0.7)

    if best_start is not None:
        plt.axvspan(times[best_start], times[best_end],
                    color='green', alpha=0.2, label='Phase en l’air')

    plt.axhline(g_est, color='gray', linestyle='--', label='g_est')
    plt.axhline(0, color='black', linewidth=0.5)

    plt.xlabel('Temps (s)')
    plt.ylabel('Accélération (m/s²)')
    plt.title('Accélération verticale du saut (axe Z)')
    plt.legend()
    plt.grid(True)

    plt.tight_layout()
    plt.show()

if __name__ == '__main__':
    main()