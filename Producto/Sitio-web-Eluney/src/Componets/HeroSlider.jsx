import { useState, useEffect } from "react";
import "../css/HeroSlider.css";


const slides = [
  {
    
    id: 1,
    image: "img/IMG_7113.JPG",
    title: "Eluney Cafeteria",
    subtitle: "Bienvenidos",
    
  },
  {
    id: 2,
    image: "img/portada-3.webp",
    title: "Tu comida favorita",
    subtitle: "Desde completos hasta pastelería.",
  },
  {
    id: 3,
    image: "img/pasteleria.webp",
    title: "Postres únicos",
    subtitle: "El dulce final que mereces cada día.",
  },
];


const INTERVALO = 4000;

export default function HeroSlider() {
 
  const [actual, setActual] = useState(0);


  useEffect(() => {
    const timer = setInterval(() => {
      setActual((prev) => (prev + 1) % slides.length);
                       
    }, INTERVALO);

    // Limpiamos el timer cuando el componente se desmonta
    return () => clearInterval(timer);
  }, []);

   //Funciones para los botones de navegación manual
  const irAnterior = () => {
    setActual((prev) => (prev - 1 + slides.length) % slides.length);
  };

  const irSiguiente = () => {
    setActual((prev) => (prev + 1) % slides.length);
  };

  return (
    <div className="slider">

      {/* IMAGEN DE FONDO */}
      <img
        src={slides[actual].image}
        alt={slides[actual].title}
        className="slider__imagen"
      />

      <div className="slider__overlay" />

    
      <div className="slider__contenido">
        <h1 className="slider__titulo">{slides[actual].title}</h1>
        <p className="slider__subtitulo">{slides[actual].subtitle}</p>
        <button className="slider_cta" onClick={() => window.location.href = '#carta'}>Ver Carta</button>
      </div>

      {/* BOTONES izquierda / derecha */}
      <button className="slider__btn slider__btn--izq" onClick={irAnterior}>
        &#8592;
      </button>
      <button className="slider__btn slider__btn--der" onClick={irSiguiente}>
        &#8594;
      </button>

      {/* PUNTOS indicadores abajo */}
      <div className="slider__puntos">
        {slides.map((_, index) => (
          <button
            key={index}
            onClick={() => setActual(index)}
            className={`slider__punto ${index === actual ? "slider__punto--activo" : ""}`}
          />
        ))}
      </div>

    </div>
  );
}