import { Route, Routes } from "react-router-dom";
import "./App.css";
import { ToastContainer } from "react-toastify";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Homepage from "./pages/Homepage";
import Error from "./pages/Error";
import { Products } from "./pages/Products";
import { Tracking } from "./pages/Tracking";
import { Box } from "@mui/material";
import { AdminPage } from "./pages/AdminPage";

function App() {
  return (
    <>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          minHeight: "100vh",
          overflowX: "hidden",
        }}
      >
        <Navbar />

        <Routes>
          <Route path="/" element={<Homepage />} />
          <Route path="/tracking" element={<Tracking />} />
          <Route path="/products" element={<Products />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="*" element={<Error />} />
        </Routes>
      </Box>

      <Footer />

      <ToastContainer />
    </>
  );
}

export default App;
