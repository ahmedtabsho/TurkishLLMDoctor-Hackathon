import React, { useState, useEffect } from 'react';
import './App.css';
import ChatbotHeader from './components/ChatbotHeader';
import ChatInterface from './components/ChatInterface';
import DocumentPanel from './components/DocumentPanel';
import GraphicsPage from './components/GraphicsPage';

function App() {
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: 'Merhaba! Sağlık asistanınız olarak size nasıl yardımcı olabilirim?',
      sender: 'bot',
      timestamp: new Date().toISOString(),
      isTyping: false,
      fullText: 'Merhaba! Sağlık asistanınız olarak size nasıl yardımcı olabilirim?'
    }
  ]);
  const [isLoading, setIsLoading] = useState(false);
  const [typingSpeed, setTypingSpeed] = useState(10); // ms per character - hızlandırıldı
  
  // Document panel states
  const [showDocPanel, setShowDocPanel] = useState(false);
  const [documents, setDocuments] = useState([]);
  const [documentDisease, setDocumentDisease] = useState('');
  const [isLoadingDocuments, setIsLoadingDocuments] = useState(false);
  
  // Graphics page states
  const [showGraphicsPage, setShowGraphicsPage] = useState(false);
  const [graphicsData, setGraphicsData] = useState(null);
  const [graphicsDisease, setGraphicsDisease] = useState('');
  const [isLoadingGraphics, setIsLoadingGraphics] = useState(false);
  const [showGraphicsNotification, setShowGraphicsNotification] = useState(false);

  // Function to detect disease in a message
  const detectDisease = (message) => {
    // Regular expression to find a word followed by "hastalığı"
    // Using Unicode property escapes for better Turkish character support
    const diseaseRegex = /([\p{L}\p{M}]+)\s+hastalığı/iu;
    const match = message.match(diseaseRegex);
    
    if (match && match[1]) {
      return match[1];
    }
    return null;
  };
  
  // Function to fetch documents for a disease
  const fetchDocuments = async (disease) => {
    if (!disease) return Promise.resolve();
    
    setIsLoadingDocuments(true);
    setShowDocPanel(true);
    setDocumentDisease(disease);
    
    return new Promise(async (resolve) => {
      try {
        const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';
        const response = await fetch(`${apiUrl}/api/documents`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ disease })
        });
        
        const data = await response.json();
        
        if (data.success && data.documents) {
          setDocuments(data.documents);
        } else {
          console.error('Error fetching documents:', data.error);
          setDocuments([]);
        }
      } catch (error) {
        console.error('API error when fetching documents:', error);
        setDocuments([]);
      } finally {
        setIsLoadingDocuments(false);
        resolve();
      }
    });
  };
  
  // Function to fetch graphics data for a disease
  const fetchGraphicsData = async (disease) => {
    if (!disease) return Promise.resolve();
    
    setIsLoadingGraphics(true);
    setGraphicsDisease(disease);
    
    return new Promise(async (resolve) => {
      try {
        const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';
        const response = await fetch(`${apiUrl}/api/graphics`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ disease })
        });
        
        const data = await response.json();
        
        if (data.success) {
          setGraphicsData(data);
          setShowGraphicsNotification(true);
        } else {
          console.error('Error fetching graphics data:', data.error);
          setGraphicsData(null);
        }
      } catch (error) {
        console.error('API error when fetching graphics data:', error);
        setGraphicsData(null);
      } finally {
        setIsLoadingGraphics(false);
        resolve();
      }
    });
  };

  const handleSendMessage = async (message) => {
    if (message.trim() === '') return;
    
    // Add user message
    const userMessage = {
      id: messages.length + 1,
      text: message,
      sender: 'user',
      timestamp: new Date().toISOString()
    };
    
    setMessages(prev => [...prev, userMessage]);
    setIsLoading(true);
    
    // Reset graphics notification
    setShowGraphicsNotification(false);
    
    // Check for disease mention and fetch documents and graphics if found
    const disease = detectDisease(message);
    let documentPromise = null;
    let graphicsPromise = null;
    
    if (disease) {
      documentPromise = fetchDocuments(disease);
      graphicsPromise = fetchGraphicsData(disease);
    }
    
    try {
      // Başlat diğer API çağrılarını ama beklemeden devam et
      if (documentPromise) documentPromise.catch(err => console.error('Document fetch error:', err));
      if (graphicsPromise) graphicsPromise.catch(err => console.error('Graphics fetch error:', err));
      
      // Sadece chat API çağrısını bekle
      const apiUrl = process.env.REACT_APP_API_URL || 'http://localhost:8080';
      const chatResponse = await fetch(`${apiUrl}/api/chat`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message })
      });
      
      const chatData = await chatResponse.json();
      
      if (chatData && chatData.message) {
        const botMessageId = messages.length + 2;
        const fullText = chatData.message.text;
        
        // Hemen mesajın ilk kısmını göster (ilk 50 karakter veya tümü)
        const initialText = fullText.length > 50 ? fullText.substring(0, 50) : fullText;
        
        // Add message with initial text that will be filled character by character
        setMessages(prev => [...prev, {
          id: botMessageId,
          text: initialText,
          sender: 'bot',
          timestamp: chatData.message.timestamp || new Date().toISOString(),
          isTyping: initialText.length < fullText.length,
          fullText: fullText
        }]);
      } else {
        // Handle error
        const errorText = 'Üzgünüm, bir hata oluştu. Lütfen tekrar deneyin.';
        
        setMessages(prev => [...prev, {
          id: messages.length + 2,
          text: errorText,
          sender: 'bot',
          timestamp: new Date().toISOString()
        }]);
      }
    } catch (error) {
      console.error('API error:', error);
      // Fallback response in case of API error
      const errorText = 'Üzgünüm, sunucuya bağlanırken bir hata oluştu. Lütfen internet bağlantınızı kontrol edin ve tekrar deneyin.';
      
      setMessages(prev => [...prev, {
        id: messages.length + 2,
        text: errorText,
        sender: 'bot',
        timestamp: new Date().toISOString()
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  // Effect to handle typing animation for bot messages
  useEffect(() => {
    // Find the first message that is still typing
    const typingMessage = messages.find(msg => msg.isTyping === true);
    
    if (typingMessage) {
      const fullText = typingMessage.fullText;
      const currentText = typingMessage.text;
      
      // If we haven't finished typing the full message
      if (currentText.length < fullText.length) {
        // Set a timeout to add the next character
        const timeoutId = setTimeout(() => {
          setMessages(prevMessages => 
            prevMessages.map(msg => 
              msg.id === typingMessage.id
                ? {
                    ...msg,
                    text: fullText.substring(0, currentText.length + 1),
                    isTyping: currentText.length + 1 < fullText.length
                  }
                : msg
            )
          );
        }, typingSpeed);
        
        return () => clearTimeout(timeoutId);
      } else if (currentText.length === fullText.length) {
        // We've finished typing this message
        // Check if there are any more messages still typing
        const stillTyping = messages.some(msg => 
          msg.id !== typingMessage.id && msg.isTyping === true
        );
        
        // If no more messages are typing, set isLoading to false
        if (!stillTyping) {
          setIsLoading(false);
        }
      }
    }
  }, [messages, typingSpeed]);

  // Function to toggle document panel
  const toggleDocPanel = () => {
    setShowDocPanel(!showDocPanel);
  };
  
  // Function to toggle graphics page
  const toggleGraphicsPage = () => {
    setShowGraphicsPage(!showGraphicsPage);
    if (showGraphicsPage) {
      setShowGraphicsNotification(false);
    }
  };
  
  // Function to show graphics page
  const showGraphics = () => {
    if (graphicsData) {
      setShowGraphicsPage(true);
    }
  };

  return (
    <div className="App">
      {!showGraphicsPage ? (
        // Chat Interface
        <>
          <ChatbotHeader 
            showGraphics={showGraphics} 
            hasGraphicsData={graphicsData !== null} 
            graphicsDisease={graphicsDisease}
            toggleGraphicsPage={toggleGraphicsPage}
            showGraphicsNotification={showGraphicsNotification}
            showGraphicsPage={showGraphicsPage}
            isLoadingGraphics={isLoadingGraphics}
          />
          <div className="app-content">
            {/* Grafik bildirimi ChatbotHeader'a taşındı */}
            
            <div className={`chat-section ${showDocPanel ? 'with-docs' : ''}`}>
              <ChatInterface 
                messages={messages} 
                onSendMessage={handleSendMessage} 
                isLoading={isLoading}
              />
            </div>
            
            {showDocPanel && (
              <div className="doc-section">
                <DocumentPanel 
                  isLoading={isLoadingDocuments}
                  disease={documentDisease}
                  documents={documents}
                />
                <button className="close-doc-panel" onClick={toggleDocPanel}>
                  <span>&times;</span>
                </button>
              </div>
            )}
          </div>
        </>
      ) : (
        // Graphics Page
        <>
          <ChatbotHeader 
            showGraphics={showGraphics} 
            hasGraphicsData={graphicsData !== null} 
            graphicsDisease={graphicsDisease}
            toggleGraphicsPage={toggleGraphicsPage}
            showGraphicsNotification={false}
            showGraphicsPage={showGraphicsPage}
            isLoadingGraphics={isLoadingGraphics}
          />
          <div className="app-content graphics-container">
            <GraphicsPage 
              data={graphicsData} 
              disease={graphicsDisease}
              isLoading={isLoadingGraphics}
              toggleGraphicsPage={toggleGraphicsPage}
            />
          </div>
        </>
      )}
    </div>
  );
}

export default App;
