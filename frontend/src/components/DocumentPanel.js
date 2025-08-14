import React from 'react';

const DocumentPanel = ({ isLoading, disease, documents }) => {
  return (
    <div className="document-panel">
      <div className="document-panel-header">
        <h3>Makaleler</h3>
        {disease && <span className="disease-tag">{disease} hastalığı</span>}
      </div>
      
      <div className="document-panel-content">
        {isLoading ? (
          <div className="document-loading">
            <div className="document-loading-spinner"></div>
            <p>Makaleler yükleniyor...</p>
          </div>
        ) : documents && documents.length > 0 ? (
          <div className="document-list">
            {documents.map((doc, index) => (
              <div key={index} className="document-item">
                <h4 className="document-title">
                  <a href={doc.link} target="_blank" rel="noopener noreferrer">
                    {doc.title}
                  </a>
                </h4>
                <p className="document-description">{doc.description}</p>
                <div className="document-meta">
                  <span className="document-source">Kaynak: {doc.source}</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="document-empty">
            <p>Henüz makale bulunmuyor.</p>
            <p className="document-tip">
              Sağlık sorunuzu yazarken "hastalığı" kelimesini kullanarak makaleleri görüntüleyebilirsiniz.
              <br />
              Örnek: "Diyabet hastalığı hakkında bilgi verir misin?"
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default DocumentPanel;
