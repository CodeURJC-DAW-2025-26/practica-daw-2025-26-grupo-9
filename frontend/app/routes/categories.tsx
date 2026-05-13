import type { Route } from "./+types/categories";
import { getCategories } from "~/services/categories.service";
import type { CategoryDTO } from "~/dto/PostDTO";
import { Link } from "react-router";
import Sidebar from "~/components/sidebar";
import { p } from "~/utils/paths";

export async function clientLoader() {
  const categories = await getCategories();
  return { categories };
}

export function meta() {
  return [
    { title: "eQuis - Categorías" },
  ];
}

export default function Categories({ loaderData }: Route.ComponentProps) {
  const { categories } = loaderData;

  const topCategories = [...categories]
    .sort((a, b) => (b.postsCount ?? 0) - (a.postsCount ?? 0))
    .slice(0, 5);

  return (
    <>
      <Sidebar />
      <div className="col-md-8 second-section" id="page-content-wrapper">
        <div className="groups bg-white shadow-sm p-4 mb-5">
          <div className="row">
            {categories.length > 0 ? (
              categories.map((cat: CategoryDTO) => (
                <div className="col-md-3 col-sm-6 mb-4" key={cat.id}>
                  <div className="card group-card shadow-sm h-100">
                    <Link to={p(`/categories/${cat.id}`)}>
                      {cat.imageUrl ? (
                        <img src={cat.imageUrl} className="card-img-top group-card-image" alt={cat.name} />
                      ) : (
                        <div className="card-img-top group-card-image bg-light d-flex align-items-center justify-content-center text-muted" style={{ height: 140 }}>
                          <i className='bx bx-folder-open' style={{ fontSize: "3rem" }}></i>
                        </div>
                      )}
                    </Link>
                    <div className="card-body text-center">
                      <h6 className="card-title">{cat.name}</h6>
                      <small className="text-muted d-block mb-2">
                        {cat.postsCount ?? 0} posts
                      </small>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="col-12 text-center text-muted">
                No hay categorías registradas.
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="col-md-2 newsfeed-right-side-content mt-2">
        <div className="sticky-top newsfeed-right-side-content">
          <h6 className="text-muted mb-3">M&aacute;s populares</h6>
          <ul className="list-group shadow-sm">
            {topCategories.length > 0 ? (
              topCategories.map((cat: CategoryDTO) => (
                <li className="list-group-item d-flex justify-content-between" key={cat.id}>
                  <Link to={p(`/categories/${cat.id}`)} className="text-dark">
                    {cat.name}
                  </Link>
                  <span className="badge badge-light">{cat.postsCount ?? 0}</span>
                </li>
              ))
            ) : (
              <li className="list-group-item text-muted">Sin datos</li>
            )}
          </ul>
        </div>
      </div>
    </>
  );
}
