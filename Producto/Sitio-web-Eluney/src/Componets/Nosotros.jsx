import { useState } from "react";
import "../css/Nosotros.css";
import { useRef } from "react";

const imagenes = [
  "img/local-1.JPG",
  "img/local-2.JPG",
  "img/local-3.JPG  ",
  "img/local-4.JPG",
];

const caracteristicas = [
  {
    icono: "🍽️",
    titulo: "Calidad Innegociable",
    descripcion: "Buenos productos elaborados al momento",
  },
  {
    icono: "❤️",
    titulo: "Servicio con Pasión",
    descripcion: "Tenemos mesa disponible y un buen ambiente para que disfrutes tu pedido aquí mismo..",
  },
  {
    icono: "🕐",
    titulo: "Trayectoria",
    descripcion: "Desde 2018 en el negocio de la comida",
  },
];



export default function Nosotros() {
  const [actual, setActual] = useState(0);

  const touchInicio = useRef(null);

const handleTouchStart = (e) => {
  touchInicio.current = e.touches[0].clientX;
};

const handleTouchEnd = (e) => {
  if (touchInicio.current === null) return;
  const diferencia = touchInicio.current - e.changedTouches[0].clientX;
  if (diferencia > 50) {
    irSiguiente();
  } else if (diferencia < -50) {
    irAnterior();
  }

  touchInicio.current = null;
};

  const irAnterior = () => {
    setActual((prev) => (prev - 1 + imagenes.length) % imagenes.length);
  };

  const irSiguiente = () => {
    setActual((prev) => (prev + 1) % imagenes.length);
  };

  return (
    <section className="nosotros">

      {/* COLUMNA IZQUIERDA: texto */}
      <div className="nosotros__texto">
        
        <h2 className="nosotros__titulo">Sobre Nosotros</h2>
        <p className="nosotros__parrafo">
          En Eluney, unimos lo mejor de dos mundos: la delicadeza de la repostería artesanal y la potencia de la sandwichería tradicional.
          Desde el aroma de nuestras galletas de avena recién horneadas
          hasta el primer mordisco de un churrasco caliente, trabajamos para ofrecerte sabor real y calidad en cada pedido.
        </p>

        {/* Lista de características */}
        <ul className="nosotros__lista">
          {caracteristicas.map((item, index) => (
            <li key={index} className="nosotros__item">
              <span className="nosotros__icono">{item.icono}</span>
              <div>
                <h3 className="nosotros__item-titulo">{item.titulo}</h3>
                <p className="nosotros__item-desc">{item.descripcion}</p>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {/* COLUMNA DERECHA: slider de imágenes */}
      <div className="nosotros__slider">
        <img
          src={imagenes[actual]}
          alt={`foto ${actual + 1}`}
          className="nosotros__imagen"
           onTouchStart={handleTouchStart}
          onTouchEnd={handleTouchEnd}
        />
       

        {/* Botones */}
        <button className="nosotros__btn nosotros__btn--izq" onClick={irAnterior}>
          &#8592;
        </button>
        <button className="nosotros__btn nosotros__btn--der" onClick={irSiguiente}>
          &#8594;
        </button>

        {/* Puntos */}
        <div className="nosotros__puntos">
          {imagenes.map((_, index) => (
            <button
              key={index}
              onClick={() => setActual(index)}
              className={`nosotros__punto ${index === actual ? "nosotros__punto--activo" : ""}`}
            />
          ))}
        </div>
      </div>

    </section>
  );
}