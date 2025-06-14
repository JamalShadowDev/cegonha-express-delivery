import React, { useState } from "react";
import axios from "axios";
import {
  Container,
  Box,
  TextField,
  Button,
  Typography,
  CircularProgress,
  Paper,
  Alert,
} from "@mui/material";
import { toast } from "react-toastify";
import { colorPalette } from "../types/colorPalette";

export const AdminPage: React.FC = () => {
  const [codigoEncomenda, setCodigoEncomenda] = useState<string>("");
  const [motivoCancelamento, setMotivoCancelamento] = useState<string>("");
  const [loadingStatus, setLoadingStatus] = useState<boolean>(false);
  const [loadingCancel, setLoadingCancel] = useState<boolean>(false);
  const [message, setMessage] = useState<{
    type: "success" | "error";
    text: string;
  } | null>(null);

  const handleAdvanceStatus = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoadingStatus(true);
    setMessage(null);

    if (!codigoEncomenda.trim()) {
      setMessage({
        type: "error",
        text: "Por favor, insira o código da encomenda.",
      });
      setLoadingStatus(false);
      return;
    }

    try {
      const response = await axios.put<string>( // Esperamos uma string como resposta
        `/api/encomendas/${codigoEncomenda}/status`,
      );
      setMessage({
        type: "success",
        text: `Status da encomenda ${codigoEncomenda} avançado para: ${response.data}`,
      });
      toast.success(
        `Encomenda ${codigoEncomenda}: Status avançado para ${response.data}`,
      );
      setCodigoEncomenda(""); // Limpa o campo após sucesso
    } catch (err) {
      console.error("Erro ao avançar status da encomenda:", err);
      let errorMessage =
        "Não foi possível avançar o status da encomenda. Verifique o código e tente novamente.";
      if (axios.isAxiosError(err) && err.response && err.response.data) {
        errorMessage = err.response.data.message || errorMessage;
      }
      setMessage({ type: "error", text: errorMessage });
      toast.error(errorMessage);
    } finally {
      setLoadingStatus(false);
    }
  };

  const handleCancelOrder = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoadingCancel(true);
    setMessage(null);

    if (!codigoEncomenda.trim()) {
      setMessage({
        type: "error",
        text: "Por favor, insira o código da encomenda para cancelar.",
      });
      setLoadingCancel(false);
      return;
    }

    if (!motivoCancelamento.trim()) {
      setMessage({
        type: "error",
        text: "Por favor, insira o motivo do cancelamento.",
      });
      setLoadingCancel(false);
      return;
    }

    try {
      const response = await axios.put<string>( // Esperamos uma string como resposta
        `/api/encomendas/${codigoEncomenda}/cancelar`,
        { motivo: motivoCancelamento },
      );
      setMessage({
        type: "success",
        text: `Encomenda ${codigoEncomenda}: ${response.data}`,
      });
      toast.success(`Encomenda ${codigoEncomenda}: ${response.data}`);
      setCodigoEncomenda(""); // Limpa os campos após sucesso
      setMotivoCancelamento("");
    } catch (err) {
      console.error("Erro ao cancelar encomenda:", err);
      let errorMessage =
        "Não foi possível cancelar a encomenda. Verifique o código e o motivo e tente novamente.";
      if (axios.isAxiosError(err) && err.response && err.response.data) {
        errorMessage = err.response.data.message || errorMessage;
      }
      setMessage({ type: "error", text: errorMessage });
      toast.error(errorMessage);
    } finally {
      setLoadingCancel(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ py: 4, flexGrow: 1 }}>
      <Typography
        variant="h4"
        component="h1"
        gutterBottom
        align="center"
        sx={{ mb: 6 }}
      >
        Administração de Encomendas
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
        <Typography
          variant="h5"
          gutterBottom
          sx={{ color: colorPalette[1].rgba, mb: 3 }}
        >
          Avançar Status ou Cancelar Encomenda
        </Typography>

        {message && (
          <Alert severity={message.type} sx={{ mb: 3 }}>
            {message.text}
          </Alert>
        )}

        <Box sx={{ display: "flex", flexDirection: "column", gap: 3 }}>
          <TextField
            label="Código da Encomenda"
            variant="outlined"
            fullWidth
            value={codigoEncomenda}
            onChange={(e) => setCodigoEncomenda(e.target.value)}
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

          
          <Box
            component="form"
            onSubmit={handleAdvanceStatus}
            sx={{ mt: 2, display: "flex", flexDirection: "column", gap: 2 }}
          >
            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={loadingStatus || !codigoEncomenda.trim()}
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
              {loadingStatus ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                "Avançar Status"
              )}
            </Button>
          </Box>

          {/* Seção Cancelar Encomenda */}
          <Box
            component="form"
            onSubmit={handleCancelOrder}
            sx={{ mt: 4, display: "flex", flexDirection: "column", gap: 2 }}
          >
            <Typography
              variant="h6"
              sx={{ color: colorPalette[1].rgba, mb: 1 }}
            >
              Cancelar Encomenda
            </Typography>
            <TextField
              label="Motivo do Cancelamento"
              variant="outlined"
              fullWidth
              multiline
              rows={3}
              value={motivoCancelamento}
              onChange={(e) => setMotivoCancelamento(e.target.value)}
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
              disabled={
                loadingCancel ||
                !codigoEncomenda.trim() ||
                !motivoCancelamento.trim()
              }
              sx={{
                py: 1.5,
                borderRadius: "8px",
                backgroundColor: colorPalette[3].rgba, // Cor diferente para cancelar
                color: colorPalette[4].rgba,
                "&:hover": {
                  backgroundColor: colorPalette[2].rgba,
                },
              }}
            >
              {loadingCancel ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                "Cancelar Encomenda"
              )}
            </Button>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
};
