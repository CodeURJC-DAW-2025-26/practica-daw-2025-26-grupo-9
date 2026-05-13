import { useState } from "react";
import { Link } from "react-router";
import type { PostDTO } from "~/dto/PostDTO";
import { apiFetch } from "~/services/api";
import { useAuthStore } from "~/store/authStore";
import { p } from "~/utils/paths";

type PostProps = {
  post: PostDTO;
  onLikeToggled: (postId: number, liked: boolean, newCount: number) => void;
};

export default function Post({ post, onLikeToggled }: PostProps) {
  const user = useAuthStore((s) => s.user);
  const [showComments, setShowComments] = useState(false);
  const [commentText, setCommentText] = useState("");

  const likePost = async () => {
    if (!user) return;
    try {
      await apiFetch(`/posts/${post.id}/like`, { method: "POST" });
      const updated = await apiFetch<PostDTO>(`/posts/${post.id}`);
      onLikeToggled(post.id, updated.likedByCurrentUser, updated.likesCount);
    } catch {
      // silently fail
    }
  };

  const addComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !commentText.trim()) return;
    try {
      await apiFetch(`/posts/${post.id}/comments`, {
        method: "POST",
        body: JSON.stringify({ content: commentText }),
      });
      setCommentText("");
      const updated = await apiFetch<PostDTO>(`/posts/${post.id}`);
      onLikeToggled(post.id, updated.likedByCurrentUser, updated.likesCount);
    } catch {
      // silently fail
    }
  };

  return (
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
            <div className="dropdown">
              <a href="#" className="post-more-settings" role="button" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false" onClick={(e) => e.preventDefault()}>
                <i className='bx bx-dots-horizontal-rounded'></i>
              </a>
            </div>
          </div>
          <span className="d-block">{new Date(post.createdAt).toLocaleDateString()} <i className='bx bx-globe ml-3'></i></span>
        </div>
      </div>
      <div className="mt-3">
        <p>{post.content}</p>
      </div>
      {post.id && (
        <div className="d-block mt-3">
          <img src={`/api/v1/posts/${post.id}/image`} className="post-content" alt="post image"
            onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }} />
        </div>
      )}
      <div className="mb-3">
        <span className="like-btn">
          <a href="#" className="post-card-buttons" onClick={(e) => { e.preventDefault(); likePost(); }}>
            <i className={`bx ${post.likedByCurrentUser ? 'bxs-like' : 'bx-like'} mr-2`}></i> {post.likesCount}
          </a>
        </span>
        <a href="#" className="post-card-buttons show-comments-btn"
          onClick={(e) => { e.preventDefault(); setShowComments(!showComments); }}>
          <i className='bx bx-message-rounded mr-2'></i> {post.comments?.length || 0}
        </a>
        <Link to={p(`/posts/${post.id}`)} className="post-card-buttons">
          <i className='bx bx-link-external mr-2'></i>
        </Link>
      </div>

      {showComments && (
        <div className="border-top pt-3">
          <div className="row bootstrap snippets">
            <div className="col-md-12">
              <div className="comment-wrapper">
                <div className="panel panel-info">
                  <div className="panel-body">
                    <ul className="media-list comments-list">
                      {user ? (
                        <li className="media comment-form">
                          <Link to={p(`/users/${user.id}`)} className="pull-left">
                            <img src={`/api/v1/users/${user.id}/profile-picture`}
                              className="mr-3 post-user-image rounded-circle" width="32" height="32"
                              alt="" />
                          </Link>
                          <div className="media-body">
                            <form onSubmit={addComment}>
                              <div className="row">
                                <div className="col-md-12">
                                  <div className="input-group">
                                    <input type="text" name="content"
                                      className="form-control comment-input"
                                      placeholder="Write a comment..."
                                      value={commentText}
                                      onChange={(e) => setCommentText(e.target.value)} />
                                    <div className="input-group-btn">
                                      <button type="submit" className="btn comment-form-btn" title="Send">
                                        <i className='bx bx-send'></i>
                                      </button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </form>
                          </div>
                        </li>
                      ) : (
                        <li className="media">
                          <div className="media-body text-center">
                            <p className="text-muted mb-2">Debes iniciar sesi&oacute;n para comentar</p>
                            <Link to={p("/login")} className="btn btn-sm btn-outline-primary">
                              Iniciar sesi&oacute;n
                            </Link>
                          </div>
                        </li>
                      )}

                      {post.comments?.length ? (
                        post.comments.map((comment) => (
                          <li className="media" key={comment.id}>
                            <Link to={p(`/users/${comment.userId}`)} className="pull-left">
                              <img src={`/api/v1/users/${comment.userId}/profile-picture`}
                                className="mr-3 post-user-image rounded-circle" width="32" height="32" alt="" />
                            </Link>
                            <div className="media-body">
                              <strong>
                                <Link to={p(`/users/${comment.userId}`)}>{comment.userNickname}</Link>
                              </strong>
                              <span className="d-block comment-created-time">
                                {new Date(comment.createdAt).toLocaleDateString()}
                              </span>
                              <p>{comment.content}</p>
                            </div>
                          </li>
                        ))
                      ) : (
                        <li className="media">
                          <div className="media-body">
                            <p className="text-muted fs-8 mb-0">No comments yet.</p>
                          </div>
                        </li>
                      )}
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
