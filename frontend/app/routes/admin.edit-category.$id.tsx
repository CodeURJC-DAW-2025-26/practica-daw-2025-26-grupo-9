import { useState, useEffect } from "react";
import type { Route } from "./+types/admin.edit-category.$id";
import { useNavigate, Link } from "react-router";
import { getCategory, updateCategory } from "~/services/categories.service";
import { requireAuth } from "~/utils/authGuard";
import { useAuthStore } from "~/store/authStore";
import Sidebar from "~/components/sidebar";
import { p } from "~/utils/paths";
import { Navigate } from "react-router";

export async function clientLoader({ params }: Route.LoaderArgs) {
  return requireAuth(async () => {
    const category = await getCategory(Number(params.id));
    return { category };
  });
}

export default function EditCategory({ loaderData }: Route.ComponentProps) {
  const { category } = loaderData;
  const user = useAuthStore((s) => s.user);
  const navigate = useNavigate();
  const [name, setName] = useState(category.name);
  const [description, setDescription] = useState(category.description || "");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  if (!user?.roles?.includes("ROLE_ADMIN")) {
    return <Navigate to="/" replace />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      await updateCategory(category.id, { name, description });
      setSuccess("Categor&iacute;a actualizada correctamente");
      setTimeout(() => navigate(p("/admin")), 1500);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to update category");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-9 d-flex justify-content-center align-items-start min-vh-100 mt-4">
        <div className="col-md-6">
          <div className="settings-form p-4 shadow bg-white rounded">
            <h2 className="text-center mb-2">Editar categor&iacute;a</h2>
            <p className="text-center text-muted mb-4">Modifica el nombre, descripci&oacute;n o imagen</p>

            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Nombre</label>
                <input type="text" className="form-control" value={name}
                  onChange={(e) => setName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Descripci&oacute;n</label>
                <textarea className="form-control" rows={3} placeholder="Describe la categor&iacute;a..."
                  value={description} onChange={(e) => setDescription(e.target.value)} />
              </div>
              <div className="d-flex justify-content-between align-items-center mt-4">
                <Link to={p("/admin")} className="btn btn-secondary btn-sm">Cancelar</Link>
                <button type="submit" className="btn btn-primary btn-sm">Guardar cambios</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}
