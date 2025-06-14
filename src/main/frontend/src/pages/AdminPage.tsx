import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  Container,
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  CircularProgress,
  Alert,
  Modal,
  TextField,
  IconButton,
  Chip,
} from "@mui/material";
import { toast } from "react-toastify";
import RefreshIcon from "@mui/icons-material/Refresh";
import CloseIcon from "@mui/icons-material/Close";
import { colorPalette } from "../types/colorPalette";

interface Encomenda {
  codigo: string;
  status: string;
  valorFrete: string;
  tempoEstimadoEntrega: string;
}

interface CancelModalData {
  isOpen: boolean;
  codigo: string;
}

export const AdminPage: React.FC = () => {
  const [encomendas, setEncomendas] = useState<Encomenda[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [loadingActions, setLoadingActions] = useState<Set<string>>(new Set());
  const [cancelModal, setCancelModal] = useState<CancelModalData>({
    isOpen: false,
    codigo: "",
  });
  const [cancelMotivo, setCancelMotivo] = useState<string>("");
  const [error, setError] = useState<string | null>(null);

  // Carregar encomendas
  const fetchEncomendas = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await axios.get<Encomenda[]>(
        "http://localhost:8080/api/encomendas",
      );
      setEncomendas(response.data);
      toast.success("Encomendas carregadas com sucesso!");
    } catch (err) {
      console.error("Erro ao carregar encomendas:", err);
      setError("Erro ao carregar as encomendas. Tente novamente.");
      toast.error("Erro ao carregar encomendas!");
    } finally {
      setLoading(false);
    }
  };

  // Formatar status da API para exibição
  const formatStatusFromApi = (status: string) => {
    const statusMap: { [key: string]: string } = {
      PENDENTE: "Pendente",
      CONFIRMADA: "Confirmada",
      EM_TRANSITO: "Em Trânsito",
      ENTREGUE: "Entregue",
      CANCELADA: "Cancelada",
    };
    return statusMap[status.toUpperCase()] || status;
  };

  // Avançar status
  const handleAvancarStatus = async (codigo: string) => {
    setLoadingActions((prev) => new Set(prev).add(codigo));

    try {
      const response = await axios.put<string>(
        `http://localhost:8080/api/encomendas/${codigo}/status`,
      );

      const formattedStatus = formatStatusFromApi(response.data);

      // Atualizar encomenda na lista
      setEncomendas((prev) =>
        prev.map((encomenda) =>
          encomenda.codigo === codigo
            ? { ...encomenda, status: formattedStatus }
            : encomenda,
        ),
      );

      toast.success(`Status avançado para: ${formattedStatus}`);
    } catch (err) {
      console.error("Erro ao avançar status:", err);
      let errorMessage = "Erro ao avançar status da encomenda.";

      if (axios.isAxiosError(err) && err.response && err.response.data) {
        errorMessage = err.response.data.message || errorMessage;
      }

      toast.error(errorMessage);
    } finally {
      setLoadingActions((prev) => {
        const newSet = new Set(prev);
        newSet.delete(codigo);
        return newSet;
      });
    }
  };

  // Abrir modal de cancelamento
  const handleOpenCancelModal = (codigo: string) => {
    setCancelModal({ isOpen: true, codigo });
    setCancelMotivo("");
  };

  // Fechar modal de cancelamento
  const handleCloseCancelModal = () => {
    setCancelModal({ isOpen: false, codigo: "" });
    setCancelMotivo("");
  };

  // Cancelar encomenda
  const handleCancelarEncomenda = async () => {
    if (!cancelMotivo.trim()) {
      toast.error("Motivo do cancelamento é obrigatório!");
      return;
    }

    const { codigo } = cancelModal;
    setLoadingActions((prev) => new Set(prev).add(codigo));

    try {
      const response = await axios.put<string>(
        `http://localhost:8080/api/encomendas/${codigo}/cancelar`,
        { motivo: cancelMotivo },
      );

      const formattedStatus = formatStatusFromApi(response.data);

      // Atualizar encomenda na lista
      setEncomendas((prev) =>
        prev.map((encomenda) =>
          encomenda.codigo === codigo
            ? { ...encomenda, status: formattedStatus }
            : encomenda,
        ),
      );

      toast.success(`Encomenda cancelada: ${formattedStatus}`);
      handleCloseCancelModal();
    } catch (err) {
      console.error("Erro ao cancelar encomenda:", err);
      let errorMessage = "Erro ao cancelar a encomenda.";

      if (axios.isAxiosError(err) && err.response && err.response.data) {
        errorMessage = err.response.data.message || errorMessage;
      }

      toast.error(errorMessage);
    } finally {
      setLoadingActions((prev) => {
        const newSet = new Set(prev);
        newSet.delete(codigo);
        return newSet;
      });
    }
  };

  // Carregar dados na inicialização
  useEffect(() => {
    fetchEncomendas();
  }, []);

  // Determinar cor do status
  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case "pendente":
        return "warning";
      case "confirmada":
        return "info";
      case "em trânsito":
      case "em_transito":
        return "primary";
      case "entregue":
        return "success";
      case "cancelada":
        return "error";
      default:
        return "default";
    }
  };

  // Verificar se pode avançar status
  const canAdvanceStatus = (status: string) => {
    const normalizedStatus = status.toLowerCase();
    return !["entregue", "cancelada"].includes(normalizedStatus);
  };

  // Verificar se pode cancelar
  const canCancel = (status: string) => {
    const normalizedStatus = status.toLowerCase();
    return !["entregue", "cancelada", "cancelado"].includes(normalizedStatus);
  };

  if (loading) {
    return (
      <Container
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: "80vh",
          flexDirection: "column",
          gap: 2,
        }}
      >
        <CircularProgress size={50} />
        <Typography variant="h6">Carregando encomendas...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4, flexGrow: 1 }}>
      {/* Header */}
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 4,
        }}
      >
        <Typography variant="h4" component="h1" fontWeight="bold">
          Painel Administrativo
        </Typography>
        <Button
          variant="outlined"
          startIcon={<RefreshIcon />}
          onClick={fetchEncomendas}
          disabled={loading}
          sx={{
            borderColor: colorPalette[1].rgba,
            color: colorPalette[1].rgba,
            "&:hover": {
              borderColor: colorPalette[2].rgba,
              backgroundColor: colorPalette[0].rgba,
            },
          }}
        >
          Atualizar
        </Button>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Lista de Encomendas */}
      {encomendas.length === 0 ? (
        <Alert severity="info">Nenhuma encomenda encontrada no sistema.</Alert>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          {encomendas.map((encomenda) => {
            const isLoading = loadingActions.has(encomenda.codigo);
            const canAdvance = canAdvanceStatus(encomenda.status);
            const canCancelEncomenda = canCancel(encomenda.status);

            return (
              <Card
                key={encomenda.codigo}
                sx={{
                  borderRadius: "12px",
                  boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
                  backgroundColor: colorPalette[4].rgba,
                  border: `1px solid ${colorPalette[0].rgba}`,
                }}
              >
                <CardContent sx={{ p: 3 }}>
                  <Box
                    sx={{
                      display: "flex",
                      flexDirection: { xs: "column", md: "row" },
                      justifyContent: "space-between",
                      alignItems: { xs: "stretch", md: "center" },
                      gap: 2,
                    }}
                  >
                    {/* Informações da Encomenda */}
                    <Box
                      sx={{
                        display: "flex",
                        flexDirection: { xs: "column", sm: "row" },
                        gap: { xs: 1, sm: 3 },
                        alignItems: { xs: "flex-start", sm: "center" },
                        flex: 1,
                      }}
                    >
                      <Typography
                        variant="h6"
                        fontWeight="bold"
                        sx={{ color: colorPalette[1].rgba }}
                      >
                        {encomenda.codigo}
                      </Typography>

                      <Chip
                        label={encomenda.status}
                        color={getStatusColor(encomenda.status) as any}
                        variant="filled"
                        sx={{ fontWeight: "bold" }}
                      />

                      <Typography
                        variant="body1"
                        sx={{ color: colorPalette[1].rgba }}
                      >
                        <strong>Valor:</strong> {encomenda.valorFrete}
                      </Typography>

                      <Typography
                        variant="body1"
                        sx={{ color: colorPalette[1].rgba }}
                      >
                        <strong>Prazo:</strong> {encomenda.tempoEstimadoEntrega}
                      </Typography>
                    </Box>

                    {/* Botões de Ação */}
                    <Box
                      sx={{
                        display: "flex",
                        gap: 2,
                        flexDirection: { xs: "column", sm: "row" },
                        minWidth: { xs: "100%", sm: "auto" },
                      }}
                    >
                      <Button
                        variant="contained"
                        onClick={() => handleAvancarStatus(encomenda.codigo)}
                        disabled={isLoading || !canAdvance}
                        sx={{
                          backgroundColor: colorPalette[1].rgba,
                          color: colorPalette[4].rgba,
                          "&:hover": {
                            backgroundColor: colorPalette[2].rgba,
                          },
                          "&:disabled": {
                            backgroundColor: "rgba(0,0,0,0.12)",
                          },
                          minWidth: "120px",
                        }}
                      >
                        {isLoading ? (
                          <CircularProgress size={20} color="inherit" />
                        ) : (
                          "Avançar Status"
                        )}
                      </Button>

                      <Button
                        variant="contained"
                        onClick={() => handleOpenCancelModal(encomenda.codigo)}
                        disabled={isLoading || !canCancelEncomenda}
                        sx={{
                          backgroundColor: colorPalette[3].rgba,
                          color: colorPalette[1].rgba,
                          "&:hover": {
                            backgroundColor: "#e57373",
                          },
                          "&:disabled": {
                            backgroundColor: "rgba(0,0,0,0.12)",
                          },
                          minWidth: "120px",
                        }}
                      >
                        {isLoading ? (
                          <CircularProgress size={20} color="inherit" />
                        ) : (
                          "Cancelar"
                        )}
                      </Button>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            );
          })}
        </Box>
      )}

      {/* Modal de Cancelamento */}
      <Modal
        open={cancelModal.isOpen}
        onClose={handleCloseCancelModal}
        aria-labelledby="cancel-modal-title"
      >
        <Box
          sx={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: { xs: "90%", sm: 500 },
            bgcolor: colorPalette[4].rgba,
            border: `2px solid ${colorPalette[1].rgba}`,
            boxShadow: 24,
            p: 4,
            borderRadius: "12px",
          }}
        >
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Typography
              id="cancel-modal-title"
              variant="h5"
              component="h2"
              sx={{ color: colorPalette[1].rgba, fontWeight: "bold" }}
            >
              Cancelar Encomenda
            </Typography>
            <IconButton
              onClick={handleCloseCancelModal}
              sx={{ color: colorPalette[1].rgba }}
            >
              <CloseIcon />
            </IconButton>
          </Box>

          <Typography
            variant="body1"
            sx={{ mb: 3, color: colorPalette[1].rgba }}
          >
            <strong>Código:</strong> {cancelModal.codigo}
          </Typography>

          <TextField
            label="Motivo do Cancelamento"
            multiline
            rows={4}
            fullWidth
            value={cancelMotivo}
            onChange={(e) => setCancelMotivo(e.target.value)}
            required
            sx={{
              mb: 3,
              "& .MuiInputLabel-root": {
                color: colorPalette[1].rgba,
              },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                "& fieldset": {
                  borderColor: colorPalette[1].rgba,
                },
                "&:hover fieldset": {
                  borderColor: colorPalette[2].rgba,
                },
                "&.Mui-focused fieldset": {
                  borderColor: colorPalette[2].rgba,
                },
              },
            }}
          />

          <Box
            sx={{
              display: "flex",
              justifyContent: "flex-end",
              gap: 2,
            }}
          >
            <Button
              variant="outlined"
              onClick={handleCloseCancelModal}
              sx={{
                borderColor: colorPalette[1].rgba,
                color: colorPalette[1].rgba,
                "&:hover": {
                  borderColor: colorPalette[2].rgba,
                  backgroundColor: colorPalette[0].rgba,
                },
              }}
            >
              Cancelar
            </Button>
            <Button
              variant="contained"
              onClick={handleCancelarEncomenda}
              disabled={
                !cancelMotivo.trim() || loadingActions.has(cancelModal.codigo)
              }
              sx={{
                backgroundColor: colorPalette[3].rgba,
                color: colorPalette[1].rgba,
                "&:hover": {
                  backgroundColor: "#e57373",
                },
              }}
            >
              {loadingActions.has(cancelModal.codigo) ? (
                <CircularProgress size={20} color="inherit" />
              ) : (
                "Confirmar Cancelamento"
              )}
            </Button>
          </Box>
        </Box>
      </Modal>
    </Container>
  );
};
