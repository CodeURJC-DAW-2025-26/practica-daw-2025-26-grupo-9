export type CommentDTO = {
  id: number;
  content: string;
  createdAt: string;
  likesCount: number;
  userId: number;
  userNickname: string;
  likedByCurrentUser: boolean;
};

export type PostDTO = {
  id: number;
  content: string;
  createdAt: string;
  likesCount: number;
  userId: number;
  userNickname: string;
  categoryId: number;
  categoryName: string;
  comments: CommentDTO[];
  likedByCurrentUser: boolean;
};

export type CategoryDTO = {
  id: number;
  name: string;
  description?: string;
  imageUrl?: string;
  postsCount?: number;
};
