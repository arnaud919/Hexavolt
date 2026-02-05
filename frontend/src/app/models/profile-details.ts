export interface ProfileDetails {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  address?: string;
  postalCode?: string;
  cityId: number;
  birthdate?: string;
}