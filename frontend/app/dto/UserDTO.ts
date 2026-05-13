import type { PostDTO } from "~/dto/PostDTO";

export type UserDTO = {
  id: number;
  name: string;
  surname: string;
  nickname: string;
  email: string;
  description: string | null;
  active: boolean;
  roles: string[];
};

export type UserProfileDTO = UserDTO & {
  postsCount: number;
  commentsCount: number;
  posts: PostDTO[];
};

export type UserStatusDTO = {
  active: boolean;
};
