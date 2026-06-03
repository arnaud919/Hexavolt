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
  photoName: string | null;
  videoName: string | null;
}