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
  Alert,
  Box,
} from "@mui/material";
import { toast } from "react-toastify";
import type { Produto } from "../types/produto";
import { mockProdutos } from "../types/mock";
import { colorPalette } from "../types/colorPalette";

const ListaProdutos: React.FC = () => {
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [paginaAtual, setPaginaAtual] = useState<number>(1);
  const produtosPorPagina = 12;

  useEffect(() => {
    const fetchProdutos = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get<Produto[]>("/produtos");
        setProdutos(response.data);
      } catch (err) {
        console.error("Erro ao buscar produtos:", err);
        setError(
          "Não foi possível carregar os produtos. Tente novamente mais tarde.",
        );
        toast.error("Erro ao carregar produtos!");
      } finally {
        setLoading(false);
      }
    };

    fetchProdutos();
  }, []);

  // const indiceUltimoProduto = paginaAtual * produtosPorPagina;
  // const indicePrimeiroProduto = indiceUltimoProduto - produtosPorPagina;
  //   const produtosAtuais = produtos.slice(
  //     indicePrimeiroProduto,
  //     indiceUltimoProduto,
  //   );
  const totalPaginas = Math.ceil(produtos.length / produtosPorPagina);

  const handleChangePage = (
    _event: React.ChangeEvent<unknown>,
    value: number,
  ) => {
    setPaginaAtual(value);
    window.scrollTo({ top: 0, behavior: "smooth" });
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

  if (error) {
    return (
      <Container sx={{ mt: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (produtos.length === 0) {
    return (
      <Container sx={{ mt: 4 }}>
        <Alert severity="info">Nenhum produto encontrado.</Alert>
      </Container>
    );
  }

  const spacing = 32;
  const halfSpacing = spacing / 2;

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography
        variant="h4"
        component="h1"
        gutterBottom
        align="center"
        sx={{ mb: 4 }}
      >
        Catálogo
      </Typography>

      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          margin: `-${halfSpacing}px`,
        }}
      >
        {mockProdutos.map((produto, index) => (
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
                backgroundColor: colorPalette[index % colorPalette.length].rgba,
              }}
            >
              <CardMedia
                component="img"
                height="200"
                image={produto.imagemUrl}
                alt={produto.nome}
                sx={{ objectFit: "cover" }}
              />
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h6" component="div">
                  {produto.nome}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {produto.descricao}
                </Typography>
              </CardContent>
            </Card>
          </Box>
        ))}
      </Box>

      {/* {produtosAtuais.map((produto) => (
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
              }}
            >
              <CardMedia
                component="img"
                height="200"
                image={produto.imagemUrl}
                alt={produto.nome}
                sx={{ objectFit: "cover" }}
              />
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h6" component="div">
                  {produto.nome}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {produto.descricao}
                </Typography>
              </CardContent>
            </Card>
          </Box>
        ))}
      </Box>
 */}
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
    </Container>
  );
};

export default ListaProdutos;
