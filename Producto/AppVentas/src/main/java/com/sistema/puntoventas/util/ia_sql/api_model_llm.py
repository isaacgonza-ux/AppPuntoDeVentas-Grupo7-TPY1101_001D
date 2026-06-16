from google import genai
from google.genai import types



def cargarModelo(api_key):
    try:
        client = genai.Client(api_key=api_key)
        return client
    except Exception as e:
        print(f"Error al cargar el modelo: {e}")
        return None

def ejecutarModelo(cliente, prompt, reglas_sistema, esquema_db):
    
    response = cliente.models._generate_content(
        model = "gemini-3.5-flash",
        contents=[esquema_db, prompt],
        config=types.GenerateContentConfig(
        system_instruction=reglas_sistema,
        temperature=0.0 
    )
    )
    return response


