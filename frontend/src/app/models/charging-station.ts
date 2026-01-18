export interface ChargingStation {
  id: number;
  power: number | string; // BigDecimal côté back → number/string côté front
  hourlyRate: number;
  isCustom: boolean;

  // phase 2 (optionnel)
  photoName?: string | null;
  videoName?: string | null;
}
