import { useState } from "react";
import type { Route } from "./+types/admin";
import { useAuthStore } from "~/store/authStore";
import { getUsers } from "~/services/users.service";
import { getCategories } from "~/services/categories.service";
import { updateUserStatus, deleteUser } from "~/services/admin.service";
import { deleteCategory } from "~/services/categories.service";
import type { UserDTO } from "~/dto/UserDTO";
import type { CategoryDTO } from "~/dto/PostDTO";
import Sidebar from "~/components/sidebar";
import { Navigate, Link } from "react-router";
import { requireAuth } from "~/utils/authGuard";
import { p } from "~/utils/paths";

type AdminData = {
  usersPage: { content: UserDTO[] };
  categories: CategoryDTO[];
};

export async function clientLoader(): Promise<AdminData> {
  return requireAuth(async () => {
    const [usersPage, categories] = await Promise.all([
      getUsers(0, 50),
      getCategories(),
    ]);
    return { usersPage, categories };
  });
}

export function meta() {
  return [{ title: "Admin Panel - eQuis" }];
}

export default function Admin({ loaderData }: Route.ComponentProps) {
  const { usersPage: initialPage, categories: initialCategories } = loaderData as AdminData;
  const user = useAuthStore((s) => s.user);
  const [users, setUsers] = useState<UserDTO[]>(initialPage.content);
  const [categories, setCategories] = useState<CategoryDTO[]>(initialCategories);
  const [loading, setLoading] = useState(false);

  const isAdmin = user?.roles?.includes("ROLE_ADMIN");
  if (!isAdmin) return <Navigate to="/" replace />;

  const handleToggleActive = async (userId: number, currentActive: boolean) => {
    const updated = await updateUserStatus(userId, !currentActive);
    setUsers((prev) => prev.map((u) => (u.id === userId ? updated : u)));
  };

  const handleDeleteUser = async (userId: number) => {
    if (!confirm("Are you sure you want to delete this user?")) return;
    await deleteUser(userId);
    setUsers((prev) => prev.filter((u) => u.id !== userId));
  };

  const handleDeleteCategory = async (categoryId: number) => {
    if (!confirm("Are you sure you want to delete this category?")) return;
    try {
      await deleteCategory(categoryId);
      setCategories((prev) => prev.filter((c) => c.id !== categoryId));
    } catch {
      alert("Cannot delete this category");
    }
  };

  return (
    <>
      <Sidebar />
      <div className="col-md-10 second-section" id="page-content-wrapper">
        <div className="mb-4">
          <div className="btn-group d-flex top-links-fg">
            <a href="#" className="btn btn-quick-links mr-3 ql-active" onClick={(e) => e.preventDefault()}>
              <img src="/assets/images/icons/theme/group-white.png" className="mr-2" alt="icon" />
              <span className="fs-8">ADMINISTRADOR</span>
            </a>
          </div>
        </div>

        <div className="groups bg-white shadow-sm p-4 mb-5">
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h5>Categor&iacute;as</h5>
            <Link to={p("/admin/categories/new")} className="btn btn-sm btn-primary">Crear</Link>
          </div>
          <div className="row">
            {categories.map((cat) => (
              <div className="col-md-3 col-sm-6 mb-4" key={cat.id}>
                <div className="card group-card shadow-sm h-100">
                  <Link to={p(`/categories/${cat.id}`)}>
                    <img src={`/api/v1/categories/${cat.id}/image`}
                      className="card-img-top group-card-image" alt="Category image"
                      onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }} />
                  </Link>
                  <div className="card-body text-center">
                    <h6 className="card-title">{cat.name}</h6>
                    <div className="d-flex justify-content-center gap-2">
                      <Link to={p(`/admin/categories/${cat.id}/edit`)} className="btn btn-sm btn-outline-primary">
                        Editar
                      </Link>
                      <button className="btn btn-sm btn-outline-danger"
                        onClick={() => handleDeleteCategory(cat.id)}>
                        Eliminar
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
            {categories.length === 0 && (
              <div className="col-12 text-center text-muted">No hay categor&iacute;as registradas.</div>
            )}
          </div>
        </div>

        <div className="groups bg-white shadow-sm p-4">
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h5>Usuarios</h5>
          </div>
          <div className="row">
            {users.map((u) => (
              <div className="col-md-3 col-sm-6 mb-4" key={u.id}>
                <div className="card group-card shadow-sm h-100">
                  <Link to={p(`/users/${u.id}`)}>
                    <img src={`/api/v1/users/${u.id}/profile-picture`}
                      className="card-img-top group-card-image" alt="User image"
                      onError={(e) => { (e.target as HTMLImageElement).src = "/assets/images/users/user-4.jpg"; }} />
                  </Link>
                  <div className="card-body text-center">
                    <h6 className="card-title">{u.name}</h6>
                    {user?.id !== u.id && (
                      <>
                        <button className={`btn btn-sm ${u.active ? "btn-success" : "btn-danger"} mb-2`}
                          onClick={() => handleToggleActive(u.id, u.active)}>
                          {u.active ? "Activo" : "Bloqueado"}
                        </button>
                        <br />
                        <button className="btn btn-sm btn-outline-danger"
                          onClick={() => handleDeleteUser(u.id)}>
                          Eliminar
                        </button>
                      </>
                    )}
                    {user?.id === u.id && (
                      <span className="text-muted small">(t&uacute;)</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
            {users.length === 0 && (
              <div className="col-12 text-center text-muted">No hay usuarios disponibles.</div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
