import { useState } from "react";
import type { Route } from "./+types/admin.create-category";
import { useNavigate, Link } from "react-router";
import { createCategory, uploadCategoryImage } from "~/services/categories.service";
import { requireAuth } from "~/utils/authGuard";
import { useAuthStore } from "~/store/authStore";
import Sidebar from "~/components/sidebar";
import { p } from "~/utils/paths";
import { Navigate } from "react-router";

export async function clientLoader() {
  return requireAuth(async () => ({}));
}

export default function CreateCategory({ loaderData }: Route.ComponentProps) {
  const user = useAuthStore((s) => s.user);
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [image, setImage] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  if (!user?.roles?.includes("ROLE_ADMIN")) {
    return <Navigate to="/" replace />;
  }

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null;
    setImage(file);
    if (file) {
      const reader = new FileReader();
      reader.onload = () => setPreview(reader.result as string);
      reader.readAsDataURL(file);
    } else {
      setPreview(null);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      const cat = await createCategory({ name, description });
      if (image) {
        await uploadCategoryImage(cat.id, image);
      }
      setSuccess("Categor&iacute;a creada correctamente");
      setTimeout(() => navigate(p("/admin")), 1500);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to create category");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-9 d-flex justify-content-center align-items-start min-vh-100 mt-4">
        <div className="col-md-6">
          <div className="settings-form p-4 shadow bg-white rounded">
            <h2 className="text-center mb-2">Crear nueva categor&iacute;a</h2>
            <p className="text-center text-muted mb-4">A&ntilde;ade una nueva categor&iacute;a a eQuis</p>

            {error && <div className="alert alert-danger">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Nombre</label>
                <input type="text" className="form-control" placeholder="Ej: Tecnolog&iacute;a"
                  value={name} onChange={(e) => setName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Descripci&oacute;n</label>
                <textarea className="form-control" rows={3} placeholder="Describe esta categor&iacute;a..."
                  value={description} onChange={(e) => setDescription(e.target.value)} />
              </div>
              <div className="form-group">
                <label>Imagen de la categor&iacute;a</label>
                <input type="file" accept="image/*" className="form-control-file"
                  onChange={handleImageChange} />
                {preview && (
                  <div className="mt-2">
                    <img src={preview} alt="Preview" style={{ maxHeight: 100, borderRadius: 8 }} />
                  </div>
                )}
              </div>
              <div className="d-flex justify-content-between align-items-center mt-4">
                <Link to={p("/admin")} className="btn btn-secondary btn-sm">Cancelar</Link>
                <button type="submit" className="btn btn-success btn-sm">Crear categor&iacute;a</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}
