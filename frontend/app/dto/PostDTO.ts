export type Comment = {
  id: number;
  content: string;
  createdAt: string;
  userId: number;
  userNickname: string;
};

export type Post = {
  id: number;
  content: string;
  createdAt: string;
  likesCount: number;

  userId: number;
  userNickname: string;

  categoryId: number;
  categoryName: string;

  comments: Comment[];
};