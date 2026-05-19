import { data } from "react-router";

export async function clientLoader() {
  throw data(null, { status: 404, statusText: "Not Found" });
}

export default function NotFound() {
  return null;
}
