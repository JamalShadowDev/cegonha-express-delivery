import { Route, Routes } from "react-router-dom";
import "./App.css";
import { ToastContainer } from "react-toastify";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Homepage from "./pages/Homepage";
import Error from "./pages/Error";
import Products from "./pages/Products";

function App() {
  return (
    <>
      <Navbar />

      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/register" element="" />
        <Route path="/products" element={<Products />} />
        <Route path="/product/:id" element="" />
        <Route path="/about-us" element="" />
        <Route path="*" element={<Error />} />
      </Routes>

      <Footer />

      <ToastContainer />
    </>
  );
}

export default App;
