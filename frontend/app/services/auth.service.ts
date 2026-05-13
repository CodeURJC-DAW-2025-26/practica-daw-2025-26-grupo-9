import { apiFetch } from "~/services/api";
import type { LoginRequest, RegisterRequest } from "~/dto/AuthDTO";
import type { UserProfileDTO } from "~/dto/UserDTO";

export async function login(data: LoginRequest): Promise<void> {
  await fetch("/api/v1/auth/login", {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export async function register(data: RegisterRequest): Promise<void> {
  await fetch("/api/v1/auth/register", {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export async function logout(): Promise<void> {
  await fetch("/api/v1/auth/logout", {
    method: "POST",
    credentials: "include",
  });
}

export async function refresh(): Promise<void> {
  await fetch("/api/v1/auth/refresh", {
    method: "POST",
    credentials: "include",
  });
}

export async function getCurrentUserProfile(): Promise<UserProfileDTO> {
  return apiFetch<UserProfileDTO>("/users/me?page=0&size=10");
}
