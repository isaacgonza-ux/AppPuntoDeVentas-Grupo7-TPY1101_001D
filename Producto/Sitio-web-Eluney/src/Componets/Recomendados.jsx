import React from 'react';
import '../css/Recomendados.css'; 
import { productos } from '../data/Productos';

const Recomendados = () => {
  // Eliminamos useRef y scrollRight para simplificar el código
  return (
    <section className="recomendados-section">
      <div className="header-section">
        <div className="header-text">
          <h2>Recomendados</h2>
          {/* El texto ahora es más llamativo gracias al CSS anterior */}
          <p>Nuestras joyas culinarias: lo más amado por nuestros clientes esta semana.</p>
        </div>
      </div>

      <div className="cards-grid">
        {/* Usamos .slice(0, 4) para mostrar exactamente una corrida de 4 */}
        {productos.slice(0, 4).map((producto) => (
          <div className="card" key={producto.id}>
            <img 
              src={producto.imagen} 
              alt={producto.titulo} 
              className="card-image" 
            />
            <div className="card-content">
              <span className="tag">{producto.etiqueta}</span>
              <h3 className="card-title">{producto.titulo}</h3>
              <p className="card-desc">{producto.descripcion}</p>
              <div className="card-footer">
                <span className="price">{producto.precio}</span>
                <button className="add-btn" aria-label="Agregar">+</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default Recomendados;