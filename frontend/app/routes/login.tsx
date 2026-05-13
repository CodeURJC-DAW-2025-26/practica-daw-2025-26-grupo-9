import { useState } from "react";
import type { Route } from "./+types/login";
import { useAuthStore } from "~/store/authStore";
import { useNavigate, Link } from "react-router";
import { p } from "~/utils/paths";

export function meta() {
  return [
    { title: "eQuis - Red Social" },
  ];
}

export default function Login() {
  const login = useAuthStore((s) => s.login);
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await login(email, password);
      navigate(p("/"));
    } catch (err: unknown) {
      setError(
        err instanceof Error ? err.message : "Invalid credentials"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row no-gutters auth-wrap" style={{ width: "100%" }}>
      <div className="col-md-6 auth-hero">
        <div className="hero-lines"></div>
        <div className="hero-inner">
          <div>
            <h1 className="hero-title">Bienvenido a <span style={{ color: "rgba(255,255,255,.92)" }}>eQuis</span></h1>
            <p className="hero-sub">
              La red social donde tus ideas pesan. Publica, comenta, debate y descubre gente con tus mismos gustos.
            </p>
          </div>
        </div>
      </div>

      <div className="col-md-6 auth-side">
        <div className="auth-card">
          <div className="brand-row">
            <div className="brand-mark" aria-hidden="true"><i className='bx bx-message-rounded-dots'></i></div>
            <div>
              <p className="brand-name">Red Social eQuis</p>
              <p className="brand-tag">Comparte lo que te apasiona</p>
            </div>
          </div>

          <h2 className="headline">Empieza a compartir tus gustos</h2>
          <p className="subline">Entra con tu cuenta y &uacute;nete a la conversaci&oacute;n.</p>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group input-icon">
              <i className='bx bx-user'></i>
              <input type="text" className="form-control" placeholder="Email" required
                value={email} onChange={(e) => setEmail(e.target.value)} />
            </div>

            <div className="form-group input-icon">
              <i className='bx bx-lock-alt'></i>
              <input type="password" className="form-control" placeholder="Contrase&ntilde;a" required
                value={password} onChange={(e) => setPassword(e.target.value)} />
            </div>

            <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
              {loading ? "Entrando..." : "Entrar"}
            </button>

            <div className="text-center mt-4 tiny-link">
              &iquest;No tienes usuario? <Link to={p("/register")} style={{ fontWeight: 700 }}>Reg&iacute;strate</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
