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
  List,
  ListItem,
  ListItemText,
  Divider,
} from "@mui/material";
import { toast } from "react-toastify";
import { colorPalette } from "../types/colorPalette";

// Interface para os dados da requisição de rastreamento

// Interface para um evento de rastreamento
interface TrackingEvent {
  timestamp: string; // Ex: "2023-10-27T10:00:00Z"
  location: string; // Ex: "São Paulo, SP"
  status: string; // Ex: "Em trânsito", "Saiu para entrega", "Entregue"
  description?: string; // Detalhes adicionais do evento
}

// Interface para a resposta da API de rastreamento
interface OrderTrackingResponse {
  orderId: string;
  status: string; // Status geral do pedido
  events: TrackingEvent[]; // Lista de eventos de movimentação
  estimatedDelivery?: string; // Data estimada de entrega, se disponível
}

export const Tracking: React.FC = () => {
  const [orderId, setOrderId] = useState<string>("");
  const [trackingData, setTrackingData] =
    useState<OrderTrackingResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [submitted, setSubmitted] = useState<boolean>(false); // Para controlar a exibição inicial

  const handleTrackOrder = async (event: React.FormEvent) => {
    event.preventDefault(); // Previne o recarregamento da página
    setLoading(true);
    setError(null);
    setTrackingData(null); // Limpa dados anteriores
    setSubmitted(true); // Marca que o formulário foi submetido

    if (!orderId.trim()) {
      setError("Por favor, insira um ID de pedido.");
      setLoading(false);
      return;
    }

    try {
      // Faz a requisição POST para o backend
      // Altere '/track-order' para o endpoint real da sua API de rastreamento
      const response = await axios.post<OrderTrackingResponse>("/track-order", {
        orderId,
      });
      setTrackingData(response.data);
      toast.success("Informações de rastreamento carregadas!");
    } catch (err) {
      console.error("Erro ao rastrear pedido:", err);
      // Verifica se o erro é uma resposta do Axios e tem uma mensagem de erro
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
          "Não foi possível rastrear o pedido. Verifique o ID e tente novamente.",
        );
        toast.error("Erro ao rastrear pedido!");
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
        Rastrear Pedido
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
        {" "}
        <Box
          component="form"
          onSubmit={handleTrackOrder}
          sx={{ display: "flex", flexDirection: "column", gap: 2 }}
        >
          <TextField
            label="ID do Pedido"
            variant="outlined"
            fullWidth
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
            required
            error={submitted && !orderId.trim()} // Exibe erro se submetido e vazio
            helperText={
              submitted && !orderId.trim() ? "ID do pedido é obrigatório" : ""
            }
            sx={{
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[4].rgba, // Branco para o fundo do input
                borderRadius: "8px",
              },
              "& .MuiInputLabel-root": {
                color: colorPalette[1].rgba, // Azul do Contorno para o label
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
              backgroundColor: colorPalette[1].rgba, // Azul do Contorno para o botão
              color: colorPalette[4].rgba, // Branco para o texto
              "&:hover": {
                backgroundColor: colorPalette[2].rgba, // Azul da Sombra da Cegonha no hover
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

      {trackingData && (
        <Paper
          elevation={3}
          sx={{
            p: { xs: 2, md: 4 },
            borderRadius: "8px",
            backgroundColor: colorPalette[3].rgba,
          }}
        >
          {" "}
          {/* Rosa Claro */}
          <Typography
            variant="h5"
            gutterBottom
            sx={{ color: colorPalette[1].rgba }}
          >
            {" "}
            {/* Azul do Contorno */}
            Detalhes do Pedido: {trackingData.orderId}
          </Typography>
          <Typography
            variant="body1"
            sx={{ mb: 1, color: colorPalette[1].rgba }}
          >
            {" "}
            {/* Azul do Contorno */}
            **Status Atual:** {trackingData.status}
          </Typography>
          {trackingData.estimatedDelivery && (
            <Typography
              variant="body1"
              sx={{ mb: 2, color: colorPalette[1].rgba }}
            >
              {" "}
              {/* Azul do Contorno */}
              **Previsão de Entrega:**{" "}
              {new Date(trackingData.estimatedDelivery).toLocaleDateString(
                "pt-BR",
                {
                  year: "numeric",
                  month: "long",
                  day: "numeric",
                },
              )}
            </Typography>
          )}
          <Typography
            variant="h6"
            sx={{ mt: 3, mb: 2, color: colorPalette[1].rgba }}
          >
            {" "}
            {/* Azul do Contorno */}
            Histórico de Movimentações:
          </Typography>
          {trackingData.events.length > 0 ? (
            <List>
              {trackingData.events.map((event, index) => (
                <React.Fragment key={index}>
                  <ListItem disablePadding sx={{ color: colorPalette[1].rgba }}>
                    {" "}
                    {/* Azul do Contorno */}
                    <ListItemText
                      primary={
                        <Typography
                          variant="subtitle1"
                          component="span"
                          sx={{
                            fontWeight: "bold",
                            color: colorPalette[1].rgba,
                          }}
                        >
                          {new Date(event.timestamp).toLocaleDateString(
                            "pt-BR",
                            {
                              day: "2-digit",
                              month: "2-digit",
                              year: "numeric",
                              hour: "2-digit",
                              minute: "2-digit",
                            },
                          )}{" "}
                          - {event.status}
                        </Typography>
                      }
                      secondary={
                        <Typography
                          variant="body2"
                          sx={{ color: colorPalette[2].rgba }}
                        >
                          {" "}
                          {/* Azul da Sombra da Cegonha */}
                          {`${event.location}${event.description ? ` - ${event.description}` : ""}`}
                        </Typography>
                      }
                    />
                  </ListItem>
                  {index < trackingData.events.length - 1 && (
                    <Divider
                      component="li"
                      sx={{ borderColor: colorPalette[2].rgba }}
                    />
                  )}{" "}
                  {/* Azul da Sombra da Cegonha */}
                </React.Fragment>
              ))}
            </List>
          ) : (
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ color: colorPalette[1].rgba }}
            >
              {" "}
              {/* Azul do Contorno */}
              Nenhuma movimentação registrada ainda.
            </Typography>
          )}
        </Paper>
      )}
    </Container>
  );
};
