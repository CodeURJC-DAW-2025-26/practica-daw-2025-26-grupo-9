import {
  Navbar as RBNavbar,
  Nav,
  Container,
  Form,
  FormControl,
  Button,
} from "react-bootstrap";

type User = {
  id: number;
  nickname?: string;
};

type NavbarProps = {
  currentUser?: User | null;
};

export default function Navbar({
  currentUser,
}: NavbarProps) {
  const logout = async () => {
    await fetch("http://localhost:8080/api/v1/auth/logout", {
      method: "POST",
      credentials: "include",
    });

    window.location.reload();
  };

  return (
    <RBNavbar bg="light" expand="lg" className="shadow-sm sticky-top">
      <Container fluid>
        <RBNavbar.Brand href="/">
          <img
            src="/assets/images/logo-64x64.png"
            width="40"
            alt="logo"
          />
        </RBNavbar.Brand>

        <RBNavbar.Toggle aria-controls="navbar" />

        <RBNavbar.Collapse id="navbar">
          {/* SEARCH */}
          <Form className="d-flex mx-auto w-50">
            <FormControl
              type="search"
              placeholder="Buscar..."
              className="me-2"
            />

            <Button variant="outline-primary">
              🔍
            </Button>
          </Form>

          <Nav className="ms-auto align-items-center">
            {currentUser ? (
              <>
                <Nav.Link href={`/users/${currentUser.id}`}>
                  Perfil
                </Nav.Link>

                <Button
                  variant="outline-danger"
                  size="sm"
                  onClick={logout}
                >
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Nav.Link href="/login">
                  Login
                </Nav.Link>

                <Nav.Link href="/register">
                  Registro
                </Nav.Link>
              </>
            )}
          </Nav>
        </RBNavbar.Collapse>
      </Container>
    </RBNavbar>
  );
}