import { redirect } from "react-router";
import { ApiError } from "~/services/api";

export async function requireAuth<T>(loader: () => Promise<T>): Promise<T> {
  try {
    return await loader();
  } catch (err) {
    if (err instanceof ApiError && err.status === 401) {
      throw redirect("/new/login");
    }
    throw err;
  }
}
