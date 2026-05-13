import { apiFetch } from "~/services/api";
import type { Page } from "~/dto/Page";
import type { UserDTO, UserProfileDTO } from "~/dto/UserDTO";

export async function getUsers(
  page: number = 0,
  size: number = 10
): Promise<Page<UserDTO>> {
  return apiFetch<Page<UserDTO>>(`/users?page=${page}&size=${size}`);
}

export async function getUser(id: number): Promise<UserDTO> {
  return apiFetch<UserDTO>(`/users/${id}`);
}

export async function getCurrentUser(
  page: number = 0,
  size: number = 10
): Promise<UserProfileDTO> {
  return apiFetch<UserProfileDTO>(`/users/me?page=${page}&size=${size}`);
}

export async function updateProfile(
  formData: FormData
): Promise<UserDTO> {
  return apiFetch<UserDTO>("/users/me", {
    method: "PATCH",
    body: formData,
  });
}

export async function deleteAccount(): Promise<void> {
  await apiFetch("/users/me", { method: "DELETE" });
}

export function getProfilePictureUrl(id: number): string {
  return `/api/v1/users/${id}/profile-picture`;
}

export function getCoverPictureUrl(id: number): string {
  return `/api/v1/users/${id}/cover-picture`;
}
