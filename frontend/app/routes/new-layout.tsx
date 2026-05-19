import { isRouteErrorResponse, useLocation, Outlet, useRouteError, Link } from "react-router";
import Navbar from "~/components/navbar";
import GlobalSpinner from "~/components/Spinner";
import { p } from "~/utils/paths";

export function ErrorBoundary() {
  const error = useRouteError();

  let status = 500;
  let title = "Error interno del servidor";
  let icon = "bx-error-circle";

  if (isRouteErrorResponse(error)) {
    status = error.status;
    if (status === 403) {
      title = "No tienes acceso al recurso especificado.";
      icon = "bx-lock-alt";
    } else if (status === 404) {
      title = "El recurso que estás buscando no se pudo encontrar.";
      icon = "bx-search-alt-2";
    } else {
      title = error.statusText || title;
    }
  }

  return (
    <div className="container-fluid text-center py-5" style={{ minHeight: "60vh" }}>
      <div className="mb-4">
        <i className={`bx ${icon} text-danger`} style={{ fontSize: "120px" }}></i>
      </div>
      <h1 className="display-4 text-danger fw-bolder mb-4">
        Error {status}
      </h1>
      <p className="mb-5 fs-4 text-muted">
        {title}
      </p>
      <Link to={p("/")} className="btn btn-outline-primary btn-lg px-5 py-3 fs-4 d-inline-flex align-items-center">
        <i className='bx bx-home me-2' style={{ fontSize: "1.2em" }}></i>
        Volver al inicio
      </Link>
    </div>
  );
}

export default function NewLayout() {
  const location = useLocation();

  const hideAuthLayout =
    location.pathname === "/new/login" ||
    location.pathname === "/new/register";

  return (
    <>
      <GlobalSpinner />

      {!hideAuthLayout ? (
        <div className="row newsfeed-size">
          <div className="col-md-12 newsfeed-right-side">

            <Navbar />

            <div className="row newsfeed-right-side-content mt-3">
              <Outlet />
            </div>

          </div>
        </div>
      ) : (
        <Outlet />
      )}
    </>
  );
}
