import { useState } from "react";
import "../css/Navbar.css";

const WhatsAppIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
    <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347z" />
    <path d="M12 0C5.373 0 0 5.373 0 12c0 2.123.554 4.117 1.528 5.845L.057 23.571a.5.5 0 0 0 .612.612l5.726-1.471A11.945 11.945 0 0 0 12 24c6.627 0 12-5.373 12-12S18.627 0 12 0zm0 21.818a9.807 9.807 0 0 1-5.031-1.386l-.36-.214-3.732.958.975-3.607-.235-.372A9.808 9.808 0 0 1 2.182 12C2.182 6.57 6.57 2.182 12 2.182S21.818 6.57 21.818 12 17.43 21.818 12 21.818z" />
  </svg>
);

export default function Navbar() {
  const [active, setActive] = useState("Inicio");
  const [menuAbierto, setMenuAbierto] = useState(false);

  const links = [
    { label: "Inicio", sectionId: "hero-section" },
    { label: "Carta", sectionId: "carta" },
    { label: "Nosotros", sectionId: "nosotros" },
    { label: "Servicios", sectionId: "servicios" },
  ];

  const handleNavClick = (label, sectionId) => {
    setActive(label);
    setMenuAbierto(false);

    const section = document.getElementById(sectionId);
    if (!section) return;

    const navbar = document.querySelector(".navbar");
    const navbarHeight = navbar ? navbar.offsetHeight : 0;
    const sectionTop = section.getBoundingClientRect().top + window.scrollY;

    window.scrollTo({
      top: sectionTop - navbarHeight,
      behavior: "smooth",
    });
  };

  return (
    <nav className="navbar">

      {/* LOGO */}
      <div className="navbar__logo">Eluney</div>

      {/* LINKS */}
      <ul className={`navbar__links ${menuAbierto ? "navbar__links--abierto" : ""}`}>
        {links.map((link) => (
          <li key={link.label}>
            <button
              onClick={() => handleNavClick(link.label, link.sectionId)}
              className={`navbar__link ${active === link.label ? "navbar__link--active" : ""}`}
            >
              {link.label}
              {active === link.label && <span className="navbar__underline" />}
            </button>
          </li>
        ))}

        <li className="navbar__whatsapp-movil-item">
          <a
            href="https://wa.me/tunumero"
            target="_blank"
            rel="noopener noreferrer"
            className="navbar__whatsapp navbar__whatsapp-movil"
            onClick={() => setMenuAbierto(false)}
          >
            <WhatsAppIcon />
            <span>WhatsApp</span>
          </a>
        </li>
      </ul>

      {/* BOTONES derecha — escritorio */}
      <div className="navbar__derecha">
        <a
          href="https://wa.me/tunumero"
          target="_blank"
          rel="noopener noreferrer"
          className="navbar__whatsapp"
        >
          <WhatsAppIcon />
          <span>WhatsApp</span>
        </a>

        <button
          className="navbar__hamburguesa"
          onClick={() => setMenuAbierto((estadoActual) => !estadoActual)}
          aria-label="Abrir menú"
          aria-expanded={menuAbierto}
          type="button"
        >
          {menuAbierto ? "✕" : "☰"}
        </button>
      </div>

    </nav>
  );
}