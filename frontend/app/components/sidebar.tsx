import {
  Card,
  ListGroup,
} from "react-bootstrap";

export default function Sidebar({
  currentUser,
  isAdmin,
}: any) {

  return (
    <Card className="shadow-sm sticky-top">
      <ListGroup variant="flush">

        <ListGroup.Item action href="/">
          Inicio
        </ListGroup.Item>

        {currentUser && (
          <ListGroup.Item
            action
            href={`/users/${currentUser.id}`}
          >
            Perfil
          </ListGroup.Item>
        )}

        {isAdmin && (
          <ListGroup.Item
            action
            href="/admin"
          >
            Administrador
          </ListGroup.Item>
        )}

        <ListGroup.Item
          action
          href="/categories"
        >
          Categorías
        </ListGroup.Item>

        <ListGroup.Item
          action
          href="/stats"
        >
          Estadísticas
        </ListGroup.Item>

      </ListGroup>
    </Card>
  );
}