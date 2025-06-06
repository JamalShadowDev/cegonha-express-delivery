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
        justifyContent: "space-between",
        backgroundColor: colorPalette[0].rgba,
        borderRadius: "8px",
        overflow: "hidden",
        boxShadow: 3,
        mt: 4,
        mb: 4,
        minHeight: { xs: "250px", md: "200px" },
        width: "100%",
      }}
    >
      <Box
        component="img"
        src="src\main\frontend\public\cegonha.png"
        alt="Banner Imagem"
        sx={{
          width: { xs: "100%", md: "50%" },
          height: { xs: "150px", md: "100%" },
          objectFit: "cover",
          borderTopLeftRadius: { xs: "8px", md: "8px" },
          borderTopRightRadius: { xs: "8px", md: "0" },
          borderBottomLeftRadius: { xs: "0", md: "8px" },
          borderBottomRightRadius: { xs: "0", md: "0" },
        }}
      />

      <Box
        sx={{
          width: { xs: "100%", md: "50%" },
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          py: { xs: 2, md: 0 },
          px: { xs: 2, md: 4 },
          textAlign: { xs: "center", md: "left" },
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
