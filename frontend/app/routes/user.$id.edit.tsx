import { useState } from "react";
import type { Route } from "./+types/user.$id.edit";
import { useNavigate, Link } from "react-router";
import { useAuthStore } from "~/store/authStore";
import { updateProfile, getProfilePictureUrl, getCoverPictureUrl } from "~/services/users.service";
import { requireAuth } from "~/utils/authGuard";
import Sidebar from "~/components/sidebar";
import { p } from "~/utils/paths";

export async function clientLoader() {
  return requireAuth(async () => ({}));
}

export default function EditProfile({ loaderData }: Route.ComponentProps) {
  const user = useAuthStore((s) => s.user);
  const navigate = useNavigate();
  const [name, setName] = useState(user?.name || "");
  const [surname, setSurname] = useState(user?.surname || "");
  const [email, setEmail] = useState(user?.email || "");
  const [description, setDescription] = useState(user?.description || "");
  const [password, setPassword] = useState("");
  const [profileImage, setProfileImage] = useState<File | null>(null);
  const [coverImage, setCoverImage] = useState<File | null>(null);
  const [profilePreview, setProfilePreview] = useState<string | null>(null);
  const [coverPreview, setCoverPreview] = useState<string | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>, type: "profile" | "cover") => {
    const file = e.target.files?.[0] ?? null;
    if (type === "profile") {
      setProfileImage(file);
      if (file) {
        const reader = new FileReader();
        reader.onload = () => setProfilePreview(reader.result as string);
        reader.readAsDataURL(file);
      } else {
        setProfilePreview(null);
      }
    } else {
      setCoverImage(file);
      if (file) {
        const reader = new FileReader();
        reader.onload = () => setCoverPreview(reader.result as string);
        reader.readAsDataURL(file);
      } else {
        setCoverPreview(null);
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      const formData = new FormData();
      formData.append("name", name);
      formData.append("surname", surname);
      formData.append("email", email);
      formData.append("description", description);
      if (password) formData.append("password", password);
      if (profileImage) formData.append("profileImage", profileImage);
      if (coverImage) formData.append("coverImage", coverImage);
      await updateProfile(formData);
      setSuccess("Profile updated successfully");
      setTimeout(() => navigate(p(`/users/${user?.id}`)), 1500);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to update profile");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-9 d-flex justify-content-center align-items-start min-vh-100 mt-4">
        <div className="col-md-6">
          <div className="settings-form p-4 shadow bg-white rounded">
            <h2 className="text-center mb-2">Modifica tu cuenta de eQuis</h2>
            <p className="text-center text-muted mb-4">Cambia toda la informaci&oacute;n que necesites</p>

            {success && <div className="alert alert-success">{success}</div>}
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit} autoComplete="on">
              <div className="form-group">
                <label>Nombre</label>
                <input type="text" className="form-control" placeholder="Nombre" value={name}
                  onChange={(e) => setName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Apellidos</label>
                <input type="text" className="form-control" placeholder="Apellidos" value={surname}
                  onChange={(e) => setSurname(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Correo electr&oacute;nico</label>
                <input type="email" className="form-control" placeholder="correo@ejemplo.com" value={email}
                  onChange={(e) => setEmail(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Nombre de usuario (Apodo)</label>
                <input type="text" className="form-control" value={user?.nickname || ""} readOnly />
              </div>
              <div className="form-group">
                <label>Nueva contrase&ntilde;a (opcional)</label>
                <input type="password" className="form-control" placeholder="D&eacute;jalo vac&iacute;o si no quieres cambiarla"
                  value={password} onChange={(e) => setPassword(e.target.value)} />
              </div>
              <div className="form-group">
                <label>Foto de perfil</label>
                <div className="d-flex align-items-center gap-3">
                  <img src={profilePreview || getProfilePictureUrl(user?.id || 0)}
                    className="rounded-circle" width="64" height="64" alt="Profile"
                    style={{ objectFit: "cover" }} />
                  <input type="file" accept="image/*" className="form-control-file"
                    onChange={(e) => handleFileChange(e, "profile")} />
                </div>
              </div>
              <div className="form-group">
                <label>Foto de portada</label>
                <div className="d-flex align-items-center gap-3">
                  <img src={coverPreview || getCoverPictureUrl(user?.id || 0)}
                    width="120" height="60" alt="Cover" style={{ objectFit: "cover", borderRadius: 6 }} />
                  <input type="file" accept="image/*" className="form-control-file"
                    onChange={(e) => handleFileChange(e, "cover")} />
                </div>
              </div>
              <div className="form-group">
                <label>Descripci&oacute;n</label>
                <textarea className="form-control" rows={3} placeholder="Cu&eacute;ntanos algo sobre ti..."
                  value={description} onChange={(e) => setDescription(e.target.value)}></textarea>
              </div>
              <div className="d-flex justify-content-between align-items-center mt-4">
                <Link to={p(`/users/${user?.id}`)} className="btn btn-secondary btn-sm">Cancelar</Link>
                <button type="submit" className="btn btn-primary btn-sm">Guardar cambios</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}
