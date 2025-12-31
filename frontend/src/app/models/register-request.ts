// src/app/models/register-request.model.ts (ou autre chemin adapté)
export interface RegisterRequest {
  firstName: string;
  lastName: string;
  address: string;
  postalCode: string;
  phone: string;
  birthdate: string;
  cityId: number;
  email: string;
  password: string;
}
