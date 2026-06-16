
from prophet.serialize import model_to_json, model_from_json
import pandas as pd
from prophet import Prophet
import logging
from prophet.serialize import model_to_json, model_from_json

logging.getLogger("prophet").setLevel(logging.WARNING)


def entrenarModelo():
    with open('serialized_model.json', 'w') as fout:
        df = pd.read_csv("datos_de_entrenamiento.csv")
        m = Prophet(weekly_seasonality=True, yearly_seasonality=False, daily_seasonality=False)
        m.fit(df)
        fout.write(model_to_json(m))  # guardar modelo



def reentrenarModelo(semanas_de_historial=52): #entrena el modelo con un rango de 52 - 3 semanas
    #al ejecutarse elimina informacion mayor a 52 semanas para mantener datos actualizados
    try:

        df = pd.read_csv("datos_de_entrenamiento.csv")
        
        # Asegurarse que la columna de fecha esté bien tipada
        df['ds'] = pd.to_datetime(df['ds'])
        
        # Calcular fecha de corte 
        fecha_corte = df['ds'].max() - pd.DateOffset(weeks=semanas_de_historial)
        df_ventana = df[df['ds'] >= fecha_corte]

        if len(df_ventana) < 14: # Ejemplo: mínimo 14 registros (días)
            print("Error: No hay suficientes datos para un entrenamiento fiable.")
            return False
        
        # Entrenar solo con el historial relevante
        m = Prophet(
            weekly_seasonality=True,
            yearly_seasonality=False,
            daily_seasonality=False
        )
        m.fit(df_ventana)
        
        
        with open('serialized_model.json', 'w') as fout:
            fout.write(model_to_json(m))

        return True
    except Exception as e:
        print(f"ERROR: {e}")
        return False


