import { useState } from "react";
import { useAuthStore } from "~/store/authStore";
import { apiFetch } from "~/services/api";
import type { CategoryDTO, PostDTO } from "~/dto/PostDTO";

type PostFormProps = {
  categories: CategoryDTO[];
  onPostCreated: (post: PostDTO) => void;
};

export default function PostForm({ categories, onPostCreated }: PostFormProps) {
  const user = useAuthStore((s) => s.user);
  const [content, setContent] = useState("");
  const [categoryId, setCategoryId] = useState("");

  const submitPost = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    const newPost = await apiFetch<PostDTO>("/posts", {
      method: "POST",
      body: JSON.stringify({ content, categoryId: Number(categoryId) }),
    });

    setContent("");
    setCategoryId("");
    onPostCreated(newPost);
  };

  return (
    <li className="media post-form w-shadow">
      <div className="media-body">
        <form onSubmit={submitPost}>
          <div className="form-group post-input">
            {user ? (
              <textarea className="form-control" id="postForm" name="content" rows={2}
                placeholder={`What's on your mind, ${user.nickname}?`}
                value={content}
                onChange={(e) => setContent(e.target.value)} />
            ) : (
              <textarea className="form-control" id="postForm" rows={2}
                placeholder="Inicia sesión para publicar" disabled />
            )}
          </div>
          <div className="form-group">
            <select className="form-control" name="category_id"
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
              disabled={!user}>
              <option value="">Select category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <div className="row post-form-group">
            <div className="col-md-9">
              <label className="btn btn-link post-form-btn btn-sm mb-0">
                <img src="/assets/images/icons/theme/post-image.png" alt="post form icon" />
                <span> Photo/Video</span>
                <input type="file" name="picture" accept="image/*" hidden />
              </label>
            </div>
            <div className="col-md-3 text-right">
              <button type="submit" className="btn btn-primary btn-sm" disabled={!user}>Publish</button>
            </div>
          </div>
        </form>
      </div>
    </li>
  );
}
