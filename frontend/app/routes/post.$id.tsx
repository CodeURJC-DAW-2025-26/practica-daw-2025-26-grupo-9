import { useState } from "react";
import type { Route } from "./+types/post.$id";
import { useNavigate, Link } from "react-router";
import { p } from "~/utils/paths";
import { useAuthStore } from "~/store/authStore";
import {
  getPost,
  togglePostLike,
  createComment,
  deletePost,
} from "~/services/posts.service";
import { apiFetch } from "~/services/api";
import type { PostDTO, CommentDTO } from "~/dto/PostDTO";
import Sidebar from "~/components/sidebar";
import { requireAuth } from "~/utils/authGuard";

export async function clientLoader({ params }: Route.LoaderArgs) {
  return requireAuth(async () => {
    const post = await getPost(Number(params.id));
    return { post };
  });
}

export default function PostDetail({ loaderData }: Route.ComponentProps) {
  const { post: initialPost } = loaderData;
  const user = useAuthStore((s) => s.user);
  const navigate = useNavigate();
  const [post, setPost] = useState<PostDTO>(initialPost);
  const [commentContent, setCommentContent] = useState("");
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editCommentContent, setEditCommentContent] = useState("");
  const [error, setError] = useState("");

  const isOwner = user?.id === post.userId;
  const isAdmin = user?.roles?.includes("ROLE_ADMIN");

  const refreshPost = async () => {
    const updated = await getPost(post.id);
    setPost(updated);
  };

  const handleLikePost = async () => {
    await togglePostLike(post.id);
    await refreshPost();
  };

  const handleCommentLike = async (commentId: number) => {
    await apiFetch(`/comments/${commentId}/like`, { method: "POST" });
    await refreshPost();
  };

  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentContent.trim() || !user) return;
    try {
      await createComment(post.id, commentContent);
      setCommentContent("");
      setError("");
      await refreshPost();
    } catch {
      setError("Failed to add comment");
    }
  };

  const startEditComment = (comment: CommentDTO) => {
    setEditingCommentId(comment.id);
    setEditCommentContent(comment.content);
  };

  const cancelEditComment = () => {
    setEditingCommentId(null);
    setEditCommentContent("");
  };

  const saveEditComment = async (commentId: number) => {
    if (!editCommentContent.trim()) return;
    try {
      await apiFetch(`/comments/${commentId}`, {
        method: "PUT",
        body: JSON.stringify({ content: editCommentContent }),
      });
      setEditingCommentId(null);
      setEditCommentContent("");
      await refreshPost();
    } catch {
      setError("Failed to edit comment");
    }
  };

  const deleteComment = async (commentId: number) => {
    if (!confirm("¿Eliminar este comentario?")) return;
    try {
      await apiFetch(`/comments/${commentId}`, { method: "DELETE" });
      setError("");
      await refreshPost();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error al eliminar el comentario");
    }
  };

  const handleDeletePost = async () => {
    if (!confirm("Are you sure you want to delete this post?")) return;
    try {
      await deletePost(post.id);
      navigate(p("/"));
    } catch {
      setError("Failed to delete post");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-7 second-section" id="page-content-wrapper">
        {error && <div className="alert alert-danger">{error}</div>}

        <div className="post border-bottom p-3 bg-white w-shadow">
          <div className="media text-muted pt-3">
            <img src={`/api/v1/users/${post.userId}/profile-picture`}
              className="mr-3 post-user-image rounded-circle"
              alt={post.userNickname}
              onError={(e) => { (e.target as HTMLImageElement).src = "/assets/images/users/user-4.jpg"; }} />
            <div className="media-body pb-3 mb-0 small lh-125">
              <div className="d-flex justify-content-between align-items-center w-100">
                <Link to={p(`/users/${post.userId}`)} className="text-gray-dark post-user-name">
                  {post.userNickname}
                </Link>
                {(isOwner || isAdmin) && (
                  <div className="d-flex gap-2">
                    {isOwner && (
                      <Link to={p(`/posts/${post.id}/edit`)} className="btn btn-outline-primary btn-sm">
                        Edit
                      </Link>
                    )}
                    <button className="btn btn-outline-danger btn-sm" onClick={handleDeletePost}>
                      Delete
                    </button>
                  </div>
                )}
              </div>
              <span className="d-block">{post.createdAt} <i className='bx bx-globe ml-3'></i></span>
              <span className="badge bg-secondary">{post.categoryName}</span>
            </div>
          </div>
          <div className="mt-3">
            <p>{post.content}</p>
          </div>

          <div className="mb-3">
            <a href="#" className="post-card-buttons" onClick={(e) => { e.preventDefault(); handleLikePost(); }}>
              <i className={`bx ${post.likedByCurrentUser ? 'bxs-like' : 'bx-like'} mr-2`}></i> {post.likesCount}
            </a>
            <a href="#" className="post-card-buttons" onClick={(e) => e.preventDefault()}>
              <i className='bx bx-message-rounded mr-2'></i> {post.comments?.length || 0}
            </a>
          </div>
        </div>

        <h5 className="mb-3 mt-4">Comments ({post.comments?.length || 0})</h5>

        {post.comments?.map((comment: CommentDTO) => {
          const isCommentOwner = user?.id === comment.userId;
          const canModify = isCommentOwner || isAdmin;

          return (
            <div className="post border-bottom p-3 bg-white w-shadow" key={comment.id}>
              <div className="media text-muted pt-3">
                <Link to={p(`/users/${comment.userId}`)} className="pull-left">
                  <img src={`/api/v1/users/${comment.userId}/profile-picture`}
                    className="mr-3 post-user-image rounded-circle" width="32" height="32" alt="" />
                </Link>
                <div className="media-body pb-3 mb-0 small lh-125">
                  <div className="d-flex justify-content-between align-items-start">
                    <div>
                      <strong>
                        <Link to={p(`/users/${comment.userId}`)}>{comment.userNickname}</Link>
                      </strong>
                      <span className="d-block comment-created-time">
                        {comment.createdAt}
                      </span>
                    </div>
                    {canModify && (
                      <div className="d-flex gap-2">
                        {isCommentOwner && (
                          <button className="btn btn-outline-primary btn-sm"
                            onClick={() => startEditComment(comment)}>
                            Editar
                          </button>
                        )}
                        <button className="btn btn-outline-danger btn-sm"
                          onClick={() => deleteComment(comment.id)}>
                          Eliminar
                        </button>
                      </div>
                    )}
                  </div>
                  {editingCommentId === comment.id ? (
                    <div className="mt-1 d-flex gap-2">
                      <input type="text" className="form-control form-control-sm"
                        value={editCommentContent}
                        onChange={(e) => setEditCommentContent(e.target.value)}
                        autoFocus />
                      <button className="btn btn-sm btn-success"
                        onClick={() => saveEditComment(comment.id)}>Guardar</button>
                      <button className="btn btn-sm btn-secondary"
                        onClick={cancelEditComment}>Cancelar</button>
                    </div>
                  ) : (
                    <p className="mb-1">{comment.content}</p>
                  )}
                  <a href="#" className="post-card-buttons" onClick={(e) => { e.preventDefault(); handleCommentLike(comment.id); }}>
                    <i className={`bx ${comment.likedByCurrentUser ? 'bxs-like' : 'bx-like'} mr-1`}></i> {comment.likesCount}
                  </a>
                </div>
              </div>
            </div>
          );
        })}

        {user ? (
          <div className="mt-3">
            <form onSubmit={handleAddComment}>
              <div className="input-group">
                <input type="text" name="content" className="form-control comment-input"
                  placeholder="Write a comment..."
                  value={commentContent}
                  onChange={(e) => setCommentContent(e.target.value)} />
                <div className="input-group-btn">
                  <button type="submit" className="btn comment-form-btn" title="Send">
                    <i className='bx bx-send'></i>
                  </button>
                </div>
              </div>
            </form>
          </div>
        ) : (
          <div className="mt-3 text-center">
            <p className="text-muted mb-2">Debes iniciar sesi&oacute;n para comentar</p>
            <Link to={p("/login")} className="btn btn-sm btn-outline-primary">Iniciar sesi&oacute;n</Link>
          </div>
        )}
      </div>
    </>
  );
}
