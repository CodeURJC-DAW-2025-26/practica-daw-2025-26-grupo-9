import {
  Card,
  ListGroup,
} from "react-bootstrap";

export default function TopCategories({
  categories,
}: any) {

  return (
    <Card className="shadow-sm">
      <Card.Body>

        <Card.Title>
          Categorías más populares
        </Card.Title>

        <ListGroup variant="flush">

          {categories?.length ? (
            categories.map((c: any) => (
              <ListGroup.Item
                key={c.id}
                action
                href={`/categories/${c.id}`}
              >
                {c.name}
              </ListGroup.Item>
            ))
          ) : (
            <ListGroup.Item className="text-muted">
              Sin categorías
            </ListGroup.Item>
          )}

        </ListGroup>
      </Card.Body>
    </Card>
  );
}