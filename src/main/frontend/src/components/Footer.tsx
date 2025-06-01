import { Box, Button, Container, IconButton, Typography } from "@mui/material";
import LinkBehavior from "../utils/LinkBehavior";
import GitHubIcon from "@mui/icons-material/GitHub";
import InstagramIcon from "@mui/icons-material/Instagram";
import FacebookIcon from "@mui/icons-material/Facebook";
import XIcon from "@mui/icons-material/X";

export default function Footer() {
  return (
    <Box
      sx={{
        backgroundColor: "rgba(3, 136, 166, 1)",
        color: "white",
        py: 3,
        mt: "auto",
        width: "100%",
        textAlign: "center",
      }}
      component="footer"
    >
      <Typography sx={{ mb: 6 }}>
        Não espere 9 meses para ter o seu bebê, adquira agora e calcule em
        quanto tempo ele chegará em sua casa!
      </Typography>
      <Box sx={{ display: "flex", justifyContent: "space-between", mx: 10 }}>
        <Typography sx={{ textAlign: "left", maxWidth: "50%" }}>
          CegonhaExpress é um sistema completo de entrega especializado em bebês
          reborn, desenvolvido como projeto acadêmico para demonstrar conceitos
          avançados de Programação Orientada a Objetos, arquitetura em camadas e
          integração de APIs. <br />
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://github.com/GabrielCoelho/cegonha-express-delivery?tab=readme-ov-file"
            target="_blank"
            rel="noopener noreferrer"
          >
            <GitHubIcon />
          </IconButton>
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://www.instagram.com"
            target="_blank"
            rel="noopener noreferrer"
          >
            <InstagramIcon />
          </IconButton>
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://www.facebook.com"
            target="_blank"
            rel="noopener noreferrer"
          >
            <FacebookIcon />
          </IconButton>
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://www.x.com"
            target="_blank"
            rel="noopener noreferrer"
          >
            <XIcon />
          </IconButton>
        </Typography>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            mx: 20,
            flexDirection: "column",
            alignItems: "end",
            mb: 6,
          }}
        >
          <Button
            variant="text"
            component={LinkBehavior}
            href="/"
            sx={{
              color: "rgba(242, 242, 240, 1)",
            }}
          >
            Home
          </Button>
          <Button
            variant="text"
            component={LinkBehavior}
            href="/catalogo"
            sx={{
              color: "rgba(242, 242, 240, 1)",
            }}
          >
            Catálogo
          </Button>
          <Button
            variant="text"
            component={LinkBehavior}
            href="/rastreio"
            sx={{
              color: "rgba(242, 242, 240, 1)",
            }}
          >
            Rastreio
          </Button>
        </Box>
      </Box>
      <hr />
      <Container maxWidth="lg">
        <Typography variant="body2" color="#012E40">
          &copy; {new Date().getFullYear()} CegonhaExpress. Todos os direitos
          reservados.
        </Typography>
      </Container>
    </Box>
  );
}
