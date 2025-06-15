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
  Card,
  CardContent,
  Chip,
  Divider,
  Grid,
} from "@mui/material";
import {
  Search as SearchIcon,
  LocalShipping as ShippingIcon,
  AccessTime as TimeIcon,
  AttachMoney as MoneyIcon,
  TrackChanges as StatusIcon,
} from "@mui/icons-material";
import { toast } from "react-toastify";
import { colorPalette } from "../types/colorPalette";

interface EncomendaResponse {
  codigo: string;
  status: string;
  valorFrete: string;
  tempoEstimadoEntrega: string;
}

const getStatusConfig = (status: string) => {
  const normalizedStatus = status.toLowerCase();

  switch (normalizedStatus) {
    case "pendente":
      return {
        color: "#FF9800" as const,
        backgroundColor: "rgba(255, 152, 0, 0.1)",
        label: "Pendente",
        icon: "⏳",
      };
    case "confirmada":
      return {
        color: "#2196F3" as const,
        backgroundColor: "rgba(33, 150, 243, 0.1)",
        label: "Confirmada",
        icon: "✅",
      };
    case "em trânsito":
    case "em_transito":
      return {
        color: "#9C27B0" as const,
        backgroundColor: "rgba(156, 39, 176, 0.1)",
        label: "Em Trânsito",
        icon: "🚚",
      };
    case "entregue":
      return {
        color: "#4CAF50" as const,
        backgroundColor: "rgba(76, 175, 80, 0.1)",
        label: "Entregue",
        icon: "📦",
      };
    case "cancelada":
      return {
        color: "#F44336" as const,
        backgroundColor: "rgba(244, 67, 54, 0.1)",
        label: "Cancelada",
        icon: "❌",
      };
    default:
      return {
        color: "#757575" as const,
        backgroundColor: "rgba(117, 117, 117, 0.1)",
        label: status,
        icon: "📋",
      };
  }
};

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
        `http://localhost:8080/api/encomendas/${codigoEncomenda}`,
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

  const statusConfig = encomendaData
    ? getStatusConfig(encomendaData.status)
    : null;

  return (
    <Container maxWidth="md" sx={{ py: 4, flexGrow: 1 }}>
      {/* Header com ícone */}
      <Box sx={{ textAlign: "center", mb: 6 }}>
        <ShippingIcon
          sx={{
            fontSize: 48,
            color: colorPalette[2].rgba,
            mb: 2,
          }}
        />
        <Typography
          variant="h4"
          component="h1"
          gutterBottom
          sx={{
            color: colorPalette[1].rgba,
            fontWeight: "bold",
          }}
        >
          Rastrear Encomenda
        </Typography>
        <Typography
          variant="body1"
          sx={{
            color: colorPalette[1].rgba,
            opacity: 0.8,
          }}
        >
          Digite o código da sua encomenda para acompanhar o status de entrega
        </Typography>
      </Box>

      {/* Formulário de busca */}
      <Paper
        elevation={3}
        sx={{
          p: { xs: 3, md: 4 },
          mb: 4,
          borderRadius: "16px",
          backgroundColor: colorPalette[4].rgba,
          border: `2px solid ${colorPalette[0].rgba}`,
        }}
      >
        <Box
          component="form"
          onSubmit={handleTrackOrder}
          sx={{ display: "flex", flexDirection: "column", gap: 3 }}
        >
          <TextField
            label="Código da Encomenda"
            placeholder="Ex: CE1234567890123"
            variant="outlined"
            fullWidth
            value={codigoEncomenda}
            onChange={(e) => setCodigoEncomenda(e.target.value)}
            required
            error={submitted && !codigoEncomenda.trim()}
            helperText={
              submitted && !codigoEncomenda.trim()
                ? "O código da encomenda é obrigatório"
                : "Digite o código que recebeu ao fazer o pedido"
            }
            sx={{
              "& .MuiOutlinedInput-root": {
                backgroundColor: "white",
                borderRadius: "12px",
                fontSize: "1.1rem",
                "& fieldset": {
                  borderColor: colorPalette[0].rgba,
                  borderWidth: "2px",
                },
                "&:hover fieldset": {
                  borderColor: colorPalette[2].rgba,
                },
                "&.Mui-focused fieldset": {
                  borderColor: colorPalette[2].rgba,
                },
              },
              "& .MuiInputLabel-root": {
                color: colorPalette[1].rgba,
                fontWeight: "500",
              },
              "& .MuiFormHelperText-root": {
                fontSize: "0.9rem",
                marginTop: "8px",
              },
            }}
          />

          <Button
            type="submit"
            variant="contained"
            size="large"
            disabled={loading}
            startIcon={
              loading ? <CircularProgress size={20} /> : <SearchIcon />
            }
            sx={{
              py: 2,
              borderRadius: "12px",
              backgroundColor: colorPalette[2].rgba,
              color: "white",
              fontSize: "1.1rem",
              fontWeight: "bold",
              textTransform: "none",
              boxShadow: "0 4px 12px rgba(3, 136, 166, 0.3)",
              "&:hover": {
                backgroundColor: colorPalette[1].rgba,
                boxShadow: "0 6px 16px rgba(1, 46, 64, 0.4)",
                transform: "translateY(-2px)",
              },
              "&:disabled": {
                backgroundColor: "rgba(0,0,0,0.12)",
                color: "rgba(0,0,0,0.26)",
              },
              transition: "all 0.3s ease",
            }}
          >
            {loading ? "Rastreando..." : "Rastrear Encomenda"}
          </Button>
        </Box>
      </Paper>

      {/* Mensagem de erro */}
      {error && (
        <Alert
          severity="error"
          sx={{
            mb: 3,
            borderRadius: "12px",
            fontSize: "1rem",
          }}
        >
          {error}
        </Alert>
      )}

      {/* Resultados do rastreamento */}
      {encomendaData && statusConfig && (
        <Card
          elevation={4}
          sx={{
            borderRadius: "20px",
            backgroundColor: "white",
            border: `3px solid ${statusConfig.color}`,
            boxShadow: `0 8px 32px ${statusConfig.backgroundColor}`,
            overflow: "hidden",
          }}
        >
          {/* Header do card com status */}
          <Box
            sx={{
              background: `linear-gradient(135deg, ${statusConfig.color}15, ${statusConfig.color}25)`,
              p: 3,
              borderBottom: `2px solid ${statusConfig.color}20`,
            }}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 2, mb: 2 }}>
              <Typography
                variant="h5"
                sx={{
                  color: colorPalette[1].rgba,
                  fontWeight: "bold",
                  fontSize: "1.3rem",
                }}
              >
                {statusConfig.icon} Encomenda Localizada
              </Typography>
            </Box>

            <Typography
              variant="h6"
              sx={{
                color: statusConfig.color,
                fontWeight: "bold",
                fontSize: "1.1rem",
                fontFamily: "monospace",
                backgroundColor: "white",
                padding: "8px 16px",
                borderRadius: "8px",
                border: `2px solid ${statusConfig.color}30`,
                display: "inline-block",
              }}
            >
              {encomendaData.codigo}
            </Typography>
          </Box>

          <CardContent sx={{ p: 4 }}>
            {/* Status atual em destaque */}
            <Box sx={{ textAlign: "center", mb: 4 }}>
              <Chip
                icon={<StatusIcon />}
                label={statusConfig.label}
                sx={{
                  backgroundColor: statusConfig.backgroundColor,
                  color: statusConfig.color,
                  fontSize: "1.1rem",
                  fontWeight: "bold",
                  height: "48px",
                  borderRadius: "24px",
                  border: `2px solid ${statusConfig.color}`,
                  "& .MuiChip-icon": {
                    color: statusConfig.color,
                  },
                }}
              />
            </Box>

            <Divider sx={{ my: 3, borderColor: `${statusConfig.color}30` }} />

            {/* Grid com informações detalhadas */}
            <Grid container spacing={3}>
              {/* Valor do Frete */}
              <Grid item xs={12} sm={6}>
                <Paper
                  elevation={2}
                  sx={{
                    p: 3,
                    borderRadius: "16px",
                    backgroundColor: colorPalette[0].rgba,
                    border: `2px solid ${colorPalette[0].rgba}`,
                    textAlign: "center",
                    transition: "all 0.3s ease",
                    "&:hover": {
                      transform: "translateY(-4px)",
                      boxShadow: "0 8px 24px rgba(133, 222, 242, 0.3)",
                    },
                  }}
                >
                  <MoneyIcon
                    sx={{
                      fontSize: 40,
                      color: colorPalette[2].rgba,
                      mb: 1,
                    }}
                  />
                  <Typography
                    variant="body2"
                    sx={{
                      color: colorPalette[1].rgba,
                      fontWeight: "500",
                      mb: 1,
                      opacity: 0.8,
                    }}
                  >
                    Valor do Frete
                  </Typography>
                  <Typography
                    variant="h6"
                    sx={{
                      color: colorPalette[1].rgba,
                      fontWeight: "bold",
                      fontSize: "1.4rem",
                    }}
                  >
                    {encomendaData.valorFrete}
                  </Typography>
                </Paper>
              </Grid>

              {/* Tempo de Entrega */}
              <Grid item xs={12} sm={6}>
                <Paper
                  elevation={2}
                  sx={{
                    p: 3,
                    borderRadius: "16px",
                    backgroundColor: colorPalette[3].rgba,
                    border: `2px solid ${colorPalette[3].rgba}`,
                    textAlign: "center",
                    transition: "all 0.3s ease",
                    "&:hover": {
                      transform: "translateY(-4px)",
                      boxShadow: "0 8px 24px rgba(242, 175, 160, 0.3)",
                    },
                  }}
                >
                  <TimeIcon
                    sx={{
                      fontSize: 40,
                      color: colorPalette[1].rgba,
                      mb: 1,
                    }}
                  />
                  <Typography
                    variant="body2"
                    sx={{
                      color: colorPalette[1].rgba,
                      fontWeight: "500",
                      mb: 1,
                      opacity: 0.8,
                    }}
                  >
                    Tempo Estimado
                  </Typography>
                  <Typography
                    variant="h6"
                    sx={{
                      color: colorPalette[1].rgba,
                      fontWeight: "bold",
                      fontSize: "1.4rem",
                    }}
                  >
                    {encomendaData.tempoEstimadoEntrega}
                  </Typography>
                </Paper>
              </Grid>
            </Grid>

            {/* Dica adicional baseada no status */}
            <Box
              sx={{
                mt: 4,
                p: 3,
                backgroundColor: `${statusConfig.color}10`,
                borderRadius: "12px",
                border: `1px solid ${statusConfig.color}30`,
              }}
            >
              <Typography
                variant="body2"
                sx={{
                  color: statusConfig.color,
                  fontWeight: "500",
                  textAlign: "center",
                  fontSize: "0.95rem",
                }}
              >
                {statusConfig.label === "Pendente" &&
                  "📋 Sua encomenda foi recebida e está sendo processada."}
                {statusConfig.label === "Confirmada" &&
                  "✅ Encomenda confirmada! Em breve será enviada."}
                {statusConfig.label === "Em Trânsito" &&
                  "🚚 Sua encomenda está a caminho do destino!"}
                {statusConfig.label === "Entregue" &&
                  "🎉 Encomenda entregue com sucesso! Obrigado pela preferência."}
                {statusConfig.label === "Cancelada" &&
                  "❌ Esta encomenda foi cancelada."}
              </Typography>
            </Box>
          </CardContent>
        </Card>
      )}
    </Container>
  );
};
