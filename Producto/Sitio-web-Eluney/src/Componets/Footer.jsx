import React from 'react';
import '../css/Footer.css';
// Puedes importar íconos de bibliotecas como FontAwesome o usar imágenes simples
import { FaInstagram, FaTiktok, FaWhatsapp, FaMapMarkerAlt } from 'react-icons/fa';

const Footer = () => {
  return (
    <footer className="footer-container">
      <div className="footer-content">
        
        {/* Columna 1: Branding / Sobre Nosotros */}
        <div className="footer-column">
          <h3 className="footer-logo">Café Eluney</h3>
          <p className="footer-description">
            
          </p>
          <div className="footer-socials">
            <a href="#" aria-label="Instagram"><FaInstagram /></a>
            <a href="#" aria-label="WhatsApp"><FaWhatsapp /></a>
          </div>
        </div>

        {/* Columna 2: Enlaces Rápidos */}
        <div className="footer-column">
          <h4>Explorar</h4>
          <ul>
            <li><a href="#inicio">Inicio</a></li>
            <li><a href="#menu">Nuestro Menú</a></li>
            <li><a href="#recomendados">Recomendados</a></li>
            <li><a href="#visitanos">Ubicación</a></li>
          </ul>
        </div>

        {/* Columna 3: Contacto Directo */}
        <div className="footer-column">
          <h4>Contacto</h4>
          <p><FaMapMarkerAlt className="icon" /> Cantera 386 Local B, Chorrillos Viña del Mar, Chile</p>
          <p>📞 +56 920392964</p>
        </div>

        {/* Columna 4: Horarios */}
        <div className="footer-column">
          <h4>Horarios</h4>
          <p>Lunes a Viernes: 09:00 - 20:00</p>
          <p>Sábado y Domingo: Cerrado</p>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; 2026 Café Eluney.</p>
      </div>
    </footer>
  );
};

export default Footer;