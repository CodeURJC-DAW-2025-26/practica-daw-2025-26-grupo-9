import { Card } from "react-bootstrap";
import Post from "./post";

export default function PostList({
  posts,
}: any) {

  if (!posts?.length) {
    return (
      <Card className="p-3 shadow-sm">
        <p className="text-muted mb-0">
          Todavía no hay posts 🙂
        </p>
      </Card>
    );
  }

  return (
    <>
      {posts.map((post: any) => (
        <Post
          key={post.id}
          post={post}
        />
      ))}
    </>
  );
}