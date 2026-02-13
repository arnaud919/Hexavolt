export interface MyReservation {
  reservationId: number;
  startDateTime: string;
  endDateTime: string;
  status: string;
  amount: number;

  chargingStationId: number;
  chargingStationName: string;
}
