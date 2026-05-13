import { type RouteConfig, route, index } from "@react-router/dev/routes";

export default [
  route("new", "routes/new-layout.tsx", [
    index("routes/home.tsx"),
    route("login", "routes/login.tsx"),
    route("register", "routes/register.tsx"),
    route("posts/:id", "routes/post.$id.tsx"),
    route("posts/:id/edit", "routes/post.$id.edit.tsx"),
    route("users/:id", "routes/user.$id.tsx"),
    route("users/:id/edit", "routes/user.$id.edit.tsx"),
    route("categories", "routes/categories.tsx"),
    route("categories/:id", "routes/category.$id.tsx"),
    route("admin", "routes/admin.tsx"),
    route("admin/categories/new", "routes/admin.create-category.tsx"),
    route("admin/categories/:id/edit", "routes/admin.edit-category.$id.tsx"),
    route("stats", "routes/stats.tsx"),
  ]),
] satisfies RouteConfig;
