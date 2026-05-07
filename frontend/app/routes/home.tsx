import { useState } from "react";
import type { Route } from "./+types/home";
import type { Post } from "../dto/PostDTO";

import "bootstrap/dist/css/bootstrap.min.css";

import {
  Container,
  Row,
  Col,
  Button,
} from "react-bootstrap";

import Navbar from "../components/navbar";
import Sidebar from "../components/sidebar";
import PostList from "../components/postList";
import TopCategories from "../components/topCategories";
import PostForm from "../components/postForm";

export async function loader() {
  const API = "https://localhost:8443/api/v1";

  const [
    postsRes,
    categoriesRes,
    currentUserRes,
  ] = await Promise.all([
    fetch(`${API}/posts?page=0&size=10`, {
      credentials: "include",
    }),

    fetch(`${API}/categories`, {
      credentials: "include",
    }),

    fetch(`${API}/users/me`, {
      credentials: "include",
    }),
  ]);

  const postsData = await postsRes.json();
  const categories = await categoriesRes.json();

  let currentUser = null;

  if (currentUserRes.ok) {
    currentUser = await currentUserRes.json();
  }

  return {
    currentUser,
    posts: postsData.content,
    categories,
    topCategories: categories.slice(0, 5),
  };
}

export default function Home({
  loaderData,
}: Route.ComponentProps) {

  const {
    currentUser,
    posts: initialPosts,
    categories,
    topCategories,
  } = loaderData;

  const [posts, setPosts] =
    useState<Post[]>(initialPosts);

  const [page, setPage] = useState(1);

  const [loading, setLoading] =
    useState(false);

  const loadMore = async () => {

    try {
      setLoading(true);

      const res = await fetch(
        `https://localhost:8443/api/v1/posts?page=${page}&size=10`,
        {
          credentials: "include",
        }
      );

      const data = await res.json();

      setPosts((prev) => [
        ...prev,
        ...data.content,
      ]);

      setPage((prev) => prev + 1);

    } finally {
      setLoading(false);
    }
  };

  return (
    <Container fluid>

      <Navbar currentUser={currentUser} />

      <Row className="mt-3">

        <Col md={2}>
          <Sidebar
            currentUser={currentUser}
            isAdmin={
              currentUser?.roles?.includes(
                "ROLE_ADMIN"
              )
            }
          />
        </Col>

        <Col md={7}>

          <PostForm
            currentUser={currentUser}
            categories={categories}
          />

          <PostList posts={posts} />

          <div className="text-center mt-3">
            <Button
              onClick={loadMore}
              disabled={loading}
            >
              {loading
                ? "Cargando..."
                : "Cargar más"}
            </Button>
          </div>

        </Col>

        <Col md={3}>
          <TopCategories
            categories={topCategories}
          />
        </Col>

      </Row>
    </Container>
  );
}