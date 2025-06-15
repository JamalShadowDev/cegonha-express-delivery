import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  Modal,
  Box,
  Typography,
  TextField,
  Button,
  CircularProgress,
  Alert,
  MenuItem,
  InputAdornment,
  IconButton,
  Divider,
  Card,
  CardContent,
  Stack,
  Chip,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import InfoIcon from "@mui/icons-material/Info";
import { toast } from "react-toastify";
import { colorPalette } from "../types/colorPalette";
import type { Produto } from "../types/produto";

interface PedidoRequest {
  enderecoDestino: {
    cep: string;
    logradouro: string;
    numero: string;
    complemento?: string;
    bairro: string;
    cidade: string;
    uf: string;
    referencia?: string;
  };
  tipoEntrega: "PADRAO" | "EXPRESSA";
  descricaoBebe: string;
  pesoKg: number;
  alturaCm: number;
  valorDeclarado: number;
}

interface ViaCepResponse {
  cep: string;
  logradouro: string;
  complemento?: string;
  bairro: string;
  localidade: string;
  uf: string;
  ibge?: string;
  gia?: string;
  ddd?: string;
  siafi?: string;
  erro?: boolean;
}

interface PedidoModalProps {
  open: boolean;
  handleClose: () => void;
  produto: Produto;
}

