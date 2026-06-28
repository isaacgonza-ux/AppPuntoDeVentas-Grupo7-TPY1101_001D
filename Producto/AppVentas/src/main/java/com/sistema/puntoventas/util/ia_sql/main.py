import json
import api_model_llm
import os
from google.genai import types
import time


with open("input.json", "r", encoding="utf-8") as archivo:
    datos = json.load(archivo)

mode = datos.get("mode", "gemini")
prompt = datos.get("prompt")

# ===== MODO LOCAL (llama.cpp) =====
if mode == "local":
    model_path = datos.get("model_path", "models/qwen2.5-3b-instruct-q4_k_m.gguf")

    if not os.path.exists(model_path):
        from huggingface_hub import hf_hub_download
        model_dir = os.path.dirname(model_path)
        if model_dir:
            os.makedirs(model_dir, exist_ok=True)
        model_path = hf_hub_download(
            repo_id="Qwen/Qwen2.5-3B-Instruct-GGUF",
            filename="qwen2.5-3b-instruct-q4_k_m.gguf"
        )

    with open("db_extructure.md", "r", encoding="utf-8") as f:
        esquema = f.read()

    with open("instrucciones.md", "r", encoding="utf-8") as f:
        reglas = f.read()

    cliente = api_model_llm.cargarModeloLocal(model_path)
    if cliente is None:
        print("error")
        exit(1)

    response = api_model_llm.ejecutarModeloLocal(cliente, prompt, reglas, esquema)

# ===== MODO GEMINI (existente, intacto) =====
else:
    api_key = datos.get("api_key")

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

# ===== RESPUESTA (común) =====
if response is not None:
    resp_text = response.text if hasattr(response, 'text') else str(response)
    with open("respuesta.json", "w", encoding="utf-8") as f:
        json.dump({"respuesta": resp_text.strip()}, f, ensure_ascii=False)
    print("IA llm: ok")
else:
    with open("respuesta.json", "w", encoding="utf-8") as f:
        json.dump({"respuesta": "La IA no genero respuesta"}, f, ensure_ascii=False)
    print("IA llm: error")