

import os
import csv
import random
from hypothesis import strategies as st

OUTPUT_PATH = "src/hypothesis-tests/csv"
os.makedirs(OUTPUT_PATH, exist_ok=True)

# Estratégias Base
# No hypothesis-gen.py, muda a strategy:
safe_text = st.text(
    alphabet=st.characters(whitelist_categories=('Lu', 'Ll', 'Nd')), 
    min_size=3, 
    max_size=20
)
gen_email = st.emails()
gen_duration = st.integers(min_value=30, max_value=600)

def generate_full_data():
    # 1. Gerar Utilizadores
    users = []
    with open(f"{OUTPUT_PATH}/test_users.csv", 'w', newline='') as f:
        writer = csv.writer(f, delimiter='|')
        for _ in range(100):
            u_name = safe_text.example()
            u_email = gen_email.example()
            # Plano aleatório para testar SubscriptionPlanAdapter
            u_plan = random.choice(["Free", "PremiumBase", "PremiumTop"])
            users.append(u_name)
            writer.writerow([u_name, u_email, u_plan])

    # 2. Gerar Álbuns e Músicas (Relacionados)
    all_music_names = []
    with open(f"{OUTPUT_PATH}/test_catalog.csv", 'w', newline='') as f:
        writer = csv.writer(f, delimiter='|')
        for _ in range(30): # 30 Álbuns
            album_name = safe_text.example()
            artist_name = safe_text.example()
            
            num_songs = random.randint(5, 12)
            for _ in range(num_songs):
                song_name = safe_text.example()
                duration = gen_duration.example()
                # Tipo de música: 0-Normal, 1-Explicit, 2-Multimedia
                s_type = random.choice(["Normal", "Explicit", "Multimedia"])
                url = "http://spotifum.com/" + safe_text.example() if s_type == "Multimedia" else ""
                
                all_music_names.append(song_name)
                writer.writerow([song_name, artist_name, album_name, duration, s_type, url])

    # 3. Gerar Playlists (usando utilizadores e músicas existentes)
    with open(f"{OUTPUT_PATH}/test_playlists.csv", 'w', newline='') as f:
        writer = csv.writer(f, delimiter='|')
        for _ in range(50):
            p_name = safe_text.example()
            p_creator = random.choice(users)
            # Selecionar entre 5 a 15 músicas da lista global que gerámos acima
            p_musics = ";".join(random.sample(all_music_names, k=min(len(all_music_names), random.randint(5, 15))))
            writer.writerow([p_name, p_creator, p_musics])

# Adicionar no hypothesis-gen.py

def generate_edge_cases():
    # 4. Gerar duplicados propositados para testar AlreadyExistsException
    with open(f"{OUTPUT_PATH}/test_duplicates.csv", 'w', newline='') as f:
        writer = csv.writer(f, delimiter='|')
        name = safe_text.example()
        # Escrevemos o mesmo nome duas vezes
        writer.writerow([name, "email1@test.com"])
        writer.writerow([name, "email2@test.com"])

    # 5. Gerar dados vazios ou strings gigantes para testar limites
    with open(f"{OUTPUT_PATH}/test_stress.csv", 'w', newline='') as f:
        writer = csv.writer(f, delimiter='|')
        writer.writerow(["A" * 1000, "email@long.com", "Free"]) # Nome gigante
        writer.writerow([" ", "invalid@email", "PremiumTop"])    # Nome vazio/espaço

if __name__ == "__main__":
    generate_full_data()
    generate_edge_cases()
    print(f"✅ Dados complexos Hypothesis gerados em: {OUTPUT_PATH}")