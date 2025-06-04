import { colorPalette } from "../types/colorPalette";
import { Box, Button, Container, Typography } from "@mui/material";
import LinkBehavior from "../utils/LinkBehavior";

export default function Homepage() {
  return (
    <Container
      disableGutters
      sx={{
        // py: 4,
        flexGrow: 1, // Permite que o container cresça e empurre o footer para baixo
        display: "flex",
        width: "100%",
        flexDirection: "column",
        justifyContent: "center", // Centraliza o conteúdo verticalmente
        alignItems: "center", // Centraliza o conteúdo horizontalmente
        maxWidth: "100vw !important",
      }}
    >
      <Typography
        variant="h4"
        component="h1"
        gutterBottom
        align="center"
        sx={{ my: 4 }}
      >
        Bem-vindo à Cegonha Express Delivery!
      </Typography>

      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" }, // Coluna em telas pequenas, linha em telas médias e maiores
          flexGrow: 1, // Permite que esta Box ocupe o espaço restante
          width: "100%", // Ocupa a largura total do container
          alignItems: "stretch", // Faz com que os filhos se estiquem para preencher a altura
          justifyContent: "center",
          gap: { xs: 2, md: 0 }, // Espaçamento entre os botões em telas pequenas
          minHeight: { xs: "auto", md: "70vh" }, // <--- Adicionado: Altura mínima para a área dividida
        }}
      >
        {/* Botão de Produtos */}
        <Box
          sx={{
            flex: 1, // Ocupa o espaço disponível
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: colorPalette[0].rgba, // Azul do Fundo
            minHeight: { xs: "200px", md: "auto" }, // Altura mínima para mobile
            py: { xs: 4, md: 0 }, // Padding vertical para mobile
          }}
        >
          <Button
            component={LinkBehavior}
            href="/products"
            variant="contained"
            size="large"
            sx={{
              backgroundColor: colorPalette[4].rgba, // Azul do Contorno para o botão
              color: colorPalette[1].rgba, // Branco para o texto
              "&:hover": {
                backgroundColor: colorPalette[2].rgba, // Azul da Sombra da Cegonha no hover
              },
              fontSize: { xs: "1.2rem", md: "1.5rem" },
              padding: { xs: "15px 30px", md: "20px 40px" },
              borderRadius: "8px",
              fontWeight: "bold",
            }}
          >
            Catálogo
          </Button>
        </Box>

        {/* Botão de Catálogo/Produtos */}
        <Box
          sx={{
            flex: 1, // Ocupa o espaço disponível
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            width: "100%",
            backgroundColor: colorPalette[3].rgba, // Rosa Claro
            minHeight: { xs: "200px", md: "auto" }, // Altura mínima para mobile
            py: { xs: 4, md: 0 }, // Padding vertical para mobile
          }}
        >
          <Button
            component={LinkBehavior}
            href="/rastreio" // Assumindo que "Catálogo/Produtos" leva para a mesma página de produtos
            variant="contained"
            size="large"
            sx={{
              backgroundColor: colorPalette[1].rgba, // Azul do Contorno para o botão
              color: colorPalette[4].rgba, // Branco para o texto
              "&:hover": {
                backgroundColor: colorPalette[2].rgba, // Azul da Sombra da Cegonha no hover
              },
              fontSize: { xs: "1.2rem", md: "1.5rem" },
              padding: { xs: "15px 30px", md: "20px 40px" },
              borderRadius: "8px",
              fontWeight: "bold",
            }}
          >
            Rastreio
          </Button>
        </Box>
      </Box>
    </Container>
  );
}
