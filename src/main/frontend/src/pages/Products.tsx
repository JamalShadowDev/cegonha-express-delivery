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
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography
                  gutterBottom
                  variant="h6"
                  component="div"
                  sx={{
                    color: index === 1 ? "white" : "inherit", // Texto branco para o card rosa escuro do Noah
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
                        : "text.secondary", // Texto branco semi-transparente para o Noah
                  }}
                >
                  {produto.descricao}
                </Typography>
                <Button
                  variant="contained"
                  sx={{
                    mt: 2,
                    backgroundColor: colorPalette[1].rgba,
                    "&:hover": {
                      backgroundColor: colorPalette[2].rgba,
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
