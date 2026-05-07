import { useState } from "react";
import {
  Form,
  Button,
  Card,
} from "react-bootstrap";

export default function PostForm({
  currentUser,
  categories,
}: any) {

  const [content, setContent] = useState("");
  const [categoryId, setCategoryId] = useState("");

  const submitPost = async (
    e: React.FormEvent
  ) => {
    e.preventDefault();

    await fetch(
      "http://localhost:8080/api/v1/posts",
      {
        method: "POST",
        credentials: "include",

        headers: {
          "Content-Type": "application/json",
        },

        body: JSON.stringify({
          content,
          categoryId,
        }),
      }
    );

    window.location.reload();
  };

  return (
    <Card className="shadow-sm mb-3">
      <Card.Body>

        <Form onSubmit={submitPost}>

          <Form.Group>
            <Form.Control
              as="textarea"
              rows={2}
              value={content}
              onChange={(e) =>
                setContent(e.target.value)
              }
              placeholder={
                currentUser
                  ? `¿Qué estás pensando ${currentUser.nickname}?`
                  : "Inicia sesión para publicar"
              }
              disabled={!currentUser}
            />
          </Form.Group>

          <Form.Group className="mt-2">
            <Form.Select
              value={categoryId}
              onChange={(e) =>
                setCategoryId(e.target.value)
              }
            >
              <option value="">
                Selecciona categoría
              </option>

              {categories.map((c: any) => (
                <option
                  key={c.id}
                  value={c.id}
                >
                  {c.name}
                </option>
              ))}
            </Form.Select>
          </Form.Group>

          <div className="text-end mt-3">
            <Button
              type="submit"
              disabled={!currentUser}
            >
              Publicar
            </Button>
          </div>

        </Form>
      </Card.Body>
    </Card>
  );
}