export interface ChargingStationSearch {
  id: number;
  name: string | null;
  power: number;
  hourlyRate: number;
  cityName: string;
  locationAddress: string;
  photoUrl: string | null;
  latitude: number;
  longitude: number;
}