import { useState } from "react";
import { Card, Button } from "react-bootstrap";

export default function Post({ post }: any) {
  const [showComments, setShowComments] = useState(false);

  const likePost = async () => {
    await fetch(
      `http://localhost:8080/api/v1/posts/${post.id}/like`,
      {
        method: "POST",
        credentials: "include",
      }
    );

    window.location.reload();
  };

  return (
    <Card className="mb-3 shadow-sm">
      <Card.Body>

        <div className="d-flex justify-content-between">
          <a href={`/users/${post.userId}`}>
            {post.userNickname}
          </a>

          <small>
            {new Date(post.createdAt).toLocaleString()}
          </small>
        </div>

        <Card.Text className="mt-3">
          {post.content}
        </Card.Text>

        <div className="d-flex gap-2 mt-2">
          <Button
            variant="outline-primary"
            size="sm"
            onClick={likePost}
          >
            👍 {post.likesCount}
          </Button>

          <Button
            variant="outline-secondary"
            size="sm"
            onClick={() => setShowComments(!showComments)}
          >
            💬 {post.comments?.length || 0}
          </Button>
        </div>

        {showComments && (
          <div className="mt-3">

            {post.comments?.length ? (
              post.comments.map((comment: any) => (
                <Card
                  key={comment.id}
                  className="mb-2"
                >
                  <Card.Body>
                    <strong>
                      {comment.userNickname}
                    </strong>

                    <p className="mb-0">
                      {comment.content}
                    </p>
                  </Card.Body>
                </Card>
              ))
            ) : (
              <p className="text-muted">
                No hay comentarios
              </p>
            )}

          </div>
        )}
      </Card.Body>
    </Card>
  );
}