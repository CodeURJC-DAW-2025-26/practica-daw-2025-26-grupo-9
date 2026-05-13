import { useState } from "react";
import type { Route } from "./+types/home";
import type { PostDTO, CategoryDTO } from "~/dto/PostDTO";
import { apiFetch } from "~/services/api";
import { requireAuth } from "~/utils/authGuard";

import Sidebar from "~/components/sidebar";
import PostList from "~/components/postList";
import TopCategories from "~/components/topCategories";
import PostForm from "~/components/postForm";

type HomeData = {
  posts: PostDTO[];
  categories: CategoryDTO[];
};

export async function clientLoader(): Promise<HomeData> {
  return requireAuth(async () => {
    const [postsRes, categoriesRes] = await Promise.all([
      apiFetch<{ content: PostDTO[] }>("/posts?page=0&size=10"),
      apiFetch<CategoryDTO[]>("/categories"),
    ]);

    return {
      posts: postsRes.content,
      categories: categoriesRes,
    };
  });
}

export default function Home({ loaderData }: Route.ComponentProps) {
  const { posts: initialPosts, categories } = loaderData;
  const [posts, setPosts] = useState<PostDTO[]>(initialPosts);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const topCategories = categories.slice(0, 5);

  const loadMore = async () => {
    try {
      setLoading(true);
      const res = await apiFetch<{ content: PostDTO[] }>(
        `/posts?page=${page}&size=10`
      );
      setPosts((prev) => [...prev, ...res.content]);
      setPage((prev) => prev + 1);
    } finally {
      setLoading(false);
    }
  };

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

  return (
    <>
      <Sidebar />
      <div className="col-md-6 second-section" id="page-content-wrapper">
        <ul className="list-unstyled" style={{ marginBottom: 0 }}>
          <PostForm categories={categories} onPostCreated={handlePostCreated} />
        </ul>
        <div className="posts-section mb-5">
          <div id="post-container">
            <PostList posts={posts} onLikeToggled={handleLikeToggled} />
          </div>
          <div className="text-center mt-3">
            <button id="load-more" type="button" className="btn btn-primary" onClick={loadMore} disabled={loading}>
              {loading ? "Loading..." : "Cargar m&aacute;s"}
            </button>
          </div>
        </div>
      </div>
      <TopCategories categories={topCategories} />
    </>
  );
}
