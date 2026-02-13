export interface WeekDayAvailability {
  id: number;          // 1..7
  label: string;       // Lundi, Mardi, ...
  enabled: boolean;
  startTime: string;
  endTime: string;
}