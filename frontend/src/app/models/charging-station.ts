export interface ChargingStation {
  id: number;
  name:String;
  power: number | string; // BigDecimal côté back → number/string côté front
  hourlyRate: number;
  isCustom: boolean;

  // phase 2 (optionnel)
  photoUrl?: string | null;
  videoUrl?: string | null;
}
