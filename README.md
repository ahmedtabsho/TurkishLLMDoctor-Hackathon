O zaman README’yi canlı uygulama linkinle güncelledim ve biraz daha şık bir hale getirdim:

---

````markdown
# 🩺 Türkçe Medikal Yapay Zekâ Asistanı

Türkçe sağlık verilerinde **büyük dil modellerinin (LLM)** performansını artırmak amacıyla geliştirilmiş, **MEDITRON-70B** tabanlı bir yapay zekâ asistanı.  
React.js tabanlı **frontend** ve **backend** altyapısıyla, hem kullanıcı dostu hem de güçlü bir medikal danışmanlık platformu.

🌐 **Canlı Demo:** [nukleotitai.vercel.app](https://nukleotitai.vercel.app)

![Demo Ekran Görüntüsü](docs/demo.png) <!-- Buraya projenin ekran görüntüsünü ekleyebilirsin -->

---

## 🚀 Özellikler

- **Türkçe Medikal Veri Seti** – Dünya çapındaki en büyük medikal veri setlerinin Türkçe çevirisi ve genişletilmesi.
- **MEDITRON-70B Entegrasyonu** – Sağlık alanında başarısı kanıtlanmış LLM modeli.
- **Finetune ile Türkçe Medikal Bilgi Geliştirme** – Türkçe medikal terminoloji ve bağlam optimizasyonu.
- **RAG (Retrieval-Augmented Generation)** – Google Scholar’dan en güncel tıbbi makaleleri sorgulama ve yanıtlara ekleme.
- **Modern UI/UX** – React.js ile tasarlanmış kullanıcı dostu arayüz.
- **Backend API** – Node.js + Express tabanlı güçlü servis yapısı.

---

## 📂 Veri Seti

Türkçe veri setimiz Hugging Face üzerinde açık erişimli:  
🔗 [Medical QA Dataset TR](https://huggingface.co/datasets/ahmadtab/medical_qa_dataset_tr)

---

## 🛠️ Teknolojiler

**Frontend:**
- React.js
- Tailwind CSS / Material UI
- Axios (API istekleri için)

**Backend:**
- Node.js
- Express.js
- OpenAI API / HuggingFace Transformers
- RAG entegrasyonu (Google Scholar scraping & API)

**Model:**
- [MEDITRON-70B](https://huggingface.co/epfl-llm/meditron-70b)
- Türkçe medikal veri seti ile finetune edildi.

---

## 📸 Ekran Görüntüleri

| Soru-Cevap Ekranı | Makale Listesi |
|-------------------|----------------|
| ![QA](docs/qa.png) | ![Makale](docs/articles.png) |

---

## ⚙️ Kurulum

### 1️⃣ Depoyu klonlayın
```bash
git clone https://github.com/kullanici_adi/meditron-tr.git
cd meditron-tr
````

### 2️⃣ Backend kurulumu

```bash
cd backend
npm install
npm start
```

### 3️⃣ Frontend kurulumu

```bash
cd frontend
npm install
npm run dev
```

---

## 📌 Kullanım

1. **[Canlı Uygulamaya Git](https://nukleotitai.vercel.app)**
2. Sağlıkla ilgili sorularınızı yazın.
3. Model size **Türkçe** ve **tıbbi bağlama uygun** yanıtlar versin.
4. Yanıtın yanında **Google Scholar’dan önerilen makaleler** listelensin.
5. Makaleye tıklayarak doğrudan kaynağa gidin.

---

## 🧠 Mimarî

```plaintext
React.js (Frontend)
       |
       v
Node.js + Express (Backend API)
       |
       v
MEDITRON-70B (LLM) + Türkçe Finetune
       |
       +--> RAG (Google Scholar Makale Tarayıcı)
```

---

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) ile lisanslanmıştır.

---

## 🤝 Katkı

1. Fork’layın 🍴
2. Yeni bir dal oluşturun (`git checkout -b feature-xyz`)
3. Değişikliklerinizi commit edin (`git commit -m 'Yeni özellik ekle'`)
4. Dalınızı push’layın (`git push origin feature-xyz`)
5. Pull Request gönderin 🚀

---

💡 **Not:** Bu proje **bilgi amaçlıdır** ve kesin tıbbi tanı/tedavi önerisi yerine geçmez.
Tıbbi konularda mutlaka alanında uzman bir doktora danışınız.

