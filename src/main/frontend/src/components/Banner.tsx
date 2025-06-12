import React from "react";
import { Box, Typography } from "@mui/material";
import { colorPalette } from "../types/colorPalette";

interface BannerProps {
  title: string;
}

const Banner: React.FC<BannerProps> = ({ title }) => {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: { xs: "column", md: "row" },
        alignItems: "center",
        justifyContent: "center",
        // backgroundColor: colorPalette[0].rgba,
        overflow: "hidden",
        boxShadow: 0,

        width: "100%",
      }}
    >
      <Box
        component="img"
        src="../../cegonha.png"
        alt="Banner Imagem"
        sx={{
          width: "400px",
          height: "400px",
          objectFit: "cover",
        }}
      />

      <Box
        sx={{
          color: colorPalette[1].rgba,
        }}
      >
        <Typography variant="h4" component="h2" sx={{ fontWeight: "bold" }}>
          {title}
        </Typography>
      </Box>
    </Box>
  );
};

export default Banner;
