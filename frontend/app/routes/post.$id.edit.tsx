import { useState, useRef, useEffect } from "react";
import type { Route } from "./+types/post.$id.edit";
import { useNavigate, Link } from "react-router";
import { getPost, updatePost } from "~/services/posts.service";
import { getCategories } from "~/services/categories.service";
import { requireAuth } from "~/utils/authGuard";
import { apiFetch } from "~/services/api";
import Sidebar from "~/components/sidebar";
import { p } from "~/utils/paths";

export async function clientLoader({ params }: Route.LoaderArgs) {
  return requireAuth(async () => {
    const postId = Number(params.id);
    const [post, categories] = await Promise.all([
      getPost(postId),
      getCategories(),
    ]);
    return { post, categories };
  });
}

export default function EditPost({ loaderData }: Route.ComponentProps) {
  const { post, categories } = loaderData;
  const navigate = useNavigate();
  const [content, setContent] = useState(post.content);
  const [categoryId, setCategoryId] = useState(post.categoryId?.toString() || "");
  const [image, setImage] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState("");
  const fileRef = useRef<HTMLInputElement>(null);

  const existingImage = `/api/v1/posts/${post.id}/image`;

  useEffect(() => {
    const checkImage = async () => {
      try {
        const res = await fetch(existingImage);
        if (!res.ok) return;
      } catch {
        // no existing image
      }
    };
    checkImage();
  }, [existingImage]);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    setImage(file);
    if (file) {
      const reader = new FileReader();
      reader.onload = () => setPreview(reader.result as string);
      reader.readAsDataURL(file);
    } else {
      setPreview(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await updatePost(post.id, content, Number(categoryId));

      if (image) {
        const formData = new FormData();
        formData.append("image", image);
        await apiFetch(`/posts/${post.id}/image`, {
          method: "PUT",
          body: formData,
        });
      }

      navigate(p(`/posts/${post.id}`));
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to update post");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-8 second-section" id="page-content-wrapper">
        <div className="groups bg-white shadow-sm p-4 rounded mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <h5 className="mb-0">Editar Publicaci&oacute;n</h5>
            <Link to={p(`/posts/${post.id}`)} className="btn btn-sm btn-outline-secondary">Cancelar</Link>
          </div>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        <div className="post border-bottom p-3 bg-white w-shadow mb-4">
          <div className="media text-muted pt-3">
            <img src={`/api/v1/users/${post.userId}/profile-picture`}
              className="mr-3 post-user-image rounded-circle" alt=""
              onError={(e) => { (e.target as HTMLImageElement).src = "/assets/images/users/user-4.jpg"; }} />
            <div className="media-body pb-3 mb-0 small lh-125">
              <div className="d-flex justify-content-between align-items-center w-100">
                <Link to={p(`/users/${post.userId}`)} className="text-gray-dark post-user-name">
                  {post.userNickname}
                </Link>
              </div>
              <span className="d-block">{post.createdAt} <i className='bx bx-globe ml-3'></i></span>
            </div>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="form-group mt-3">
              <textarea name="content" className="form-control" rows={4}
                value={content} onChange={(e) => setContent(e.target.value)} required />
            </div>
            <div className="form-group">
              <select className="form-control" value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}>
                {categories.map((c: any) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label className="btn btn-outline-secondary btn-sm">
                <span>Cambiar imagen</span>
                <input type="file" accept="image/*" hidden
                  ref={fileRef} onChange={handleImageChange} />
              </label>
              {preview && (
                <div className="mt-2 d-inline-block position-relative">
                  <img src={preview} alt="Preview" style={{ maxHeight: 120, borderRadius: 8 }} />
                </div>
              )}
            </div>
            <div className="form-group text-right mt-4">
              <button type="submit" className="btn btn-success">Guardar cambios</button>
              <Link to={p(`/posts/${post.id}`)} className="btn btn-secondary ml-2">Cancelar</Link>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}
