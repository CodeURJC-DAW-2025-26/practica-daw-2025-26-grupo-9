import { useLocation, Link } from "react-router";
import { useAuthStore } from "~/store/authStore";
import { p } from "~/utils/paths";

export default function Sidebar() {
  const user = useAuthStore((s) => s.user);
  const isAdmin = user?.roles?.includes("ROLE_ADMIN");
  const location = useLocation();
  const path = location.pathname;

  const isActive = (prefix: string) => path.startsWith(prefix) ? "sd-active" : "";

  return (
    <div className="col-md-2 newsfeed-left-side sticky-top shadow-sm" id="sidebar-wrapper">
      <div className="card newsfeed-user-card h-100">
        <ul className="list-group list-group-flush newsfeed-left-sidebar">
          <li className={`list-group-item d-flex justify-content-between align-items-center ${isActive("/new/") && !isActive("/new/users/") && !isActive("/new/admin") && !isActive("/new/categories") && !isActive("/new/stats") ? "sd-active" : ""}`}>
            <Link to={p("/")} className="sidebar-item">
              <img src="/assets/images/icons/left-sidebar/home-icon-silhouette.png" alt="Home icon" />
              Inicio
            </Link>
          </li>

          {user && (
            <li className={`list-group-item d-flex justify-content-between align-items-center ${isActive("/new/users/") ? "sd-active" : ""}`}>
              <Link to={p(`/users/${user.id}`)} className="sidebar-item">
                <img src="/assets/images/icons/left-sidebar/Person.png" alt="Profile icon" />
                Perfil
              </Link>
            </li>
          )}

          {isAdmin && (
            <li className={`list-group-item d-flex justify-content-between align-items-center ${isActive("/new/admin") ? "sd-active" : ""}`}>
              <Link to={p("/admin")} className="sidebar-item">
                <img src="/assets/images/icons/left-sidebar/group.png" alt="Admin icon" />
                Administrador
              </Link>
            </li>
          )}

          <li className={`list-group-item d-flex justify-content-between align-items-center ${isActive("/new/categories") ? "sd-active" : ""}`}>
            <Link to={p("/categories")} className="sidebar-item">
              <img src="/assets/images/icons/left-sidebar/team.png" alt="Categories icon" />
              Categor&iacute;as
            </Link>
          </li>

          <li className={`list-group-item d-flex justify-content-between align-items-center ${isActive("/new/stats") ? "sd-active" : ""}`}>
            <Link to={p("/stats")} className="sidebar-item">
              <img src="/assets/images/icons/left-sidebar/news.png" alt="Stats icon" />
              Estad&iacute;sticas
            </Link>
          </li>
        </ul>
      </div>
    </div>
  );
}
