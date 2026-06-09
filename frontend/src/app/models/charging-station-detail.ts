export interface ChargingStationDetail {
  id: number;
  name: string;
  power: number;
  hourlyRate: number;
  instruction: string | null;
  latitude: number;
  longitude: number;
  isCustom: boolean;
  statusName: string | null;
  locationAddress: string;
  cityName: string;
  photoUrl?: string | null;
  videoUrl?: string | null;
  locationName: string |null;
}