import pandas as pd
from prophet import Prophet
import sys
import logging
import os


logging.getLogger("prophet").setLevel(logging.WARNING)

def generar_prediccion():
    try:
        df1 = pd.read_csv("datos_ventas.csv")
        file_path = "datos_ventas.csv"
        
        if not os.path.exists(file_path):
            print(f"ERROR: Archivo {file_path} no encontrado.")
            sys.exit(1)

        df1 = pd.read_csv(file_path)
        
        if len(df1) < 2:
            print("ERROR: Insuficientes datos para predecir (mínimo 2 registros).")
            sys.exit(1)

        modelo = Prophet(weekly_seasonality=True, yearly_seasonality=False, daily_seasonality=False)
        modelo.fit(df1)
        futuro = modelo.make_future_dataframe(periods=7)#le decimos que vamos a predecir los próximos 7 días
        prediccion = modelo.predict(futuro)
        dias_futuros = prediccion.tail(7) #tomamos solo los 7 nuevos días, ignoramos los anteriores

        for index, row in dias_futuros.iterrows():
            fecha = row['ds'].strftime('%Y-%m-%d')
            valor_estimado = round(row['yhat'])

            if valor_estimado < 0:
                valor_estimado = 0

                # Formato de salida: 2026-04-26,45000
            print(f"{fecha},{valor_estimado}")

    except Exception as e:
        # Si algo falla, se lo enviamos a Java
        print(f"ERROR: {e}")
        sys.exit(1)

if __name__ == '__main__':
    generar_prediccion()
