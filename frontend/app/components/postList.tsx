import Post from "./post";
import type { PostDTO } from "~/dto/PostDTO";

type PostListProps = {
  posts: PostDTO[];
  onLikeToggled: (postId: number, liked: boolean, newCount: number) => void;
};

export default function PostList({ posts, onLikeToggled }: PostListProps) {
  if (!posts?.length) {
    return (
      <div className="post border-bottom p-3 bg-white w-shadow">
        <p className="mb-0 text-muted">Todav&iacute;a no hay posts. S&eacute; la primera persona en publicar algo 🙂</p>
      </div>
    );
  }

  return (
    <>
      {posts.map((post) => (
        <Post key={post.id} post={post} onLikeToggled={onLikeToggled} />
      ))}
    </>
  );
}
