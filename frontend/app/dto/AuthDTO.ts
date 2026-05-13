export type LoginRequest = {
  username: string;
  password: string;
};

export type RegisterRequest = {
  name: string;
  surname: string;
  nickname: string;
  email: string;
  password: string;
  description?: string;
};
