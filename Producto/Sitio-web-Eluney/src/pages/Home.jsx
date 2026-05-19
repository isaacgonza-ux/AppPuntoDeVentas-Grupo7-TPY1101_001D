import HeroSlider from "../Componets/HeroSlider";
import Nosotros from "../Componets/Nosotros";
import Personalizado from "../Componets/Personalizado";
import Recomendados from "../Componets/Recomendados";
import Ubicacion from "../Componets/Ubicacion";
import Menu from "../Componets/Carta";


export const Home = ({rows}) => {
    return (
        <div className="home-page">
            <section id="hero-section">
                <HeroSlider />
            </section>

            <section id="recomendados">
                <Recomendados />
            </section>

            <section id="carta">
                <Menu rows={rows} />
            </section>

             <section id="servicios">
                <Personalizado />
            </section>

            <section id="nosotros">
                <Nosotros />
            </section>

            <section id="ubicacion">
                <Ubicacion />
            </section>
            
        </div>
    )
}