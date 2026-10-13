# Parcel Delivery System

A console-based Parcel Delivery System in Java that demonstrates **polymorphism**,
**exception handling**, and the **Singleton** and **Strategy** design patterns.

## How to Run

```bash
javac -d out src/*.java
java -cp out Main
```

## Design

| Concept | Where |
|---|---|
| **Polymorphism** | `Parcel` (abstract) → `Document`, `Box`, `Fragile extends Box`. Each overrides `getType()`, `getMaxWeight()`, `getChargeableWeight()`, `getBaseFee()`. `Fragile` reuses `Box` via `super.getBaseFee() + 100`. |
| **Strategy pattern** | `DeliveryStrategy` interface → `StandardDelivery`, `ExpressDelivery`, `SameDayDelivery`. A parcel's fee is `deliveryService.calculateFee(getBaseFee())`, and the strategy can be swapped at runtime. `SameDayDelivery` owns its own `isAllowedFor()` rule (≤ 10 kg). |
| **Singleton pattern** | `ParcelManager` — private constructor + `getInstance()`; the single store of all parcels. |
| **Exception handling** | Custom checked exceptions extending `ParcelException`: `InvalidInputException`, `DuplicateTrackingNumberException`, `ParcelNotFoundException`, `ParcelAlreadyDeliveredException`, `SameDayNotAllowedException`. `Main` catches them to re-prompt or show the right message. |

## Pricing Rules

| Type | Max weight | Chargeable weight | Base fee |
|---|---|---|---|
| Document | 0.5 kg | actual weight | Php 50 flat |
| Box | 50 kg | max(actual, L×W×H / 3500), rounded up | Php 40 × chargeable |
| Fragile | 30 kg | same as Box | Box fee + Php 100 |

| Service | Delivery fee |
|---|---|
| Standard | Base fee |
| Express | Base fee × 1.5 |
| Same Day | (Base fee × 2) + Php 100 — only if chargeable weight ≤ 10 kg |

**Example:** Fragile, 40×20×10 cm, 1.8 kg, Express →
volumetric 2.29 kg → chargeable 3 kg → base (40×3)+100 = Php 220 → fee **Php 330.00**.

## Menu

1. Add Parcel
2. View All Parcels
3. Change Delivery Service
4. Mark as Delivered
5. Exit
