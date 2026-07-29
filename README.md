# PlyoMetrics

PlyoMetrics is an Android app that measures vertical jump using only the smartphone's built-in accelerometer.

The goal of the project is to estimate jump metrics such as flight time and jump height without requiring any external hardware.

## How it works

A jump is detected by analyzing the acceleration measured by the phone.

The current algorithm identifies three key events:

- Impulse, the first large acceleration peak generated during the push-off.
- Take-off, the instant when the phone enters free fall.
- Landing, the impact peak when the feet touch the ground.

The flight time is then computed as:
```
flightTime = landingTime - takeOffTime
```

The jump height is estimated using the classical projectile motion equation:

```
height = g × flightTime² / 8
```

where:
```
g = 9.81 m/s²
flightTime is expressed in seconds.
```
