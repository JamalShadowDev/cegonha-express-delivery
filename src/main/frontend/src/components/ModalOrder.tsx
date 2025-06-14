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
} from "@mui/material";
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
    descricaoBebe: produto.nome || "",
    pesoKg: 0,
    alturaCm: 0,
    valorDeclarado: 0,
  });

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: { xs: "90%", md: 600 },
    bgcolor: "background.paper",
    border: "2px solid #000",
    boxShadow: 24,
    p: 4,
    borderRadius: "8px",
    maxHeight: "90vh",
    overflowY: "auto",
    backgroundColor: colorPalette[4].rgba,
    color: colorPalette[1].rgba,
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await axios.post("/api/encomendas", formData);
      toast.success(
        `Pedido de ${produto.nome} criado com sucesso! ID: ${response.data.id || response.data.codigo}`,
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

  useEffect(() => {
    if (produto) {
      setFormData((prev) => ({
        ...prev,
        descricaoBebe: produto.nome || "",
      }));
    }
  }, [produto]);

  return (
    <Modal open={open} onClose={handleClose}>
      <Box sx={style} component="form" onSubmit={handleSubmit}>
        <Typography
          variant="h5"
          component="h2"
          gutterBottom
          sx={{ color: colorPalette[1].rgba }}
        >
          Fazer Pedido para: {produto.nome}
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Typography
          variant="h6"
          sx={{ mt: 3, mb: 1, color: colorPalette[1].rgba }}
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
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />
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

        <Typography
          variant="h6"
          sx={{ mt: 3, mb: 1, color: colorPalette[1].rgba }}
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
          <MenuItem value="PADRAO">Padrão</MenuItem>
          <MenuItem value="EXPRESSA">Expressa</MenuItem>
        </TextField>
        <TextField
          label="Descrição do Bebê"
          name="descricaoBebe"
          value={formData.descricaoBebe}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          multiline
          rows={3}
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />
        <TextField
          label="Peso (kg)"
          name="pesoKg"
          type="number"
          value={formData.pesoKg}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          inputProps={{ step: "0.1" }}
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
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
          inputProps={{ step: "0.1" }}
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />
        <TextField
          label="Valor Declarado"
          name="valorDeclarado"
          type="number"
          value={formData.valorDeclarado}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          inputProps={{ step: "0.01" }}
          sx={{
            "& .MuiInputLabel-root": { color: colorPalette[1].rgba },
            "& .MuiOutlinedInput-root": {
              backgroundColor: colorPalette[0].rgba,
              color: colorPalette[1].rgba,
            },
          }}
        />

        <Box
          sx={{ display: "flex", justifyContent: "flex-end", mt: 3, gap: 2 }}
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
