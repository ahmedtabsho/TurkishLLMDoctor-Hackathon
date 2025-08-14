package com.backend.backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.backend.model.ChatMessage;
import com.backend.backend.dto.DocumentResponse;
import com.backend.backend.dto.GraphicsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;

@Service
public class OpenAIService {

    private OpenAiService openAiService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String MODEL = "gpt-4o";
    
    // Zaman aşımı süresi (saniye)
    private static final int READ_TIMEOUT = 120;      // 120 saniye
    
    @Value("${openai.api.key}")
    private String apiKey;
    
    @Autowired
    public OpenAIService() {
        // Constructor boş bırakılıyor, @PostConstruct ile initialization yapılacak
    }
    
    @PostConstruct
    public void initialize() {
        // OpenAI API servisini application.properties'den alınan API anahtarı ve uzun zaman aşımı süresiyle oluştur
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(READ_TIMEOUT));
    }
    
    public ChatMessage generateResponse(String userMessage) {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        // Hangi model olduğuna dair soruları kontrol et
        String lowerCaseMessage = userMessage.toLowerCase();
        if (lowerCaseMessage.contains("hangi model") || 
            lowerCaseMessage.contains("ne modeli") || 
            lowerCaseMessage.contains("kimsin") || 
            lowerCaseMessage.contains("adın ne") || 
            lowerCaseMessage.contains("model adı") || 
            lowerCaseMessage.contains("gpt") || 
            lowerCaseMessage.contains("yapay zeka") || 
            lowerCaseMessage.contains("ai model")) {
            
            return new ChatMessage(
                UUID.randomUUID().toString(),
                "Ben Meditron Model, sağlık alanında özel olarak eğitilmiş 70B parametreli Ollama tabanlı bir yapay zeka asistanıyım. " +
                "Tıbbi bilgiler, hastalıklar, tedaviler ve sağlıklı yaşam konularında yardımcı olmak için geniş bir tıbbi veri seti " +
                "üzerinde eğitildim. Size sağlık konularında bilimsel ve güncel bilgiler sunmayı amaçlıyorum. " +
                "Ancak verdiğim bilgiler bir doktor muayenesinin yerini tutmaz ve ciddi sağlık sorunlarında mutlaka bir sağlık kuruluşuna başvurmanızı öneririm.",
                "bot",
                LocalDateTime.now()
            );
        }
        
        // System message with Turkish health-focused instructions
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen Meditron Model adında, sağlık alanında özel olarak eğitilmiş 70B parametreli Ollama tabanlı bir yapay zeka asistanısın. " +
            "Kullanıcıların sağlık sorularına kısa, öz ve anlaşılır bir şekilde cevap ver. " +
            "Türkçe tıbbi terimleri kullan ve gerektiğinde basit açıklamalar ekle. " +
            "Verdiğin bilgilerin güncel tıbbi bilgilere dayandığından emin ol. " +
            "Ciddi sağlık sorunları için mutlaka bir doktora başvurulması gerektiğini belirt. " +
            "Yanıtların kısa, net ve Türkçe olmalı. Bilimsel ve doğru bilgiler ver, ancak karmaşık tıbbi jargondan kaçın. " +
            "Kullanıcının sorusuna göre hastalık belirtileri, tedavi yöntemleri, korunma yolları gibi bilgileri içerebilirsin. " +
            "Eğer bir konuda bilgin yoksa veya emin değilsen, bunu dürüstçe belirt. " +
            "Asla kendini GPT, ChatGPT veya OpenAI modeli olarak tanıtma."
        ));
        
        // User message
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", userMessage));
        
        // Create completion request
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        // Call OpenAI API
        ChatCompletionChoice choice = openAiService.createChatCompletion(completionRequest).getChoices().get(0);
        
        // Create response message
        return new ChatMessage(
            UUID.randomUUID().toString(),
            choice.getMessage().getContent(),
            "bot",
            LocalDateTime.now()
        );
    }
    
    public DocumentResponse generateDocuments(String disease) {
        try {
            // İlk deneme - Google Scholar'dan makale arama
            DocumentResponse response = searchGoogleScholar(disease);
            
            // Eğer Google Scholar'dan yeterli makale bulunamadıysa, detaylı makale aramaya geç
            if (response.getDocuments() == null || response.getDocuments().size() < 5) {
                response = fetchDetailedArticles(disease);
            }
            
            // Eğer hala yeterli makale bulunamadıysa, daha geniş bir arama yap
            if (response.getDocuments() == null || response.getDocuments().size() < 5) {
                response = fetchBroaderArticles(disease);
            }
            
            // Hala yeterli makale bulunamadıysa, alternatif yaklaşım dene
            if (response.getDocuments() == null || response.getDocuments().size() < 5) {
                response = fetchAlternativeArticles(disease);
            }
            
            // Son kontrol - eğer hala makale yoksa varsayılan makaleler oluştur
            if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
                response = createFallbackDocuments(disease);
            }
            
            return response;
            
        } catch (Exception e) {
            // Herhangi bir hata durumunda varsayılan makaleler döndür
            return createFallbackDocuments(disease);
        }
    }

    // Google Scholar'dan makale arama
    private DocumentResponse searchGoogleScholar(String disease) {
        try {
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
            
            // System message with Google Scholar search instructions
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "system",
                "Sen bir akademik araştırma uzmanısın. Verilen hastalık hakkında Google Scholar'da bulunabilecek en güncel ve önemli " +
                "bilimsel makaleleri listelemelisin. Her makale için başlık, yazarlar, yayın yılı, dergi adı, kısa özet ve " +
                "Google Scholar'da bulunabilecek bir link vermelisin. Makaleler mümkünse son 5 yıl içinde yayınlanmış olmalı. " +
                "En az 5 makale bulmalısın. Türkçe karakterlere dikkat et. " +
                "Yanıtını sadece JSON formatında ver: " +
                "{\"documents\": [{\"title\": \"Makale başlığı\", \"description\": \"Yazarlar, Dergi Adı (Yıl). Kısa özet.\", \"link\": \"https://scholar.google.com/...\", \"source\": \"Google Scholar\"}]}"
            ));
            
            // User message with disease query for Google Scholar
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "user", 
                disease + " hastalığı hakkında Google Scholar'da bulunan en güncel ve önemli bilimsel makaleler"
            ));
            
            // Create completion request with appropriate settings
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(MODEL)
                .temperature(0.3) // Daha tutarlı sonuçlar için düşük sıcaklık
                .maxTokens(1000) // Zaman aşımını önlemek için token limitini sınırla
                .build();
            
            try {
                // Call OpenAI API with timeout handling
                ChatCompletionChoice choice = openAiService.createChatCompletion(completionRequest).getChoices().get(0);
                String jsonResponse = choice.getMessage().getContent();
                
                // Clean the response if it contains backticks or other formatting
                jsonResponse = cleanJsonResponse(jsonResponse);
                
                return parseDocumentResponse(jsonResponse, disease);
                
            } catch (Exception apiError) {
                // API hatası durumunda detaylı makale aramaya geç
                return fetchDetailedArticles(disease);
            }
            
        } catch (Exception e) {
            // Genel hata durumunda detaylı makale aramaya geç
            return fetchDetailedArticles(disease);
        }
    }
    
    // Detaylı ve spesifik makaleler için
    private DocumentResponse fetchDetailedArticles(String disease) {
        try {
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
            
            // System message with detailed article retrieval instructions - daha kısa ve öz
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "system",
                "Sen bir tıp literatürü uzmanısın. Verilen hastalık hakkında güncel ve doğru bilgileri içeren makaleleri bulmalısın. " +
                "Eğer tam olarak bu hastalık adıyla makale bulamazsan, benzer hastalıklar veya ilişkili durumlar hakkında makaleler ekle. " +
                "Her makale için başlık, açıklama, link ve kaynak bilgisi vermelisin. " +
                "Türkçe karakterlere dikkat et. Linkler güvenilir sağlık kaynaklarına ait olmalı. " +
                "Yanıtını sadece JSON formatında ver: " +
                "{\"documents\": [{\"title\": \"Makale başlığı\", \"description\": \"Açıklama\", \"link\": \"https://ornek.com/link\", \"source\": \"Kaynak adı\"}]}"
            ));
            
            // User message with disease query
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "user", 
                disease + " hastalığı hakkında güncel bilimsel makaleler ve araştırmalar"
            ));
            
            // Create completion request with reduced token limit to prevent timeouts
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(MODEL)
                .temperature(0.5) // Daha tutarlı sonuçlar için düşük sıcaklık
                .maxTokens(1000) // Zaman aşımını önlemek için token limitini sınırla
                .build();
            
            try {
                // Call OpenAI API with timeout handling
                ChatCompletionChoice choice = openAiService.createChatCompletion(completionRequest).getChoices().get(0);
                String jsonResponse = choice.getMessage().getContent();
                
                // Clean the response if it contains backticks or other formatting
                jsonResponse = cleanJsonResponse(jsonResponse);
                
                return parseDocumentResponse(jsonResponse, disease);
                
            } catch (Exception apiError) {
                // API hatası durumunda daha geniş arama yap
                return DocumentResponse.builder()
                    .success(false)
                    .disease(disease)
                    .documents(new ArrayList<>())
                    .build();
            }
            
        } catch (Exception e) {
            // Genel hata durumunda boş liste döndür
            return DocumentResponse.builder()
                .success(false)
                .disease(disease)
                .documents(new ArrayList<>())
                .build();
        }
    }

    // Daha geniş kapsamlı makaleler için
    private DocumentResponse fetchBroaderArticles(String disease) {
        try {
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
            
            // System message with broader article retrieval instructions - daha kısa ve öz
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "system",
                "Sen bir sağlık bilgilendirme uzmanısın. Verilen hastalık veya sağlık durumu için güvenilir kaynaklar bulmalısın. " +
                "Hastalığın semptomları, teşhis yöntemleri, tedavi seçenekleri, risk faktörleri ve yaşam kalitesini artırma konularını içeren kaynakları dahil et. " +
                "Her kaynak için başlık, açıklama, link ve kaynak bilgisi vermelisin. " +
                "Türkçe karakterlere dikkat et. Yanıtını sadece JSON formatında ver: " +
                "{\"documents\": [{\"title\": \"Kaynak başlığı\", \"description\": \"Açıklama\", \"link\": \"https://ornek.com/link\", \"source\": \"Kaynak adı\"}]}"
            ));
            
            // User message with broader query
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "user", 
                disease + " hastalığı hakkında semptomlar, teşhis, tedavi ve risk faktörleri bilgileri"
            ));
            
            // Create completion request with reduced token limit to prevent timeouts
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(MODEL)
                .temperature(0.7)
                .maxTokens(1000) // Zaman aşımını önlemek için token limitini sınırla
                .build();
            
            try {
                // Call OpenAI API with timeout handling
                ChatCompletionChoice choice = openAiService.createChatCompletion(completionRequest).getChoices().get(0);
                String jsonResponse = choice.getMessage().getContent();
                
                // Clean the response
                jsonResponse = cleanJsonResponse(jsonResponse);
                
                return parseDocumentResponse(jsonResponse, disease);
                
            } catch (Exception apiError) {
                // API hatası durumunda alternatif yaklaşıma geç
                return DocumentResponse.builder()
                    .success(false)
                    .disease(disease)
                    .documents(new ArrayList<>())
                    .build();
            }
            
        } catch (Exception e) {
            // Genel hata durumunda boş liste döndür
            return DocumentResponse.builder()
                .success(false)
                .disease(disease)
                .documents(new ArrayList<>())
                .build();
        }
    }

    // Alternatif yaklaşım - daha genel sağlık kaynakları
    private DocumentResponse fetchAlternativeArticles(String disease) {
        try {
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
            
            // System message with alternative approach - daha kısa ve öz
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "system",
                "Sen bir sağlık bilgilendirme uzmanısın. Verilen hastalık veya sağlık durumu için güvenilir kaynaklar bulmalısın. " +
                "Eğer tam olarak bu hastalık için kaynak bulamazsan, genel sağlık portalları, benzer hastalıklar, semptomlar, " +
                "teşhis yöntemleri, tedavi yaklaşımları, hasta destek grupları ve resmi sağlık kurumlarının rehberleri gibi kaynaklar ekle. " +
                "Her kaynak için başlık, kısa açıklama, link ve kaynak bilgisi vermelisin. " +
                "Türkçe karakterlere dikkat et. Yanıtını sadece JSON formatında ver: " +
                "{\"documents\": [{\"title\": \"Kaynak başlığı\", \"description\": \"Açıklama\", \"link\": \"https://ornek.com/link\", \"source\": \"Kaynak adı\"}]}"
            ));
            
            // User message with alternative query
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "user", 
                disease + " ile ilgili sağlık kaynakları ve bilgi portalları"
            ));
            
            // Create completion request with reduced token count to prevent timeouts
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(MODEL)
                .temperature(0.7)
                .maxTokens(1000) // Daha az token ile zaman aşımını önle
                .build();
            
            try {
                // Call OpenAI API with timeout handling
                ChatCompletionChoice choice = openAiService.createChatCompletion(completionRequest).getChoices().get(0);
                String jsonResponse = choice.getMessage().getContent();
                
                // Clean the response
                jsonResponse = cleanJsonResponse(jsonResponse);
                
                return parseDocumentResponse(jsonResponse, disease);
                
            } catch (Exception apiError) {
                // API hatası durumunda varsayılan makaleler oluştur
                return createFallbackDocuments(disease);
            }
            
        } catch (Exception e) {
            // Genel hata durumunda varsayılan makaleler oluştur
            return createFallbackDocuments(disease);
        }
    }
    
    // Zaman aşımı veya hata durumunda varsayılan makaleler oluştur
    private DocumentResponse createFallbackDocuments(String disease) {
        try {
            List<DocumentResponse.Document> fallbackDocs = new ArrayList<>();
            
            // Varsayılan kaynaklar ekle
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("Sağlık Bakanlığı - Sağlık Bilgi Sistemleri")
                .description("Türkiye Cumhuriyeti Sağlık Bakanlığı'nın resmi sağlık bilgi sistemleri portalı. Çeşitli hastalıklar hakkında güvenilir bilgiler ve sağlık hizmetlerine erişim için kaynaklar içerir.")
                .link("https://www.saglik.gov.tr")
                .source("T.C. Sağlık Bakanlığı")
                .build());
                
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("Türkiye Halk Sağlığı Kurumu")
                .description("Halk sağlığı konusunda bilimsel araştırmalar ve hastalık bilgilendirmeleri sunan resmi kurum. Hastalıklar, korunma yöntemleri ve tedavi yaklaşımları hakkında detaylı bilgiler içerir.")
                .link("https://hsgm.saglik.gov.tr")
                .source("Türkiye Halk Sağlığı Kurumu")
                .build());
                
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("Memorial Sağlık Grubu - Hastalıklar")
                .description("Memorial Sağlık Grubu'nun hastalıklar hakkında bilgilendirme sayfası. Çeşitli hastalıkların belirtileri, teşhis yöntemleri ve tedavi seçenekleri hakkında detaylı bilgiler sunar.")
                .link("https://www.memorial.com.tr/saglik-rehberi")
                .source("Memorial Sağlık Grubu")
                .build());
                
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("Acibadem Sağlık Grubu - Sağlık Rehberi")
                .description("Acibadem Sağlık Grubu'nun sağlık rehberi. Hastalıklar, tedavi yöntemleri, korunma yolları ve sağlıklı yaşam önerileri hakkında güvenilir bilgiler içerir.")
                .link("https://www.acibadem.com.tr/saglik-rehberi")
                .source("Acibadem Sağlık Grubu")
                .build());
                
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("Mayo Clinic - Hastalıklar ve Durumlar")
                .description("Dünyanın önde gelen sağlık kuruluşlarından Mayo Clinic'in hastalıklar ve sağlık durumları hakkında kapsamlı bilgi kaynağı. Belirtiler, nedenler, risk faktörleri ve tedavi seçenekleri hakkında detaylı bilgiler sunar.")
                .link("https://www.mayoclinic.org/diseases-conditions")
                .source("Mayo Clinic")
                .build());
                
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("WebMD - Sağlık A-Z")
                .description("WebMD'nin sağlık ansiklopedisi. Çeşitli hastalıklar, durumlar, semptomlar ve tedaviler hakkında güvenilir bilgiler içerir. Uzman doktorlar tarafından gözden geçirilmiş sağlık içeriği sunar.")
                .link("https://www.webmd.com/a-to-z-guides/common-topics")
                .source("WebMD")
                .build());
                
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title("Hacettepe Üniversitesi Tıp Fakültesi")
                .description("Hacettepe Üniversitesi Tıp Fakültesi'nin sağlık bilgilendirme sayfası. Akademik araştırmalar, hastalıklar ve tedavi yöntemleri hakkında bilimsel bilgiler içerir.")
                .link("https://www.hacettepe.edu.tr/akademik/fakulteler/tip-fakultesi")
                .source("Hacettepe Üniversitesi")
                .build());
            
            // Hastalığa özel bir başlık ekle
            fallbackDocs.add(DocumentResponse.Document.builder()
                .title(disease + " Hakkında Genel Bilgiler")
                .description(disease + " hastalığı hakkında genel bilgiler, belirtiler, risk faktörleri ve tedavi yöntemleri. Bu hastalık hakkında daha detaylı bilgi için bir sağlık kuruluşuna başvurmanız önerilir.")
                .link("https://www.saglik.gov.tr/TR,11472/saglik-bakanligina-bagli-kurum-ve-kuruluslar.html")
                .source("Sağlık Kaynakları")
                .build());
            
            return DocumentResponse.builder()
                .success(true)
                .disease(disease)
                .documents(fallbackDocs)
                .build();
                
        } catch (Exception e) {
            // Son çare - boş liste döndür
            return DocumentResponse.builder()
                .success(true) // Başarılı olarak işaretle ama boş liste döndür
                .disease(disease)
                .documents(new ArrayList<>())
                .build();
        }
    }

    // JSON yanıtını parse etmek için yardımcı metod
    private DocumentResponse parseDocumentResponse(String jsonResponse, String disease) {
        try {
            // Use a Map to parse the JSON first
            Map<String, List<Map<String, String>>> responseMap = objectMapper.readValue(jsonResponse, 
                new TypeReference<Map<String, List<Map<String, String>>>>() {});
            
            List<Map<String, String>> docsMap = responseMap.get("documents");
            List<DocumentResponse.Document> documents = new ArrayList<>();
            
            // Convert each map to a Document object
            if (docsMap != null) {
                for (Map<String, String> docMap : docsMap) {
                    DocumentResponse.Document doc = DocumentResponse.Document.builder()
                        .title(docMap.get("title"))
                        .description(docMap.get("description"))
                        .link(docMap.get("link"))
                        .source(docMap.get("source"))
                        .build();
                    documents.add(doc);
                }
            }
            
            // Create and return the response
            return DocumentResponse.builder()
                .success(true)
                .disease(disease)
                .documents(documents)
                .build();
            
        } catch (JsonProcessingException e) {
            return DocumentResponse.builder()
                .success(false)
                .disease(disease)
                .error("JSON parsing error: " + e.getMessage() + "\nResponse was: " + jsonResponse)
                .build();
        }
    }
    
    // 8. İlaç isimleri ve fiyatları için veri çekme
    private List<GraphicsResponse.DrugPriceInfo> fetchDrugPrices(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık için kullanılan ilaçların isimleri ve fiyatları hakkında gerçekçi veriler üretmelisin. " +
            "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
            "Türkçe karakterlere dikkat et (ç, ş, ı, ğ, ö, ü). " +
            "En az 6 ilaç verisi üret. " +
            "İlaç fiyatları TL cinsinden olmalı ve gerçekçi olmalı (100 TL - 5000 TL arası). " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[{\"drugName\": \"İlaç adı\", \"price\": fiyat}]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı için kullanılan ilaçlar ve fiyatları"));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<GraphicsResponse.DrugPriceInfo>>() {});
    }
    
    // Helper method to clean JSON response
    private String cleanJsonResponse(String jsonResponse) {
        jsonResponse = jsonResponse.trim();
        if (jsonResponse.startsWith("```json")) {
            jsonResponse = jsonResponse.substring(7);
        } else if (jsonResponse.startsWith("```")) {
            jsonResponse = jsonResponse.substring(3);
        }
        
        if (jsonResponse.endsWith("```")) {
            jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
        }
        
        return jsonResponse.trim();
    }
    
    public GraphicsResponse generateGraphicsData(String disease) {
        try {
            GraphicsResponse response = new GraphicsResponse();
            response.setSuccess(true);
            response.setDisease(disease);
            
            // Her grafik için ayrı ayrı API çağrısı yaparak veri toplama
            try {
                // 1. İlaç üreten ülkeler (Bar Chart)
                List<GraphicsResponse.DrugProducingCountry> drugProducingCountries = fetchDrugProducingCountries(disease);
                response.setDrugProducingCountries(drugProducingCountries);
                
                // 2. İlacın bulunduğu ülkeler (Liste)
                List<String> countriesWithDrug = fetchCountriesWithDrug(disease);
                response.setCountriesWithDrug(countriesWithDrug);
                
                // 3. Yıllık üretim (Line Chart)
                List<GraphicsResponse.YearlyProduction> yearlyProduction = fetchYearlyProduction(disease);
                response.setYearlyProduction(yearlyProduction);
                
                // 4. Ülkelere göre hasta sayısı (Heat Map)
                List<GraphicsResponse.PatientsByCountry> patientsByCountry = fetchPatientsByCountry(disease);
                response.setPatientsByCountry(patientsByCountry);
                
                // 5. Bilim insanları (Tablo)
                List<GraphicsResponse.Scientist> scientists = fetchScientists(disease);
                response.setScientists(scientists);
                
                // 6. Risk faktörleri (Pie Chart)
                List<GraphicsResponse.RiskFactor> riskFactors = fetchRiskFactors(disease);
                response.setRiskFactors(riskFactors);
                
                // 7. Yayılma hızı (Area Chart)
                List<GraphicsResponse.SpreadRate> spreadRate = fetchSpreadRate(disease);
                response.setSpreadRate(spreadRate);
                
                // 8. İlaç isimleri ve fiyatları (Bar Chart)
                List<GraphicsResponse.DrugPriceInfo> drugPrices = fetchDrugPrices(disease);
                response.setDrugPrices(drugPrices);
                
                return response;
            } catch (Exception e) {
                response.setSuccess(false);
                response.setError("Veri çekme hatası: " + e.getMessage());
                return response;
            }
            
        } catch (Exception e) {
            GraphicsResponse errorResponse = new GraphicsResponse();
            errorResponse.setSuccess(false);
            errorResponse.setDisease(disease);
            errorResponse.setError("Grafik verileri oluşturma hatası: " + e.getMessage());
            return errorResponse;
        }
    }
    
    // 1. İlaç üreten ülkeler için veri çekme
    private List<GraphicsResponse.DrugProducingCountry> fetchDrugProducingCountries(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık için ilaç üreten ülkeler ve ürettikleri ilaç sayısı hakkında gerçekçi veriler üretmelisin. " +
            "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
            "Türkçe karakterlere dikkat et (ç, ş, ı, ğ, ö, ü). " +
            "En az 5 ülke verisi üret. " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[{\"country\": \"Ülke adı\", \"drugCount\": sayı}]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı için ilaç üreten ülkeler ve ilaç sayıları"));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<GraphicsResponse.DrugProducingCountry>>() {});
    }
    
    // 2. İlacın bulunduğu ülkeler için veri çekme
    private List<String> fetchCountriesWithDrug(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık için ilacın bulunduğu ülkeler hakkında gerçekçi veriler üretmelisin. " +
            "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
            "Türkçe karakterlere dikkat et (ç, ş, ı, ğ, ö, ü). " +
            "En az 8 ülke verisi üret. " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[\"Ülke1\", \"Ülke2\", \"Ülke3\"]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı ilacının bulunduğu ülkeler"));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<String>>() {});
    }
    
    // 3. Yıllık üretim için veri çekme
    private List<GraphicsResponse.YearlyProduction> fetchYearlyProduction(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık için yıllık ilaç üretim miktarları hakkında gerçekçi veriler üretmelisin. " +
            "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
            "Son 5 yıl için veri üret. " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[{\"year\": \"Yıl\", \"production\": sayı}]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı için yıllık ilaç üretim miktarları"));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<GraphicsResponse.YearlyProduction>>() {});
    }
    
    // 4. Ülkelere göre hasta sayısı için veri çekme
    private List<GraphicsResponse.PatientsByCountry> fetchPatientsByCountry(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık için ülkelere göre hasta sayıları hakkında gerçekçi veriler üretmelisin. " +
            "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
            "Türkçe karakterlere dikkat et (ç, ş, ı, ğ, ö, ü). " +
            "En az 5 ülke verisi üret. " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[{\"country\": \"Ülke adı\", \"patientCount\": sayı}]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı için ülkelere göre hasta sayıları"));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<GraphicsResponse.PatientsByCountry>>() {});
    }
    
    // 5. Bilim insanları için veri çekme
    private List<GraphicsResponse.Scientist> fetchScientists(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık alanında çalışan bilim insanları ve iletişim bilgileri hakkında gerçekçi veriler üretmelisin. " +
            "Her seferinde farklı ve çeşitli bilim insanları üret, tekrar eden isimler kullanma. " +
            "Farklı ülkelerden ve kurumlardan bilim insanları seç. " +
            "E-posta adresleri gerçekçi olmalı ve kurum adreslerini içermeli (ornek.bilimci@universitesi.edu.tr gibi). " +
            "Telefon numaraları uluslararası formatta olmalı (+90 555 123 4567 gibi). " +
            "Türkçe karakterlere dikkat et (ç, ş, ı, ğ, ö, ü). " +
            "Tam olarak 5 bilim insanı verisi üret. " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[{\"name\": \"İsim\", \"institution\": \"Kurum\", \"email\": \"eposta\", \"phone\": \"telefon\", \"country\": \"ülke\"}]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı alanında çalışan bilim insanları ve iletişim bilgileri. Lütfen her seferinde farklı ve çeşitli bilim insanları üret."));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.9) // Daha yüksek sıcaklık değeri ile daha çeşitli sonuçlar
            .maxTokens(800)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<GraphicsResponse.Scientist>>() {});
    }
    
    // 6. Risk faktörleri için veri çekme
    private List<GraphicsResponse.RiskFactor> fetchRiskFactors(String disease) throws Exception {
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
            "system",
            "Sen bir sağlık verileri uzmanısın. Verilen hastalık için risk faktörleri ve yüzdeleri hakkında gerçekçi veriler üretmelisin. " +
            "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
            "Türkçe karakterlere dikkat et (ç, ş, ı, ğ, ö, ü). " +
            "En az 5 risk faktörü verisi üret. Yüzdelerin toplamı 100 olmalı. " +
            "Yanıtını aşağıdaki formatta ver: " +
            "[{\"factor\": \"Risk faktörü\", \"percentage\": yüzde}]"
        ));
        
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı için risk faktörleri ve yüzdeleri"));
        
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .temperature(0.7)
            .maxTokens(500)
            .build();
        
        ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
        String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
        
        return objectMapper.readValue(jsonResponse, new TypeReference<List<GraphicsResponse.RiskFactor>>() {});
    }
    
    // 7. Yayılma hızı için veri çekme
    private List<GraphicsResponse.SpreadRate> fetchSpreadRate(String disease) throws Exception {
        try {
            List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
            
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                "system",
                "Sen bir sağlık verileri uzmanısın. Verilen hastalık için yayılma hızı ve dönemler hakkında gerçekçi veriler üretmelisin. " +
                "Yanıtını sadece JSON formatında ver, başka açıklama ekleme. " +
                "Son 6 dönem için veri üret (2023 Q1, 2023 Q2, 2023 Q3, 2023 Q4, 2024 Q1, 2024 Q2). " +
                "Tüm değerler 0'dan büyük olmalıdır. Değerler 5 ile 100 arasında olmalıdır. " +
                "Yanıtını aşağıdaki formatta ver: " +
                "[{\"period\": \"Dönem\", \"rate\": sayı}]"
            ));
            
            messages.add(new com.theokanning.openai.completion.chat.ChatMessage("user", disease + " hastalığı için yayılma hızı ve dönemler"));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .messages(messages)
                .model(MODEL)
                .temperature(0.7)
                .maxTokens(500)
                .build();
            
            ChatCompletionChoice choice = openAiService.createChatCompletion(request).getChoices().get(0);
            String jsonResponse = cleanJsonResponse(choice.getMessage().getContent());
            
            List<GraphicsResponse.SpreadRate> spreadRates = objectMapper.readValue(jsonResponse, 
                new TypeReference<List<GraphicsResponse.SpreadRate>>() {});
            
            // Veri doğrulama - eğer herhangi bir değer 0 ise düzelt
            for (GraphicsResponse.SpreadRate rate : spreadRates) {
                if (rate.getRate() <= 0) {
                    // 10-50 arası rastgele bir değer ata
                    rate.setRate(10 + (int)(Math.random() * 40));
                }
            }
            
            return spreadRates;
        } catch (Exception e) {
            // Hata durumunda manuel veri oluştur
            List<GraphicsResponse.SpreadRate> fallbackData = new ArrayList<>();
            String[] periods = {"2023 Q1", "2023 Q2", "2023 Q3", "2023 Q4", "2024 Q1", "2024 Q2"};
            
            for (String period : periods) {
                // 10-50 arası rastgele değerler
                int rate = 10 + (int)(Math.random() * 40);
                fallbackData.add(new GraphicsResponse.SpreadRate(period, rate));
            }
            
            return fallbackData;
        }
    }
}
