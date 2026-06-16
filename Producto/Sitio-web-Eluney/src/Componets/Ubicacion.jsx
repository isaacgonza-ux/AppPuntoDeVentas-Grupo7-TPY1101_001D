import React from 'react';
import '../css/Ubicacion.css';

const Ubicacion = () => {
  return (
    <section className="ubicacion-section">
      <div className="header-text">
        <h2>Visítanos</h2>
        <p>Te esperamos con el mejor café y un ambiente acogedor.</p>
      </div>

      <div className="map-container">
        <iframe 
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3344.8812164187657!2d-71.53710852448444!3d-33.033259773557575!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9689de653adeaaab%3A0x14c90b8af525cb49!2sCantera%20386%2C%20LOCAL%20B%2C%202570088%20Vi%C3%B1a%20del%20Mar%2C%20Valpara%C3%ADso!5e0!3m2!1ses-419!2scl!4v1775853896576!5m2!1ses-419!2scl" 
          width="100%" 
          height="450" 
          style={{ border: 0 }} 
          allowFullScreen="" 
          loading="lazy"
          title="Ubicación de la cafetería"
        ></iframe>
      </div>

      <div className="info-contacto">
        <div className="info-item">
          <strong>Dirección:</strong>
          <span>Cantera 386 Local B, Chorrillos Viña del Mar, Chile</span>
        </div>
        <div className="info-item">
          <strong>Horario:</strong>
          <span>Lunes a Viernes: 09:00 - 20:00</span>
        </div>
      </div>
    </section>
  );
};

export default Ubicacion;