import { useState, useEffect } from "react";
import "./App.css";
interface EncomendaResponse {
  codigo: string;
  status: string;
  valorFrete: string;
  tempoEstimadoEntrega: string;
}
function App() {
  const [encomendas, setEncomendas] = useState<EncomendaResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchEncomendas = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch("http://localhost:8080/api/encomendas");

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data: EncomendaResponse[] = await response.json();
      setEncomendas(data);
      console.log("✅ CORS funcionou! Dados recebidos:", data);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : "Erro desconhecido";
      setError(errorMessage);
      console.error("❌ Erro CORS ou API:", errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>🐣 Teste CORS - CegonhaExpress</h1>

        <div style={{ margin: "20px 0" }}>
          <button
            onClick={fetchEncomendas}
            disabled={loading}
            style={{
              padding: "10px 20px",
              fontSize: "16px",
              backgroundColor: "#007bff",
              color: "white",
              border: "none",
              borderRadius: "5px",
              cursor: loading ? "not-allowed" : "pointer",
            }}
          >
            {loading ? "Carregando..." : "Buscar Encomendas"}
          </button>
        </div>

        {error && (
          <div
            style={{
              color: "red",
              padding: "10px",
              border: "1px solid red",
              borderRadius: "5px",
              margin: "10px 0",
              backgroundColor: "#ffe6e6",
            }}
          >
            <strong>Erro:</strong> {error}
          </div>
        )}

        {encomendas.length > 0 && (
          <div style={{ marginTop: "20px", textAlign: "left" }}>
            <h2>📦 Encomendas Encontradas:</h2>
            <ul>
              {encomendas.map((encomenda) => (
                <li
                  key={encomenda.codigo}
                  style={{
                    margin: "10px 0",
                    padding: "10px",
                    border: "1px solid #ccc",
                    borderRadius: "5px",
                  }}
                >
                  <strong>Código:</strong> {encomenda.codigo}
                  <br />
                  <strong>Status:</strong> {encomenda.status}
                  <br />
                  <strong>Valor Frete:</strong> {encomenda.valorFrete}
                  <br />
                  <strong>Prazo:</strong> {encomenda.tempoEstimadoEntrega}
                </li>
              ))}
            </ul>
          </div>
        )}

        <div style={{ marginTop: "30px", fontSize: "14px", color: "#666" }}>
          <p>🎯 Esta aplicação testa se o CORS está configurado corretamente</p>
          <p>🌐 Origem: localhost:3000 → Destino: localhost:8080</p>
        </div>
      </header>
    </div>
  );
}

export default App;
