import {pasos} from '../data/Personalizado';
import {pasteleria} from '../data/Personalizado';
import '../css/Personalizado.css';
import { Palette, ChefHat, PackageCheck } from "lucide-react";

const iconos = {
  Palette: Palette,
  ChefHat: ChefHat,
  PackageCheck: PackageCheck,
};


export default function Personalizado() {
    return(
    <section className="pasteleria">

      <div className="pasteleria__intro">
        <div className='pasteleria__header'>
          <span className='pasteleria__etiqueta'></span>
          <h2 className="pasteleria__titulo">Pastelería Personalizada</h2>
          <p className="pasteleria__subtitulo">
            Creamos el dulce perfecto para tu momento especial.
            Pedidos personalizados por encargo.
          </p>
        </div>

        {/* PASOS */}
        <div className="pasteleria__pasos">
          {pasos.map((paso) => {
            const IconoComponente = iconos[paso.icono];

            return (
            <div key={paso.numero} className="pasteleria__paso">
              <div className="pasteleria__paso-icono">
                {IconoComponente ? <IconoComponente size={28} /> : null}
              </div>
              <h3 className="pasteleria__paso-titulo">{paso.titulo}</h3>
            </div>
            );
          })}
        </div>
      </div>

      
      
      
               
                
              
           
         
     
  

    </section>
  );
}
          