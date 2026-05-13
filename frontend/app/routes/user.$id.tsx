import { useState } from "react";
import type { Route } from "./+types/user.$id";
import { Link, useNavigate } from "react-router";
import { p } from "~/utils/paths";
import { useAuthStore } from "~/store/authStore";
import { getProfilePictureUrl, getCoverPictureUrl, getUser } from "~/services/users.service";
import { getPosts } from "~/services/posts.service";
import { getCategories } from "~/services/categories.service";
import { apiFetch } from "~/services/api";
import type { CategoryDTO, PostDTO } from "~/dto/PostDTO";
import Sidebar from "~/components/sidebar";
import PostList from "~/components/postList";
import PostForm from "~/components/postForm";
import { requireAuth } from "~/utils/authGuard";

export async function clientLoader({ params }: Route.LoaderArgs) {
  return requireAuth(async () => {
    const userId = Number(params.id);
    const [userInfo, allPosts, categories] = await Promise.all([
      getUser(userId),
      getPosts(0, 50),
      getCategories(),
    ]);
    const userPosts = allPosts.content.filter((p: PostDTO) => p.userId === userId);
    return { userInfo, posts: userPosts, categories };
  });
}

export default function UserProfile({ loaderData }: Route.ComponentProps) {
  const { userInfo, posts: initialPosts, categories } = loaderData;
  const currentUser = useAuthStore((s) => s.user);
  const navigate = useNavigate();

  const [posts, setPosts] = useState<PostDTO[]>(initialPosts);
  const isOwnProfile = currentUser?.id === userInfo.id;

  const postsCount = posts.length;
  const commentsCount = posts.reduce((sum, p) => sum + (p.comments?.length || 0), 0);

  const handleLikeToggled = (
    postId: number,
    liked: boolean,
    newCount: number
  ) => {
    setPosts((prev) =>
      prev.map((p) =>
        p.id === postId
          ? { ...p, likedByCurrentUser: liked, likesCount: newCount }
          : p
      )
    );
  };

  const handlePostCreated = (newPost: PostDTO) => {
    setPosts((prev) => [newPost, ...prev]);
  };

  const handleDeleteAccount = async () => {
    if (!confirm("¿Estás segura de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")) return;
    try {
      await apiFetch("/users/me", { method: "DELETE" });
      navigate(p("/login"));
    } catch {
      alert("Error al eliminar la cuenta");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="profile col-md-10">
        <div className="profile-header-background">
          <img
            src={getCoverPictureUrl(userInfo.id)}
            alt="Profile Header Background"
            onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }}
          />
        </div>

        <div className="row profile-rows">
          <div className="col-md-3">
            <div className="profile-info-left">
              <div className="text-center">
                <div className="profile-img w-shadow">
                  <div className="profile-img-overlay"></div>
                  <img
                    src={getProfilePictureUrl(userInfo.id)}
                    alt="Avatar"
                    className="avatar img-circle"
                    onError={(e) => { (e.target as HTMLImageElement).src = "https://via.placeholder.com/150"; }}
                  />
                </div>
                <p className="profile-fullname mt-3">{userInfo.name} {userInfo.surname}</p>
                <p className="profile-username mb-3 text-muted">@{userInfo.nickname}</p>

                {isOwnProfile && (
                  <>
                    <Link to={p(`/users/${userInfo.id}/edit`)} className="btn btn-sm btn-outline-primary">
                      Editar perfil
                    </Link>
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-danger ml-2"
                      onClick={handleDeleteAccount}
                    >
                      Eliminar cuenta
                    </button>
                  </>
                )}
              </div>

              <div className="intro mt-5 mv-hidden">
                <div className="intro-item d-flex justify-content-between align-items-center">
                  <h3 className="intro-about">Descripci&oacute;n:</h3>
                </div>
                <div className="intro-item d-flex justify-content-between align-items-center">
                  <p className="intro-title text-muted">
                    {userInfo.description || "Sin descripción."}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="col-md-9 p-0">
            <div className="profile-info-right">
              <div className="row">
                <div className="col-md-9 profile-center">
                  <hr className="my-5" />

                  {isOwnProfile && (
                    <PostForm categories={categories} onPostCreated={handlePostCreated} />
                  )}

                  <div className="bg-white profile-posts-options mt-5 mb-4 py-3 d-flex justify-content-between shadow-sm">
                    <div className="col-md-3 col-sm-12 d-flex justify-content-between align-items-center">
                      <h6 className="timeline-title mb-0">Publicaciones:</h6>
                      <span className="text-muted">{postsCount}</span>
                    </div>
                    <div className="col-md-9 col-sm-12">
                      <div className="timeline-manage"></div>
                    </div>
                  </div>

                  <div className="bg-white profile-posts-options mt-5 mb-4 py-3 d-flex justify-content-between shadow-sm">
                    <div className="col-md-3 col-sm-12 d-flex justify-content-between align-items-center">
                      <h6 className="timeline-title mb-0">Comentarios:</h6>
                      <span className="text-muted">{commentsCount}</span>
                    </div>
                    <div className="col-md-9 col-sm-12">
                      <div className="timeline-manage"></div>
                    </div>
                  </div>

                  <PostList posts={posts} onLikeToggled={handleLikeToggled} />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
