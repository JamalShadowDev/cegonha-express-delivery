import React, { useState } from "react";
import axios from "axios";
import {
  Container,
  Box,
  TextField,
  Button,
  Typography,
  CircularProgress,
  Alert,
  Paper,
} from "@mui/material";
import { toast } from "react-toastify";
import { colorPalette } from "../types/colorPalette";

interface EncomendaResponse {
  codigo: string;
  status: string;
  valorFrete: string;
  tempoEstimadoEntrega: string;
}

export const Tracking: React.FC = () => {
  const [codigoEncomenda, setCodigoEncomenda] = useState<string>("");
  const [encomendaData, setEncomendaData] = useState<EncomendaResponse | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [submitted, setSubmitted] = useState<boolean>(false);

  const handleTrackOrder = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    setEncomendaData(null);
    setSubmitted(true);

    if (!codigoEncomenda.trim()) {
      setError("Por favor, insira um código de encomenda.");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.get<EncomendaResponse>(
        `/api/encomendas/${codigoEncomenda}`,
      );
      setEncomendaData(response.data);
      toast.success("Informações da encomenda carregadas!");
    } catch (err) {
      console.error("Erro ao rastrear encomenda:", err);

      if (
        axios.isAxiosError(err) &&
        err.response &&
        err.response.data &&
        err.response.data.message
      ) {
        setError(err.response.data.message);
        toast.error(err.response.data.message);
      } else {
        setError(
          "Não foi possível rastrear a encomenda. Verifique o código e tente novamente.",
        );
        toast.error("Erro ao rastrear encomenda!");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4, flexGrow: 1 }}>
      <Typography
        variant="h4"
        component="h1"
        gutterBottom
        align="center"
        sx={{ mb: 10 }}
      >
        Rastrear Encomenda
      </Typography>

      <Paper
        elevation={3}
        sx={{
          p: { xs: 2, md: 4 },
          mb: 4,
          borderRadius: "8px",
          backgroundColor: colorPalette[0].rgba,
        }}
      >
        <Box
          component="form"
          onSubmit={handleTrackOrder}
          sx={{ display: "flex", flexDirection: "column", gap: 2 }}
        >
          <TextField
            label="Código da Encomenda"
            variant="outlined"
            fullWidth
            value={codigoEncomenda}
            onChange={(e) => setCodigoEncomenda(e.target.value)}
            required
            error={submitted && !codigoEncomenda.trim()}
            helperText={
              submitted && !codigoEncomenda.trim()
                ? "O código da encomenda é obrigatório"
                : ""
            }
            sx={{
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[4].rgba,
                borderRadius: "8px",
              },
              "& .MuiInputLabel-root": {
                color: colorPalette[1].rgba,
              },
            }}
          />
          <Button
            type="submit"
            variant="contained"
            fullWidth
            disabled={loading}
            sx={{
              py: 1.5,
              borderRadius: "8px",
              backgroundColor: colorPalette[1].rgba,
              color: colorPalette[4].rgba,
              "&:hover": {
                backgroundColor: colorPalette[2].rgba,
              },
            }}
          >
            {loading ? (
              <CircularProgress size={24} color="inherit" />
            ) : (
              "Rastrear"
            )}
          </Button>
        </Box>
      </Paper>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {encomendaData && (
        <Paper
          elevation={3}
          sx={{
            p: { xs: 2, md: 4 },
            borderRadius: "8px",
            backgroundColor: colorPalette[2].rgba,
          }}
        >
          <Typography
            variant="h5"
            gutterBottom
            sx={{ color: colorPalette[1].rgba }}
          >
            Detalhes da Encomenda: {encomendaData.codigo}
          </Typography>
          <Box
            display="flex"
            sx={{ alignItems: "center", justifyContent: "space-between" }}
          >
            <Typography
              variant="body1"
              sx={{ mb: 1, color: colorPalette[1].rgba }}
            >
              Status Atual: {encomendaData.status}
            </Typography>

            <Typography
              variant="body1"
              sx={{ mb: 2, color: colorPalette[1].rgba }}
            >
              Tempo Estimado de Entrega: {encomendaData.tempoEstimadoEntrega}
            </Typography>
          </Box>
        </Paper>
      )}
    </Container>
  );
};