export const ModalOrder: React.FC<PedidoModalProps> = ({
  open,
  handleClose,
  produto,
}) => {
  const [formData, setFormData] = useState<PedidoRequest>({
    enderecoDestino: {
      cep: "",
      logradouro: "",
      numero: "",
      complemento: "",
      bairro: "",
      cidade: "",
      uf: "",
      referencia: "",
    },
    tipoEntrega: "PADRAO",
    descricaoBebe: "",
    pesoKg: 0,
    alturaCm: 0,
    valorDeclarado: 0,
  });

  const [loading, setLoading] = useState<boolean>(false);
  const [loadingCep, setLoadingCep] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: { xs: "95%", md: 700 },
    bgcolor: "background.paper",
    border: "2px solid #000",
    boxShadow: 24,
    p: 4,
    borderRadius: "12px",
    maxHeight: "90vh",
    overflowY: "auto",
    backgroundColor: colorPalette[4].rgba,
    color: colorPalette[1].rgba,
  };

  // Função para criar descrição completa do bebê
  const createFullDescription = (produto: Produto) => {
    const parts = [];

    if (produto.nome) {
      parts.push(`Bebê ${produto.nome}`);
    }

    if (produto.descricao) {
      parts.push(produto.descricao);
    }

    if (produto.acessorios) {
      parts.push(`Acessórios inclusos: ${produto.acessorios}`);
    }

    return parts.join(" - ");
  };

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | { name?: string; value: unknown }
    >,
  ) => {
    const { name, value } = e.target;

    if (name?.startsWith("enderecoDestino.")) {
      setFormData((prev) => ({
        ...prev,
        enderecoDestino: {
          ...prev.enderecoDestino,
          [name.split(".")[1]]: value,
        },
      }));
    } else {
      setFormData((prev) => ({
        ...prev,
        [name as string]: value,
      }));
    }
  };

  const buscarCep = async () => {
    const cep = formData.enderecoDestino.cep.replace(/\D/g, "");

    if (cep.length !== 8) {
      toast.error("CEP deve ter 8 dígitos");
      return;
    }

    setLoadingCep(true);
    try {
      const response = await axios.get<ViaCepResponse>(
        `http://localhost:8080/api/enderecos/cep/${cep}`,
      );
      const data = response.data;

      if (data.erro) {
        toast.error("CEP não encontrado");
        return;
      }

      setFormData((prev) => ({
        ...prev,
        enderecoDestino: {
          ...prev.enderecoDestino,
          cep: data.cep,
          logradouro: data.logradouro || "",
          bairro: data.bairro || "",
          cidade: data.localidade || "",
          uf: data.uf || "",
          complemento: data.complemento || prev.enderecoDestino.complemento,
        },
      }));

      toast.success("CEP encontrado!");
    } catch (error) {
      console.error("Erro ao buscar CEP:", error);
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        toast.error("CEP não encontrado");
      } else {
        toast.error("Erro ao buscar CEP. Tente novamente.");
      }
    } finally {
      setLoadingCep(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/encomendas",
        formData,
      );
      toast.success(
        `Pedido de ${produto.nome} criado com sucesso! Código: ${response.data.codigo || response.data.id}`,
      );
      handleClose();
    } catch (err) {
      console.error("Erro ao fazer o pedido:", err);
      if (
        axios.isAxiosError(err) &&
        err.response &&
        err.response.data &&
        err.response.data.message
      ) {
        setError(err.response.data.message);
        toast.error(err.response.data.message);
      } else {
        setError("Não foi possível criar o pedido. Verifique os dados.");
        toast.error("Erro ao criar pedido!");
      }
    } finally {
      setLoading(false);
    }
  };

  // Effect para preencher dados automaticamente quando o produto mudar
  useEffect(() => {
    if (produto && open) {
      const fullDescription = createFullDescription(produto);

      setFormData((prev) => ({
        ...prev,
        descricaoBebe: fullDescription,
        pesoKg: produto.peso_kg || 0,
        alturaCm: produto.altura_cm || 0,
        valorDeclarado: 0, // Valor declarado continua sendo preenchido pelo usuário
      }));
    }
  }, [produto, open]);

  // Reset form when modal closes
  useEffect(() => {
    if (!open) {
      setFormData({
        enderecoDestino: {
          cep: "",
          logradouro: "",
          numero: "",
          complemento: "",
          bairro: "",
          cidade: "",
          uf: "",
          referencia: "",
        },
        tipoEntrega: "PADRAO",
        descricaoBebe: "",
        pesoKg: 0,
        alturaCm: 0,
        valorDeclarado: 0,
      });
      setError(null);
    }
  }, [open]);

  return (
    <Modal open={open} onClose={handleClose}>
      <Box sx={style} component="form" onSubmit={handleSubmit}>
        <Typography
          variant="h5"
          component="h2"
          gutterBottom
          sx={{ color: colorPalette[1].rgba, fontWeight: "bold", mb: 3 }}
        >
          Fazer Pedido para: {produto.nome}
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Divider sx={{ my: 2 }} />

        <Typography
          variant="h6"
          sx={{ mt: 3, mb: 2, color: colorPalette[1].rgba, fontWeight: "bold" }}
        >
          Endereço de Destino
        </Typography>

        <TextField
          label="CEP"
          name="enderecoDestino.cep"
          value={formData.enderecoDestino.cep}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton
                  onClick={buscarCep}
                  disabled={loadingCep || !formData.enderecoDestino.cep}
                  edge="end"
                  sx={{
                    color: colorPalette[1].rgba,
                    "&:disabled": {
                      color: "rgba(0, 0, 0, 0.26)",
                    },
                  }}
                >
                  {loadingCep ? <CircularProgress size={20} /> : <SearchIcon />}
                </IconButton>
              </InputAdornment>
            ),
          }}
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />

        <Box sx={{ display: "flex", gap: 2 }}>
          <TextField
            label="Logradouro"
            name="enderecoDestino.logradouro"
            value={formData.enderecoDestino.logradouro}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
            sx={{
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
            }}
          />
          <TextField
            label="Número"
            name="enderecoDestino.numero"
            value={formData.enderecoDestino.numero}
            onChange={handleChange}
            margin="normal"
            required
            sx={{
              width: "30%",
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
            }}
          />
        </Box>

        <TextField
          label="Complemento (Opcional)"
          name="enderecoDestino.complemento"
          value={formData.enderecoDestino.complemento}
          onChange={handleChange}
          fullWidth
          margin="normal"
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />

        <Box sx={{ display: "flex", gap: 2 }}>
          <TextField
            label="Bairro"
            name="enderecoDestino.bairro"
            value={formData.enderecoDestino.bairro}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
            sx={{
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
            }}
          />
          <TextField
            label="Cidade"
            name="enderecoDestino.cidade"
            value={formData.enderecoDestino.cidade}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
            sx={{
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
            }}
          />
          <TextField
            label="UF"
            name="enderecoDestino.uf"
            value={formData.enderecoDestino.uf}
            onChange={handleChange}
            margin="normal"
            required
            sx={{
              width: "20%",
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
            }}
          />
        </Box>

        <TextField
          label="Referência (Opcional)"
          name="enderecoDestino.referencia"
          value={formData.enderecoDestino.referencia}
          onChange={handleChange}
          fullWidth
          margin="normal"
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />

        <Divider sx={{ my: 3 }} />

        <Typography
          variant="h6"
          sx={{ mb: 2, color: colorPalette[1].rgba, fontWeight: "bold" }}
        >
          Detalhes do Pedido
        </Typography>

        <TextField
          select
          label="Tipo de Entrega"
          name="tipoEntrega"
          value={formData.tipoEntrega}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        >
          <MenuItem value="PADRAO">Padrão (mínimo 3 dias úteis)</MenuItem>
          <MenuItem value="ECONOMICA">Econômica (mínimo 7 dias úteis)</MenuItem>
          <MenuItem value="EXPRESSA">Expressa (mínimo 1 dia útil)</MenuItem>
        </TextField>

        <TextField
          label="Descrição Completa do Bebê"
          name="descricaoBebe"
          value={formData.descricaoBebe}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          disabled
          multiline
          rows={4}
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
            "& .MuiFormHelperText-root": {
              color: colorPalette[1].rgba,
              opacity: 0.7,
            },
          }}
        />

        <Box sx={{ display: "flex", gap: 2 }}>
          <TextField
            label="Peso (kg)"
            name="pesoKg"
            type="number"
            value={formData.pesoKg}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
            inputProps={{ step: "0.1", min: "0.1" }}
            helperText="Preenchido automaticamente"
            sx={{
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
              "& .MuiFormHelperText-root": {
                color: colorPalette[1].rgba,
                opacity: 0.7,
              },
            }}
          />
          <TextField
            label="Altura (cm)"
            name="alturaCm"
            type="number"
            value={formData.alturaCm}
            onChange={handleChange}
            fullWidth
            margin="normal"
            required
            inputProps={{ step: "0.1", min: "20" }}
            helperText="Preenchido automaticamente"
            sx={{
              "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
              "& .MuiOutlinedInput-root": {
                backgroundColor: colorPalette[0].rgba,
                color: colorPalette[1].rgba,
              },
              "& .MuiFormHelperText-root": {
                color: colorPalette[1].rgba,
                opacity: 0.7,
              },
            }}
          />
        </Box>

        <TextField
          label="Valor Declarado (R$)"
          name="valorDeclarado"
          type="number"
          value={formData.valorDeclarado}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          inputProps={{ step: "0.01", min: "0" }}
          helperText="Informe o valor do bebê para fins de seguro"
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
            "& .MuiFormHelperText-root": {
              color: colorPalette[1].rgba,
              opacity: 0.7,
            },
          }}
        />

        <Box
          sx={{ display: "flex", justifyContent: "flex-end", mt: 4, gap: 2 }}
        >
          <Button
            variant="outlined"
            onClick={handleClose}
            sx={{
              color: colorPalette[1].rgba,
              borderColor: colorPalette[1].rgba,
              "&:hover": {
                borderColor: colorPalette[2].rgba,
                color: colorPalette[2].rgba,
              },
            }}
          >
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            sx={{
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
              "Confirmar Pedido"
            )}
          </Button>
        </Box>
      </Box>
    </Modal>
  );
};
