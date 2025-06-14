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
      {/* Slogan com fonte cursiva e destaque */}
      <Typography
        sx={{
          mb: 6,
          fontSize: { xs: "1.3rem", md: "1.6rem" },
          fontFamily: "'Dancing Script', 'Brush Script MT', cursive",
          fontWeight: 600,
          lineHeight: 1.4,
          letterSpacing: "0.5px",
          textShadow: "0 2px 4px rgba(0,0,0,0.3)",
          maxWidth: "800px",
          margin: "0 auto",
          px: 2,
        }}
      >
        "Não espere 9 meses para ter o seu bebê, adquira agora e calcule em
        quanto tempo ele chegará em sua casa!"
      </Typography>

      <Box sx={{ display: "flex", justifyContent: "space-between", mx: 10 }}>
        <Typography
          sx={{
            textAlign: "left",
            maxWidth: "50%",
            fontSize: { xs: "0.95rem", md: "1rem" },
            lineHeight: 1.6,
            fontWeight: 400,
          }}
        >
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
            sx={{
              "&:hover": {
                transform: "scale(1.1)",
                transition: "transform 0.2s ease",
              },
            }}
          >
            <GitHubIcon />
          </IconButton>
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://www.instagram.com"
            target="_blank"
            rel="noopener noreferrer"
            sx={{
              "&:hover": {
                transform: "scale(1.1)",
                transition: "transform 0.2s ease",
              },
            }}
          >
            <InstagramIcon />
          </IconButton>
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://www.facebook.com"
            target="_blank"
            rel="noopener noreferrer"
            sx={{
              "&:hover": {
                transform: "scale(1.1)",
                transition: "transform 0.2s ease",
              },
            }}
          >
            <FacebookIcon />
          </IconButton>
          <IconButton
            color="inherit"
            component={LinkBehavior}
            href="https://www.x.com"
            target="_blank"
            rel="noopener noreferrer"
            sx={{
              "&:hover": {
                transform: "scale(1.1)",
                transition: "transform 0.2s ease",
              },
            }}
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
            onClick={() => {
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
            sx={{
              color: "rgba(242, 242, 240, 1)",
              fontSize: "1rem",
              fontWeight: 500,
              "&:hover": {
                backgroundColor: "rgba(255,255,255,0.1)",
                transform: "translateX(-5px)",
                transition: "all 0.2s ease",
              },
            }}
          >
            Home
          </Button>
          <Button
            variant="text"
            component={LinkBehavior}
            href="/products"
            onClick={() => {
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
            sx={{
              color: "rgba(242, 242, 240, 1)",
              fontSize: "1rem",
              fontWeight: 500,
              "&:hover": {
                backgroundColor: "rgba(255,255,255,0.1)",
                transform: "translateX(-5px)",
                transition: "all 0.2s ease",
              },
            }}
          >
            Catálogo
          </Button>
          <Button
            variant="text"
            component={LinkBehavior}
            href="/tracking"
            onClick={() => {
              window.scrollTo({ top: 0, behavior: "smooth" });
            }}
            sx={{
              color: "rgba(242, 242, 240, 1)",
              fontSize: "1rem",
              fontWeight: 500,
              "&:hover": {
                backgroundColor: "rgba(255,255,255,0.1)",
                transform: "translateX(-5px)",
                transition: "all 0.2s ease",
              },
            }}
          >
            Rastreio
          </Button>
        </Box>
      </Box>
      <hr />
      <Container maxWidth="lg">
        <Typography
          variant="body2"
          sx={{
            color: "white", // Alterado de "#012E40" para branco
            fontSize: "0.9rem",
            fontWeight: 400,
            opacity: 0.9,
          }}
        >
          &copy; {new Date().getFullYear()} CegonhaExpress. Todos os direitos
          reservados.
        </Typography>
      </Container>
    </Box>
  );
}
