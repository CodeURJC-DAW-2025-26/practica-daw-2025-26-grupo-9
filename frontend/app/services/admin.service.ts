import { apiFetch } from "~/services/api";
import type { UserDTO } from "~/dto/UserDTO";

export async function updateUserStatus(
  id: number,
  active: boolean
): Promise<UserDTO> {
  return apiFetch<UserDTO>(`/admin/users/${id}/status`, {
    method: "PATCH",
    body: JSON.stringify({ active }),
  });
}

export async function deleteUser(id: number): Promise<void> {
  await apiFetch(`/admin/users/${id}`, { method: "DELETE" });
}
