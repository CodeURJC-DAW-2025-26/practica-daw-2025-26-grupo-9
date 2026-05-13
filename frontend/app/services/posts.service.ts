import { apiFetch } from "~/services/api";
import type { Page } from "~/dto/Page";
import type { PostDTO, CommentDTO } from "~/dto/PostDTO";

export async function getPosts(
  page: number = 0,
  size: number = 10
): Promise<Page<PostDTO>> {
  return apiFetch<Page<PostDTO>>(`/posts?page=${page}&size=${size}`);
}

export async function getPost(id: number): Promise<PostDTO> {
  return apiFetch<PostDTO>(`/posts/${id}`);
}

export async function createPost(
  content: string,
  categoryId: number
): Promise<PostDTO> {
  return apiFetch<PostDTO>("/posts", {
    method: "POST",
    body: JSON.stringify({ content, categoryId }),
  });
}

export async function updatePost(
  id: number,
  content: string,
  categoryId?: number
): Promise<PostDTO> {
  return apiFetch<PostDTO>(`/posts/${id}`, {
    method: "PUT",
    body: JSON.stringify({ content, categoryId }),
  });
}

export async function deletePost(id: number): Promise<PostDTO> {
  return apiFetch<PostDTO>(`/posts/${id}`, { method: "DELETE" });
}

export async function togglePostLike(id: number): Promise<void> {
  await apiFetch(`/posts/${id}/like`, { method: "POST" });
}

export async function createComment(
  postId: number,
  content: string
): Promise<CommentDTO> {
  return apiFetch<CommentDTO>(`/posts/${postId}/comments`, {
    method: "POST",
    body: JSON.stringify({ content }),
  });
}

export async function getTopPosts(): Promise<PostDTO[]> {
  return apiFetch<PostDTO[]>("/posts/statistics");
}
