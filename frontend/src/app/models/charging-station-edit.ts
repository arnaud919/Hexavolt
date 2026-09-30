export interface ChargingStationEdit {
  id: number;
  name: string;
  powerId: number;
  hourlyRate: number;
  instruction: string | null;
  isCustom: boolean;
  latitude: number;
  longitude: number;
  statusId: number;
}