import { Link } from "react-router";
import type { CategoryDTO } from "~/dto/PostDTO";
import { p } from "~/utils/paths";

type TopCategoriesProps = {
  categories: CategoryDTO[];
};

export default function TopCategories({ categories }: TopCategoriesProps) {
  return (
    <div className="col-md-3 third-section">
      <div className="card shadow-sm">
        <div className="card-body">
          <div className="weather-card-header d-flex justify-content-between align-items-center">
            <p className="fs-1 mb-0">Categorias mas populares</p>
          </div>
          <div className="weather-quick align-items-center mt-4">
            <div className="row">
              <div className="col-md-8">
                <ul className="list-group list-group-flush newsfeed-left-sidebar tamaño">
                  {categories?.length ? (
                    categories.map((c) => (
                      <li className="list-group-item" key={c.id}>
                        <Link to={p(`/categories/${c.id}`)}>{c.name}</Link>

                      </li>
                    ))
                  ) : (
                    <li className="list-group-item text-muted">Sin categor&iacute;as todav&iacute;a</li>
                  )}
                </ul>
              </div>
              <div className="col-md-4"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
