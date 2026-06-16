import pandas as pd
from prophet import Prophet
from prophet.serialize import model_to_json, model_from_json
import os
import sys
import logging

logging.getLogger("prophet").setLevel(logging.WARNING)

def predecir_agotamiento():
    try:
        # 1. Leer los nuevos datos
        df = pd.read_csv("datos_stock.csv")

        # 2. Obtener lista de productos únicos
        productos = df['id_producto'].unique()

        carpeta_modelos = "modelos_ia"
        os.makedirs(carpeta_modelos, exist_ok=True)

        # 3. Iterar y predecir por cada producto
        for prod_id in productos:
            df_prod = df[df['id_producto'] == prod_id][['ds', 'y']]  # Filtramos solo los datos de este producto en específico

            # Prophet necesita mínimo 2-3 datos históricos para no dar error
            if len(df_prod) < 3:
                continue

            nombre_archivo_modelo = os.path.join(carpeta_modelos, f"modelo_producto_{prod_id}.json")

            if os.path.exists(nombre_archivo_modelo):
                with open(nombre_archivo_modelo,'r') as fin:
                    modelo = model_from_json(fin.read())
            else:

                modelo = Prophet(weekly_seasonality=True, yearly_seasonality=False, daily_seasonality=False)
                modelo.fit(df_prod)
                with open(nombre_archivo_modelo, 'w') as fout:
                    fout.write(model_to_json(modelo))

            # Predecir los próximos 7 días de este producto
            futuro = modelo.make_future_dataframe(periods=7)
            prediccion = modelo.predict(futuro)
            dias_futuros = prediccion.tail(7)

            # Calcular la demanda promedio diaria (evitando números negativos)
            demanda_diaria = sum(dias_futuros['yhat'].apply(lambda x: max(0, x))) / 7

            # Imprimir al formato: id_producto, demanda_diaria_promedio
            # Ejemplo de salida: 12, 4.5
            print(f"{prod_id},{round(demanda_diaria, 2)}")

    except Exception as e:
        print(f"ERROR: {e}")
        sys.exit(1)

if __name__ == '__main__':
    predecir_agotamiento()