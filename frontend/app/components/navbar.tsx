import { useAuthStore } from "~/store/authStore";
import { useNavigate, Link } from "react-router";
import { p } from "~/utils/paths";

export default function Navbar() {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate(p("/login"));
  };

  return (
    <nav id="navbar-main" className="navbar navbar-expand-lg shadow-sm sticky-top">
      <div className="w-100 justify-content-md-center">
        <ul className="nav navbar-nav enable-mobile px-2">
          <li className="nav-item">
            <Link to={p("/")} className="btn nav-link p-0">
              <img src="/assets/images/icons/theme/post-image.png" className="f-nav-icon" alt="Quick make post" />
            </Link>
          </li>
          <li className="nav-item w-100 py-2">
            <div className="d-inline form-inline w-100 px-4">
              <div className="input-group">
                <input type="text" className="form-control search-input" name="q"
                  placeholder="Search for people, companies, events and more..." aria-label="Search"
                  aria-describedby="search-addon" />
                <div className="input-group-append">
                  <button className="btn search-button" type="button"><i className='bx bx-search'></i></button>
                </div>
              </div>
            </div>
          </li>
          <li className="nav-item">
            <a href="/messages" className="nav-link nav-icon nav-links message-drop drop-w-tooltip"
              data-placement="bottom" data-title="Messages">
              <img src="/assets/images/icons/navbar/message.png" className="message-dropdown f-nav-icon"
                alt="navbar icon" />
            </a>
          </li>
        </ul>

        <ul className="navbar-nav mr-5 flex-row" id="main_menu">
          <Link className="navbar-brand nav-item mr-lg-5" to={p("/")}>
            <img src="/assets/images/logo-64x64.png" width="40" height="40" className="mr-3" alt="Logo" />
          </Link>

          {user ? (
            <>
              <li className="nav-item s-nav ml-auto">
                <Link to={p(`/users/${user.id}`)} className="nav-link nav-links">
                  <div className="menu-user-image">
                    <img src={`/api/v1/users/${user.id}/profile-picture`}
                      className="menu-user-img ml-1" alt="Menu Image"
                      onError={(e) => { (e.target as HTMLImageElement).src = "/assets/images/users/user-4.jpg"; }} />
                  </div>
                </Link>
              </li>
              <li className="nav-item s-nav nav-icon">
                <button type="button"
                  style={{ border: 0, background: "transparent", padding: 0, cursor: "pointer" }}
                  onClick={handleLogout}>
                  <img src="/assets/images/icons/navbar/logout.png" className="nav-settings" alt="Logout" />
                </button>
              </li>
            </>
          ) : (
            <>
              <li className="nav-item s-nav ml-auto">
                <Link to={p("/login")} className="nav-link nav-links">Inicia sesi&oacute;n</Link>
              </li>
              <li className="nav-item s-nav">
                <Link to={p("/register")} className="nav-link nav-links">Reg&iacute;strate</Link>
              </li>
            </>
          )}

          <button type="button" className="btn nav-link" id="menu-toggle">
            <img src="/assets/images/icons/theme/navs.png" alt="Navbar navs" />
          </button>
        </ul>
      </div>
    </nav>
  );
}
