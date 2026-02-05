export interface ProfileUpdateRequest {
  firstName: string;
  lastName: string;
  phone?: string;
  address?: string;
  postalCode?: string;
  cityId: number;
  birthdate?: string;
}
