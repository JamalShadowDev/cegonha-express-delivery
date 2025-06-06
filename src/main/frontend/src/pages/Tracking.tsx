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

interface TrackingEvent {
  timestamp: string;
  location: string;
  status: string;
  description?: string;
}

interface OrderTrackingResponse {
  orderId: string;
  status: string;
  events: TrackingEvent[];
  estimatedDelivery?: string;
}

export const Tracking: React.FC = () => {
  const [orderId, setOrderId] = useState<string>("");
  const [trackingData, setTrackingData] =
    useState<OrderTrackingResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [submitted, setSubmitted] = useState<boolean>(false);

  const handleTrackOrder = async (event: React.FormEvent) => {
    event.preventDefault(); // Previne o recarregamento da página
    setLoading(true);
    setError(null);
    setTrackingData(null);
    setSubmitted(true);

    if (!orderId.trim()) {
      setError("Por favor, insira um ID de pedido.");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post<OrderTrackingResponse>("/track-order", {
        orderId,
      });
      setTrackingData(response.data);
      toast.success("Informações de rastreamento carregadas!");
    } catch (err) {
      console.error("Erro ao rastrear pedido:", err);

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
            error={submitted && !orderId.trim()}
            helperText={
              submitted && !orderId.trim() ? "ID do pedido é obrigatório" : ""
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
          <Typography
            variant="h5"
            gutterBottom
            sx={{ color: colorPalette[1].rgba }}
          >
            {" "}
            Detalhes do Pedido: {trackingData.orderId}
          </Typography>
          <Typography
            variant="body1"
            sx={{ mb: 1, color: colorPalette[1].rgba }}
          >
            {" "}
            **Status Atual:** {trackingData.status}
          </Typography>
          {trackingData.estimatedDelivery && (
            <Typography
              variant="body1"
              sx={{ mb: 2, color: colorPalette[1].rgba }}
            >
              {" "}
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
            Histórico de Movimentações:
          </Typography>
          {trackingData.events.length > 0 ? (
            <List>
              {trackingData.events.map((event, index) => (
                <React.Fragment key={index}>
                  <ListItem disablePadding sx={{ color: colorPalette[1].rgba }}>
                    {" "}
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
              Nenhuma movimentação registrada ainda.
            </Typography>
          )}
        </Paper>
      )}
    </Container>
  );
};
