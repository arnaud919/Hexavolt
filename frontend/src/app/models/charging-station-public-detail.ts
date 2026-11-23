export interface ChargingStationPublicDetail {
  id: number;
  name: string | null;
  power: number;
  hourlyRate: number;
  instruction: string | null;
  latitude: number;
  longitude: number;
  locationAddress: string;
  cityName: string;
  photoUrl: string | null;
  videoUrl: string | null;
  weeklySchedules: {
    dayOfWeekId: number;
    startTime: string;
    endTime: string;
  }[];
}
