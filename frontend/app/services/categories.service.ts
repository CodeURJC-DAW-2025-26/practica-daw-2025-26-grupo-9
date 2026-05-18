import { apiFetch } from "~/services/api";
import type { CategoryDTO } from "~/dto/PostDTO";

export type CategoryExtendedDTO = CategoryDTO & {
  postCount?: number;
};

export async function getCategories(): Promise<CategoryDTO[]> {
  return apiFetch<CategoryDTO[]>("/categories");
}

export async function getCategory(id: number): Promise<CategoryExtendedDTO> {
  return apiFetch<CategoryExtendedDTO>(`/categories/${id}`);
}

export async function createCategory(data: { name: string; description?: string }): Promise<CategoryDTO> {
  return apiFetch<CategoryDTO>("/categories", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateCategory(id: number, data: { name?: string; description?: string }): Promise<CategoryDTO> {
  return apiFetch<CategoryDTO>(`/categories/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteCategory(id: number): Promise<void> {
  await apiFetch(`/categories/${id}`, { method: "DELETE" });
}

export async function uploadCategoryImage(id: number, image: File): Promise<void> {
  const formData = new FormData();
  formData.append("image", image);
  await apiFetch(`/categories/${id}/image`, {
    method: "POST",
    body: formData,
  });
}

export async function updateCategoryImage(id: number, image: File): Promise<void> {
  const formData = new FormData();
  formData.append("image", image);
  await apiFetch(`/categories/${id}/image`, {
    method: "PUT",
    body: formData,
  });
}
