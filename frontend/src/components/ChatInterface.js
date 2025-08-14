import React, { useState, useRef, useEffect } from 'react';

const ChatInterface = ({ messages, onSendMessage, isLoading }) => {
  const [inputMessage, setInputMessage] = useState('');
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);
  const inputRef = useRef(null);
  
  // Auto-scroll to bottom of messages
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };
  
  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]);
  
  useEffect(() => {
    // Focus input on component mount
    inputRef.current?.focus();
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (inputMessage.trim() && !isLoading) {
      onSendMessage(inputMessage);
      setInputMessage('');
    }
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };
  
  // Function to render message with markdown formatting
  const renderMessageText = (text) => {
    return text;
  };

  return (
    <div className="chat-interface">
      <div className="chat-container">
        <div className="messages-container" ref={messagesContainerRef}>
          {messages.map((message) => (
            <div 
              key={message.id} 
              className={`message-row ${message.sender === 'bot' ? 'bot-row' : 'user-row'}`}
            >
              <div className="message-wrapper">
                {message.sender === 'bot' && (
                  <div className="avatar bot-avatar">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2ZM12 7C9.23858 7 7 9.23858 7 12C7 14.7614 9.23858 17 12 17C14.7614 17 17 14.7614 17 12C17 9.23858 14.7614 7 12 7Z" fill="url(#paint0_linear)"/>
                      <defs>
                        <linearGradient id="paint0_linear" x1="2" y1="2" x2="22" y2="22" gradientUnits="userSpaceOnUse">
                          <stop stopColor="#3A47D5"/>
                          <stop offset="1" stopColor="#00D2FF"/>
                        </linearGradient>
                      </defs>
                    </svg>
                  </div>
                )}
                
                <div className={`message ${message.sender === 'bot' ? 'bot-message' : 'user-message'}`}>
                  <div className="message-bubble">
                    <div className="message-content">
                      <p>
                        {renderMessageText(message.text)}
                        {message.isTyping && <span className="cursor"></span>}
                      </p>
                    </div>
                  </div>
                  <div className="message-footer">
                    <span className="message-time">{formatTime(message.timestamp)}</span>
                  </div>
                </div>
                
                {message.sender === 'user' && (
                  <div className="avatar user-avatar">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="#3A47D5"/>
                    </svg>
                  </div>
                )}
              </div>
            </div>
          ))}
          
          {/* Typing indicator */}
          {isLoading && !messages.some(msg => msg.isTyping) && (
            <div className="message-row bot-row">
              <div className="message-wrapper">
                <div className="avatar bot-avatar">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2ZM12 7C9.23858 7 7 9.23858 7 12C7 14.7614 9.23858 17 12 17C14.7614 17 17 14.7614 17 12C17 9.23858 14.7614 7 12 7Z" fill="url(#paint0_linear)"/>
                    <defs>
                      <linearGradient id="paint0_linear" x1="2" y1="2" x2="22" y2="22" gradientUnits="userSpaceOnUse">
                        <stop stopColor="#3A47D5"/>
                        <stop offset="1" stopColor="#00D2FF"/>
                      </linearGradient>
                    </defs>
                  </svg>
                </div>
                <div className="message bot-message">
                  <div className="message-bubble">
                    <div className="message-content">
                      <div className="typing-dots">
                        <span></span>
                        <span></span>
                        <span></span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
          
          <div ref={messagesEndRef} />
        </div>
      </div>
      
      {/* Input Area */}
      <div className="input-container">
        <form className="message-input-form" onSubmit={handleSubmit}>
          <input
            type="text"
            ref={inputRef}
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            placeholder="Sağlık sorunuzu yazın..."
            className="message-input"
            disabled={isLoading}
          />
          <button 
            type="submit" 
            className={`send-button ${(!inputMessage.trim() || isLoading) ? 'disabled' : ''}`}
            disabled={!inputMessage.trim() || isLoading}
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 2L11 13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>
        </form>
        
        <div className="input-footer">
          <div className="disclaimer">
            Nükleotit AI sağlık bilgilendirmesi yapabilir ancak tıbbi tanı ve tedavi için doktorunuza danışın.
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatInterface;
