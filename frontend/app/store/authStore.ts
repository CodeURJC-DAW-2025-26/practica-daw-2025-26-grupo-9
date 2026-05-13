import { create } from "zustand";
import * as authService from "~/services/auth.service";
import type { UserProfileDTO } from "~/dto/UserDTO";

export type User = UserProfileDTO;

type AuthState = {
  user: User | null;
  loading: boolean;
  setUser: (user: User | null) => void;
  login: (email: string, password: string) => Promise<void>;
  register: (data: {
    name: string;
    surname: string;
    nickname: string;
    email: string;
    password: string;
  }) => Promise<void>;
  logout: () => Promise<void>;
  checkAuth: () => Promise<void>;
};

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  loading: true,
  setUser: (user) => set({ user }),
  login: async (email, password) => {
    await authService.login({ username: email, password });
    const user = await authService.getCurrentUserProfile();
    set({ user });
  },
  register: async (data) => {
    await authService.register(data);
  },
  logout: async () => {
    await authService.logout();
    set({ user: null });
  },
  checkAuth: async () => {
    try {
      const user = await authService.getCurrentUserProfile();
      set({ user, loading: false });
    } catch {
      set({ user: null, loading: false });
    }
  },
}));
