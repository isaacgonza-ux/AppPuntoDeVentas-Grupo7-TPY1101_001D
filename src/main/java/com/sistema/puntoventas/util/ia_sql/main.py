import json
import api_model_llm
from google.genai import types


with open("input.json", "r", encoding="utf-8") as archivo:
    datos = json.load(archivo)

api_key = datos.get("api_key")
prompt = datos.get("prompt")

if not api_key or not prompt:
    print("error")
    exit(1)

cliente = api_model_llm.cargarModelo(api_key)

if cliente is None:
    print("error")
    exit(1)

esquema_db = cliente.files.upload(
    file="db_extructure.md", 
    config=types.UploadFileConfig(
        mime_type="text/markdown" 
    )
)

with open("instrucciones.md", "r", encoding="utf-8") as archivo_reglas:
    reglas_sistema = archivo_reglas.read()

response = api_model_llm.ejecutarModelo(cliente, prompt, reglas_sistema, esquema_db)

if response is not None:
    with open("respuesta.json", "w", encoding="utf-8") as f:
        json.dump({"respuesta": response.text.strip()}, f, ensure_ascii=False)
    print("IA llm: ok")
else:
    with open("respuesta.json", "w", encoding="utf-8") as f:
        json.dump({"respuesta": "La IA no genero respuesta"}, f, ensure_ascii=False)
    print("IA llm: error")