import React from 'react';

const ChatbotHeader = ({ showGraphics, hasGraphicsData, graphicsDisease, toggleGraphicsPage, showGraphicsNotification, showGraphicsPage, isLoadingGraphics }) => {
  return (
    <header className="chatbot-header">
      <div className="header-left">
        <div className="logo">
          <div className="logo-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2ZM12 7C9.23858 7 7 9.23858 7 12C7 14.7614 9.23858 17 12 17C14.7614 17 17 14.7614 17 12C17 9.23858 14.7614 7 12 7ZM12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9Z" fill="url(#paint0_linear)"/>
              <defs>
                <linearGradient id="paint0_linear" x1="2" y1="2" x2="22" y2="22" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#3A47D5"/>
                  <stop offset="1" stopColor="#00D2FF"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div className="logo-text">
            <h1>Nükleotit<span className="badge">AI</span></h1>
            <p>Sağlık Analiz Asistanınız</p>
          </div>
        </div>
      </div>
      <div className="header-right">
        {hasGraphicsData && showGraphicsNotification && (
          <div className="header-graphics-notification">
            <div className="notification-content">
              <div className="notification-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 3v18h18"></path>
                  <path d="M18 17V9"></path>
                  <path d="M13 17V5"></path>
                  <path d="M8 17v-3"></path>
                </svg>
              </div>
              <span><strong>{graphicsDisease}</strong> hastalığı için grafikler hazır</span>
            </div>
            <button className="view-graphics-btn" onClick={toggleGraphicsPage}>
              Görüntüle
            </button>
          </div>
        )}
        
        {/* Grafik butonu sadece veri hazır olduğunda ve yükleme tamamlandığında aktif olsun */}
        {hasGraphicsData && !isLoadingGraphics && (
          <button 
            className="header-button graphics-button" 
            onClick={toggleGraphicsPage}
            title={showGraphicsPage ? `Sohbet ekranına geri dön` : `${graphicsDisease} hastalığı grafiklerini görüntüle`}
          >
            {showGraphicsPage ? (
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M19 12H5"></path>
                <path d="M12 19l-7-7 7-7"></path>
              </svg>
            ) : (
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M3 3v18h18"></path>
                <path d="M18 17V9"></path>
                <path d="M13 17V5"></path>
                <path d="M8 17v-3"></path>
              </svg>
            )}
          </button>
        )}

      </div>
    </header>
  );
};

export default ChatbotHeader;
