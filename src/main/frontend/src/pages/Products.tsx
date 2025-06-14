import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  Container,
  Card,
  CardMedia,
  CardContent,
  Typography,
  Pagination,
  CircularProgress,
  Box,
  Button,
  Chip,
  Stack,
} from "@mui/material";
import { toast } from "react-toastify";
import type { Produto } from "../types/produto";

import Banner from "../components/Banner";
import { colorPalette } from "../types/colorPalette";
import { ModalOrder } from "../components/ModalOrder";

export const Products: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [paginaAtual, setPaginaAtual] = useState<number>(1);
  const produtosPorPagina = 12;

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [selectedProduto, setSelectedProduto] = useState<Produto | null>(null);

  useEffect(() => {
    const fetchProdutos = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get<Produto[]>(
          "http://localhost:8080/api/encomendas/bebes",
        );
        setProdutos(response.data);
      } catch (err) {
        console.error("Erro ao buscar produtos:", err);
        toast.error("Erro ao carregar produtos! Tente novamente mais tarde.");
        setError("true");
      } finally {
        setLoading(false);
      }
    };

    fetchProdutos();
  }, []);

  const indiceUltimoProduto = paginaAtual * produtosPorPagina;
  const indicePrimeiroProduto = indiceUltimoProduto - produtosPorPagina;
  const produtosAtuais = produtos.slice(
    indicePrimeiroProduto,
    indiceUltimoProduto,
  );
  const totalPaginas = Math.ceil(produtos.length / produtosPorPagina);

  const handleChangePage = (
    _event: React.ChangeEvent<unknown>,
    value: number,
  ) => {
    setPaginaAtual(value);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleOpenModal = (produto: Produto) => {
    setSelectedProduto(produto);
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setSelectedProduto(null);
  };

  // Cores personalizadas para os cards com rosa escuro no lugar do azul escuro
  const getCardColor = (index: number) => {
    const customColors = [
      colorPalette[0].rgba, // Azul claro - Maya
      "#8B4A6B", // Rosa escuro - Noah (substituindo o azul escuro problemático)
      colorPalette[2].rgba, // Azul médio - Alice
      colorPalette[3].rgba, // Rosa claro - Arthur
      colorPalette[4].rgba, // Branco/cinza - Sophia
      colorPalette[0].rgba, // Azul claro - Samuel
    ];
    return customColors[index % customColors.length];
  };

  if (loading) {
    return (
      <Container
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: "80vh",
        }}
      >
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Carregando produtos...
        </Typography>
      </Container>
    );
  }

  if (error && produtos.length === 0) {
    return (
      <Container sx={{ mt: 4 }}>
        <Typography variant="h6" color="error">
          Não foi possível carregar os produtos devido a um erro. Por favor,
          tente novamente.
        </Typography>
      </Container>
    );
  }

  if (produtos.length === 0) {
    return (
      <Container sx={{ mt: 4 }}>
        <Typography variant="h6" color="text.secondary">
          Nenhum produto encontrado.
        </Typography>
      </Container>
    );
  }

  const spacing = 32;
  const halfSpacing = spacing / 2;

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Banner title="Catálogo de produtos" />
      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          margin: `-${halfSpacing}px`,
        }}
      >
        {produtosAtuais.map((produto, index) => (
          <Box
            key={produto.id}
            sx={{
              padding: `${halfSpacing}px`,
              width: {
                xs: "100%",
                sm: "50%",
                md: "33.33%",
                lg: "25%",
              },
            }}
          >
            <Card
              sx={{
                height: "100%",
                display: "flex",
                flexDirection: "column",
                borderRadius: "8px",
                backgroundColor: getCardColor(index),
                cursor: "pointer",
                transition: "transform 0.2s ease-in-out",
                "&:hover": {
                  transform: "translateY(-4px)",
                  boxShadow: "0 8px 16px rgba(0,0,0,0.15)",
                },
              }}
              onClick={() => handleOpenModal(produto)}
            >
              <CardMedia
                component="img"
                height="200"
                image={produto.linkImg}
                alt={produto.nome}
                sx={{ objectFit: "cover" }}
              />
              <CardContent sx={{ flexGrow: 1, pb: 1 }}>
                <Typography
                  gutterBottom
                  variant="h6"
                  component="div"
                  sx={{
                    color: index === 1 ? "white" : "inherit",
                    fontWeight: "bold",
                    mb: 1,
                  }}
                >
                  {produto.nome}
                </Typography>

                <Typography
                  variant="body2"
                  sx={{
                    color:
                      index === 1
                        ? "rgba(255, 255, 255, 0.8)"
                        : "text.secondary",
                    mb: 2,
                    display: "-webkit-box",
                    WebkitLineClamp: 3,
                    WebkitBoxOrient: "vertical",
                    overflow: "hidden",
                  }}
                >
                  {produto.descricao}
                </Typography>

                {/* Chips com peso e altura */}
                <Stack
                  direction="row"
                  spacing={1}
                  sx={{ mb: 2, flexWrap: "wrap", gap: 0.5 }}
                >
                  <Chip
                    label={`${produto.peso_kg}kg`}
                    size="small"
                    sx={{
                      backgroundColor: colorPalette[1].rgba,
                      color: colorPalette[4].rgba,
                      fontWeight: "bold",
                      fontSize: "0.75rem",
                    }}
                  />
                  <Chip
                    label={`${produto.altura_cm}cm`}
                    size="small"
                    sx={{
                      backgroundColor: colorPalette[1].rgba,
                      color: colorPalette[4].rgba,
                      fontWeight: "bold",
                      fontSize: "0.75rem",
                    }}
                  />
                </Stack>

                <Button
                  variant="contained"
                  fullWidth
                  sx={{
                    mt: "auto",
                    backgroundColor: colorPalette[1].rgba,
                    color: colorPalette[4].rgba,
                    fontWeight: "bold",
                    borderRadius: "8px",
                    "&:hover": {
                      backgroundColor: colorPalette[2].rgba,
                      transform: "translateY(-1px)",
                    },
                  }}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleOpenModal(produto);
                  }}
                >
                  Fazer Pedido
                </Button>
              </CardContent>
            </Card>
          </Box>
        ))}
      </Box>

      {totalPaginas > 1 && (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 4 }}>
          <Pagination
            count={totalPaginas}
            page={paginaAtual}
            onChange={handleChangePage}
            color="primary"
            size="large"
            showFirstButton
            showLastButton
          />
        </Box>
      )}

      {selectedProduto && (
        <ModalOrder
          open={modalOpen}
          handleClose={handleCloseModal}
          produto={selectedProduto}
        />
      )}
    </Container>
  );
};
