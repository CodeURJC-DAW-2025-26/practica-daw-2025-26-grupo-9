import { useState } from "react";
import type { Route } from "./+types/category.$id";
import { Link } from "react-router";
import { getCategory } from "~/services/categories.service";
import { getPosts } from "~/services/posts.service";
import type { PostDTO } from "~/dto/PostDTO";
import Sidebar from "~/components/sidebar";
import { p } from "~/utils/paths";
import { apiFetch } from "~/services/api";
import { useAuthStore } from "~/store/authStore";

export async function clientLoader({ params }: Route.LoaderArgs) {
  const categoryId = Number(params.id);
  const [category, postsRes] = await Promise.all([
    getCategory(categoryId),
    getPosts(0, 50),
  ]);
  const categoryPosts = postsRes.content.filter((p: PostDTO) => p.categoryId === categoryId);
  const topCategories = await (await import("~/services/categories.service")).getCategories();
  return { category, posts: categoryPosts, topCategories: topCategories.slice(0, 5) };
}

export default function CategoryDetail({ loaderData }: Route.ComponentProps) {
  const { category, posts, topCategories } = loaderData;
  const user = useAuthStore((s) => s.user);
  const [postList, setPostList] = useState<PostDTO[]>(posts);

  const handleLike = async (postId: number) => {
    if (!user) return;
    await apiFetch(`/posts/${postId}/like`, { method: "POST" });
    const updated = await apiFetch<PostDTO>(`/posts/${postId}`);
    setPostList((prev) =>
      prev.map((p) =>
        p.id === postId ? { ...p, likedByCurrentUser: updated.likedByCurrentUser, likesCount: updated.likesCount } : p
      )
    );
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-8 second-section" id="page-content-wrapper">
        <h4 className="mb-1">{category.name}</h4>
        {category.description && <p className="text-muted">{category.description}</p>}

        <div className="mt-3">
          {postList.length > 0 ? (
            postList.map((post) => (
              <div className="card mb-3 shadow-sm" key={post.id}>
                <div className="card-body">
                  <div className="d-flex justify-content-between">
                    <Link to={p(`/users/${post.userId}`)}><strong>{post.userNickname}</strong></Link>
                    <span className="text-muted small">{new Date(post.createdAt).toLocaleDateString()}</span>
                  </div>
                  <Link to={p(`/posts/${post.id}`)} className="text-decoration-none text-dark">
                    <p className="mt-2 mb-2">{post.content}</p>
                  </Link>
                  <div className="d-flex justify-content-between align-items-center">
                    <div className="text-muted small">
                      <a href="#" className="post-card-buttons" onClick={(e) => { e.preventDefault(); handleLike(post.id); }}>
                        <i className={`bx ${post.likedByCurrentUser ? "bxs-like" : "bx-like"} mr-2`}></i> {post.likesCount}
                      </a>
                      <i className='bx bx-message-rounded ml-3 mr-1'></i> {post.comments?.length || 0}
                    </div>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="alert alert-secondary">No hay publicaciones en esta categor&iacute;a.</div>
          )}
        </div>
      </div>

      <div className="col-md-3 third-section">
        <div className="card shadow-sm">
          <div className="card-body">
            <div className="weather-card-header d-flex justify-content-between align-items-center">
              <p className="fs-1 mb-0">M&aacute;s populares</p>
            </div>
            <ul className="list-group list-group-flush newsfeed-left-sidebar tamaño mt-3">
              {topCategories.map((c: any) => (
                <li className="list-group-item" key={c.id}>
                  <Link to={p(`/categories/${c.id}`)}>{c.name}</Link>
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </>
  );
}
