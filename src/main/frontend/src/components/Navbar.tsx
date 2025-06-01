import { AppBar, Box, Button, Toolbar } from "@mui/material";
import LinkBehavior from "../utils/LinkBehavior";

export default function Navbar() {
  return (
    <AppBar
      position="static"
      sx={{
        backgroundColor: "rgba(3, 136, 166, 1)",
      }}
    >
      <Toolbar
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mx: 2,
        }}
      >
        <img src="..\..\public\logo.png" width="90px" height="90px" />
        <Box sx={{ display: "flex", alignContent: "end", gap: 1 }}>
          <Button
            variant="text"
            component={LinkBehavior}
            href="/"
            sx={{
              borderRadius: "30px",
              color: "rgba(242, 242, 240, 1)",
            }}
          >
            Home
          </Button>
          <Button
            variant="contained"
            component={LinkBehavior}
            href="/catalogo"
            sx={{
              borderRadius: "30px",
              backgroundColor: "rgba(242, 175, 160, 1)",
            }}
          >
            Catálogo
          </Button>
          <Button
            variant="contained"
            component={LinkBehavior}
            href="/rastreio"
            sx={{
              borderRadius: "30px",
              backgroundColor: "rgba(242, 175, 160, 1)",
            }}
          >
            Rastreio
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
