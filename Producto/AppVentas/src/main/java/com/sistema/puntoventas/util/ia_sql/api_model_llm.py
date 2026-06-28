from google import genai
from google.genai import types
from llama_cpp import Llama


def cargarModelo(api_key):
    try:
        client = genai.Client(api_key=api_key)
        return client
    except Exception as e:
        print(f"Error al cargar el modelo: {e}")
        return None

def ejecutarModelo(cliente, prompt, reglas_sistema, esquema_db):
    
    response = cliente.models.generate_content(
        model = "gemini-3.5-flash",
        contents=[esquema_db, prompt],
        config=types.GenerateContentConfig(
        system_instruction=reglas_sistema,
        temperature=0.0 
    )
    )
    return response


def cargarModeloLocal(model_path):
    try:
        llm = Llama(model_path=model_path, n_ctx=4096, verbose=False)
        return llm
    except Exception as e:
        print(f"Error al cargar modelo local: {e}")
        return None

def ejecutarModeloLocal(cliente, prompt, reglas_sistema, esquema_texto):
    messages = [
        {"role": "system", "content": reglas_sistema},
        {"role": "user", "content": f"Esquema de base de datos:\n\n{esquema_texto}\n\nPregunta del usuario: {prompt}"}
    ]
    r = cliente.create_chat_completion(
        messages=messages,
        temperature=0.0,
        max_tokens=1024
    )
    return r["choices"][0]["message"]["content"]


