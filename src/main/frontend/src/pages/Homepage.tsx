import { colorPalette } from "../types/colorPalette";
import { Box, Button, Container } from "@mui/material";
import LinkBehavior from "../utils/LinkBehavior";
import Banner from "../components/Banner";

export default function Homepage() {
  return (
    <Container
      disableGutters
      sx={{
        flexGrow: 1,
        display: "flex",
        width: "100%",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        maxWidth: "100vw !important",
      }}
    >
      {/* <Typography
        variant="h4"
        component="h1"
        gutterBottom
        align="center"
        sx={{ my: 4 }}
      >
        Bem-vindo à Cegonha Express Delivery!
      </Typography> */}
      <Banner title="Bem-vindo à Cegonha Express Delivery!" />
      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          flexGrow: 1,
          width: "100%",
          alignItems: "stretch",
          justifyContent: "center",
          gap: { xs: 2, md: 0 },
          minHeight: { xs: "auto", md: "70vh" },
        }}
      >
        <Box
          sx={{
            flex: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            backgroundColor: colorPalette[0].rgba,
            minHeight: { xs: "200px", md: "auto" },
            py: { xs: 4, md: 0 },
          }}
        >
          <Button
            component={LinkBehavior}
            href="/products"
            variant="contained"
            size="large"
            sx={{
              backgroundColor: colorPalette[4].rgba,
              color: colorPalette[1].rgba,
              "&:hover": {
                backgroundColor: colorPalette[2].rgba,
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

        <Box
          sx={{
            flex: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            width: "100%",
            backgroundColor: colorPalette[3].rgba,
            minHeight: { xs: "200px", md: "auto" },
            py: { xs: 4, md: 0 },
          }}
        >
          <Button
            component={LinkBehavior}
            href="/rastreio"
            variant="contained"
            size="large"
            sx={{
              backgroundColor: colorPalette[1].rgba,
              color: colorPalette[4].rgba,
              "&:hover": {
                backgroundColor: colorPalette[2].rgba,
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
