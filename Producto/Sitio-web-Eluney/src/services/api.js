
export async function leerDatosyRetornar() {
  const SHEET_ID = '1XdsYMpG5W-u3o0QqlmTb9ZpgJkl_Gfw5Gr8-Flk4KXY';
  
  // URL de sheet (debe ser publica)
  const url = `https://docs.google.com/spreadsheets/d/${SHEET_ID}/gviz/tq?tqx=out:json&sheet=Hoja%201`;
  
  const response = await fetch(url);
  const text = await response.text();
  
  // Google devuelve JSONP, hay que limpiar el wrapper
  const json = JSON.parse(text.match(/google\.visualization\.Query\.setResponse\(([\s\S]*?)\);/)[1]);
  
  // Convertir el formato gviz a array de arrays 
  const rows = json.table.rows.map(row =>
    row.c.map(cell => cell ? cell.v : null)
  );
  
  return rows;
}

