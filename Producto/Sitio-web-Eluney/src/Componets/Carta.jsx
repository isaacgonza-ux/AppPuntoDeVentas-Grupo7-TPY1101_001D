
import '../css/Menu.css';

function parseMenuData(rows) {
  if (!rows || rows.length === 0) return [];

  const headers = rows[0];
  const categories = headers.map((name) => ({ name, items: [] }));

  for (let i = 1; i < rows.length; i++) {
    const row = rows[i];
    row.forEach((cell, colIndex) => {
      if (!cell || !cell.trim()) return;
      const parts = cell.trim().rsplit ? cell.trim().split(/\s+(?=\d+$)/) : splitNamePrice(cell.trim());
      if (parts) {
        categories[colIndex].items.push({ name: parts.name, price: parts.price });
      }
    });
  }

  return categories.filter((cat) => cat.items.length > 0);
}

function splitNamePrice(cell) {
  const match = cell.match(/^(.+?)\s+(\d+)$/);
  if (!match) return null;
  return { name: match[1], price: parseInt(match[2], 10) };
}

export default function Menu({ rows }) {
  const categories = [];

  if (rows && rows.length > 0) {
    const headers = rows[0];
    headers.forEach((h, i) => categories.push({ name: h, items: [] }));

    for (let i = 1; i < rows.length; i++) {
      rows[i].forEach((cell, colIndex) => {
        if (!cell || !cell.trim()) return;
        const parsed = splitNamePrice(cell.trim());
        if (parsed) categories[colIndex].items.push(parsed);
      });
    }
  }

  const filtered = categories.filter((c) => c.items.length > 0);

  return (
    <div className="carta-container">
      <div className="menu">
        <h1 className="menu__title">Menú</h1>
        <div className="menu__grid">
          {filtered.map((cat) => (
            <div key={cat.name} className="menu__category">
              <h2 className="menu__category-name">{cat.name}</h2>
              <ul className="menu__list">
                {cat.items.map((item) => (
                  <li key={item.name} className="menu__item">
                    <span className="menu__item-name">{item.name}</span>
                    <span className="menu__item-dots" />
                    <span className="menu__item-price">${item.price.toLocaleString('es-CL')}</span>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}