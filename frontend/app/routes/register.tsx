import { useState } from "react";
import type { Route } from "./+types/register";
import { useAuthStore } from "~/store/authStore";
import { useNavigate, Link } from "react-router";
import { p } from "~/utils/paths";

export function meta() {
  return [
    { title: "eQuis - Red Social" },
  ];
}

export default function Register() {
  const register = useAuthStore((s) => s.register);
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [nickname, setNickname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await register({ name, surname, nickname, email, password });
      navigate(p("/login"));
    } catch (err: unknown) {
      setError(
        err instanceof Error ? err.message : "Registration failed"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row no-gutters auth-wrap" style={{ width: "100%" }}>
      <div className="col-md-6 auth-hero">
        <div className="hero-inner">
          <div>
            <h1 className="hero-title">Bienvenido a eQuis</h1>
            <p className="hero-sub">
              La red social donde tus ideas pesan. Publica, comenta y conecta.
            </p>
          </div>
        </div>
      </div>

      <div className="col-md-6 auth-side">
        <div className="auth-card">
          <h2 className="headline">Crear cuenta</h2>
          <p className="subline">&Uacute;nete a la comunidad</p>

          {error && (
            <div className="alert alert-danger">{error}</div>
          )}

          <form onSubmit={handleSubmit} autoComplete="on">
            <div className="form-group input-icon">
              <i className='bx bx-user'></i>
              <input type="text" className="form-control" placeholder="Nombre" required
                value={name} onChange={(e) => setName(e.target.value)} />
            </div>

            <div className="form-group input-icon">
              <i className='bx bx-user-circle'></i>
              <input type="text" className="form-control" placeholder="Apellidos" required
                value={surname} onChange={(e) => setSurname(e.target.value)} />
            </div>

            <div className="form-group input-icon">
              <i className='bx bx-at'></i>
              <input type="text" className="form-control" placeholder="Nickname" required
                value={nickname} onChange={(e) => setNickname(e.target.value)} />
            </div>

            <div className="form-group input-icon">
              <i className='bx bx-envelope'></i>
              <input type="email" className="form-control" placeholder="Correo electr&oacute;nico" required
                value={email} onChange={(e) => setEmail(e.target.value)} />
            </div>

            <div className="form-group input-icon">
              <i className='bx bx-lock-alt'></i>
              <input type="password" className="form-control" placeholder="Contrase&ntilde;a" required
                value={password} onChange={(e) => setPassword(e.target.value)} />
            </div>

            <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
              {loading ? "Creando..." : "Crear cuenta"}
            </button>

            <div className="text-center mt-4 tiny-link">
              &iquest;Ya tienes cuenta? <Link to={p("/login")} style={{ fontWeight: 700 }}>Inicia sesi&oacute;n</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
