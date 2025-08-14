import React from 'react';
import { Bar, Line, Pie } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const GraphicsPage = ({ data, disease, isLoading, toggleGraphicsPage }) => {
  // If loading or no data, show loading spinner
  if (isLoading || !data) {
    return (
      <div className="graphics-page">
        <div className="graphics-loading">
          <div className="spinner"></div>
          <h2>Grafikler yükleniyor...</h2>
        </div>
      </div>
    );
  }

  // Prepare data for charts
  const drugProducingCountriesData = {
    labels: data.drugProducingCountries?.map(item => item.country) || [],
    datasets: [
      {
        label: 'İlaç Sayısı',
        data: data.drugProducingCountries?.map(item => item.drugCount) || [],
        backgroundColor: 'rgba(54, 162, 235, 0.7)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
      }
    ]
  };

  const yearlyProductionData = {
    labels: data.yearlyProduction?.map(item => item.year) || [],
    datasets: [
      {
        label: 'Üretim Miktarı',
        data: data.yearlyProduction?.map(item => item.production) || [],
        fill: false,
        backgroundColor: 'rgba(40, 167, 69, 0.7)',
        borderColor: 'rgba(40, 167, 69, 1)',
        tension: 0.1
      }
    ]
  };

  const riskFactorsData = {
    labels: data.riskFactors?.map(item => item.factor) || [],
    datasets: [
      {
        label: 'Risk Faktörleri',
        data: data.riskFactors?.map(item => item.percentage) || [],
        backgroundColor: [
          'rgba(255, 99, 132, 0.7)',
          'rgba(54, 162, 235, 0.7)',
          'rgba(255, 206, 86, 0.7)',
          'rgba(75, 192, 192, 0.7)',
          'rgba(153, 102, 255, 0.7)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 1
      }
    ]
  };

  const spreadRateData = {
    labels: data.spreadRate?.map(item => item.period) || [],
    datasets: [
      {
        label: 'Yayılma Hızı',
        data: data.spreadRate?.map(item => item.rate) || [],
        fill: true,
        backgroundColor: 'rgba(220, 53, 69, 0.5)',
        borderColor: 'rgba(220, 53, 69, 1)',
        tension: 0.4
      }
    ]
  };
  
  // İlaç Fiyatları Grafiği için veri
  const drugPricesData = {
    labels: data.drugPrices?.map(item => item.drugName) || [],
    datasets: [
      {
        label: 'İlaç Fiyatları (TL)',
        data: data.drugPrices?.map(item => item.price) || [],
        backgroundColor: 'rgba(153, 102, 255, 0.7)',
        borderColor: 'rgba(153, 102, 255, 1)',
        borderWidth: 1
      }
    ]
  };
  
  // Chart options - Chatbot UI ile uyumlu tema
  const chartTheme = {
    color: 'rgba(60, 60, 80, 0.8)',
    font: {
      family: '"Poppins", "Roboto", "Helvetica", "Arial", sans-serif',
      size: 12
    },
    grid: {
      color: 'rgba(200, 200, 200, 0.1)'
    }
  };
  
  const barOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          color: chartTheme.color,
          font: chartTheme.font,
          boxWidth: 15,
          padding: 15
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        titleColor: '#3A47D5',
        bodyColor: '#333',
        borderColor: 'rgba(58, 71, 213, 0.2)',
        borderWidth: 1,
        cornerRadius: 8,
        boxPadding: 6
      }
    },
    scales: {
      x: {
        grid: {
          display: false
        },
        ticks: {
          color: chartTheme.color,
          font: chartTheme.font
        }
      },
      y: {
        grid: {
          color: chartTheme.grid.color
        },
        ticks: {
          color: chartTheme.color,
          font: chartTheme.font
        }
      }
    },
    animation: {
      duration: 1000,
      easing: 'easeOutQuart'
    }
  };
  
  const lineOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          color: chartTheme.color,
          font: chartTheme.font,
          boxWidth: 15,
          padding: 15
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        titleColor: '#3A47D5',
        bodyColor: '#333',
        borderColor: 'rgba(58, 71, 213, 0.2)',
        borderWidth: 1,
        cornerRadius: 8,
        boxPadding: 6
      }
    },
    scales: {
      x: {
        grid: {
          display: false
        },
        ticks: {
          color: chartTheme.color,
          font: chartTheme.font
        }
      },
      y: {
        grid: {
          color: chartTheme.grid.color
        },
        ticks: {
          color: chartTheme.color,
          font: chartTheme.font
        }
      }
    },
    animation: {
      duration: 1000,
      easing: 'easeOutQuart'
    }
  };
  
  const pieOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          color: chartTheme.color,
          font: chartTheme.font,
          boxWidth: 15,
          padding: 15
        }
      },
      tooltip: {
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        titleColor: '#3A47D5',
        bodyColor: '#333',
        borderColor: 'rgba(58, 71, 213, 0.2)',
        borderWidth: 1,
        cornerRadius: 8,
        boxPadding: 6
      }
    },
    animation: {
      animateRotate: true,
      animateScale: true,
      duration: 1000,
      easing: 'easeOutQuart'
    }
  };

  return (
    <div className="graphics-page">
      <div className="graphics-header">
        <h1 className="graphics-title">{disease} Hastalığı Grafik Verileri</h1>
        <button className="back-to-chat-btn" onClick={toggleGraphicsPage}>
          &larr; Sohbete Geri Dön
        </button>
      </div>
      
      <div className="graphics-grid">
        {/* Bar Chart - Drug Producing Countries */}
        <div className="chart-container">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M4 11H2v3h2v-3zm5-4H7v7h2V7zm5-5v12h-2V2h2zm-2-1a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1h-2zM6 7a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v7a1 1 0 0 1-1 1H7a1 1 0 0 1-1-1V7zm-5 4a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1v-3z"/>
              </svg>
            </span>
            İlaç Türü Üreten Ülkeler
          </h2>
          <div className="chart-wrapper">
            <Bar data={drugProducingCountriesData} options={barOptions} />
          </div>
        </div>
        
        {/* Countries with Drug List */}
        <div className="chart-container">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0ZM4.882 1.731a.482.482 0 0 0 .14.291.487.487 0 0 1-.126.78l-.291.146a.721.721 0 0 0-.188.135l-.48.48a1 1 0 0 1-1.023.242l-.02-.007a.996.996 0 0 0-.462-.04 7.03 7.03 0 0 1 2.45-2.027Zm-3 9.674.86-.216a1 1 0 0 0 .758-.97v-.184a1 1 0 0 1 .445-.832l.04-.026a1 1 0 0 0 .152-1.54L3.121 6.621a.414.414 0 0 1 .542-.624l1.09.818a.5.5 0 0 0 .523.047.5.5 0 0 1 .724.447v.455a.78.78 0 0 0 .131.433l.795 1.192a1 1 0 0 1 .116.238l.73 2.19a1 1 0 0 0 .949.683h.058a1 1 0 0 0 .949-.684l.73-2.189a1 1 0 0 1 .116-.238l.791-1.187A.454.454 0 0 1 11.743 8c.16 0 .306.084.392.218.557.875 1.63 2.282 2.365 2.282a.61.61 0 0 0 .04-.001 7.003 7.003 0 0 1-12.658.905Z"/>
              </svg>
            </span>
            İlacın Bulunduğu Ülkeler
          </h2>
          <div className="countries-grid">
            {data.countriesWithDrug?.map((country, index) => (
              <div key={index} className="country-badge">
                {country}
              </div>
            ))}
          </div>
        </div>
        
        {/* Line Chart - Yearly Production */}
        <div className="chart-container">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M0 0h1v15h15v1H0V0Zm10 3.5a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 .5.5v4a.5.5 0 0 1-1 0V4.9l-3.613 4.417a.5.5 0 0 1-.74.037L7.06 6.767l-3.656 5.027a.5.5 0 0 1-.808-.588l4-5.5a.5.5 0 0 1 .758-.06l2.609 2.61L13.445 4H10.5a.5.5 0 0 1-.5-.5Z"/>
              </svg>
            </span>
            İlacın Yıllık Üretim Miktarı
          </h2>
          <div className="chart-wrapper">
            <Line data={yearlyProductionData} options={lineOptions} />
          </div>
        </div>
        
        {/* Spread Rate Chart */}
        <div className="chart-container">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fillRule="evenodd" d="M8 3a5 5 0 1 1-4.546 2.914.5.5 0 0 0-.908-.417A6 6 0 1 0 8 2v1z"/>
                <path d="M8 4.466V.534a.25.25 0 0 0-.41-.192L5.23 2.308a.25.25 0 0 0 0 .384l2.36 1.966A.25.25 0 0 0 8 4.466z"/>
              </svg>
            </span>
            Yayılma Hızı
          </h2>
          <div className="chart-wrapper">
            <Line data={spreadRateData} options={lineOptions} />
          </div>
        </div>
        
        {/* Risk Factors Chart */}
        <div className="chart-container">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
              </svg>
            </span>
            Risk Faktörleri
          </h2>
          <div className="chart-wrapper">
            <Pie data={riskFactorsData} options={pieOptions} />
          </div>
        </div>
        
        {/* Drug Prices Chart */}
        <div className="chart-container">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M8 10a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"/>
                <path d="M0 4a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H1a1 1 0 0 1-1-1V4zm3 0a2 2 0 0 1-2 2v4a2 2 0 0 1 2 2h10a2 2 0 0 1 2-2V6a2 2 0 0 1-2-2H3z"/>
              </svg>
            </span>
            İlaç Fiyatları
          </h2>
          <div className="chart-wrapper">
            <Bar data={drugPricesData} options={barOptions} />
          </div>
        </div>
        
        {/* Scientists Table */}
        <div className="chart-container scientists-table">
          <h2>
            <span className="chart-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0Zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4Zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10c-2.29 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10Z"/>
              </svg>
            </span>
            Bu Alanda Çalışan Bilim İnsanları
          </h2>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>İsim</th>
                  <th>Kurum</th>
                  <th>E-posta</th>
                  <th>Telefon</th>
                  <th>Ülke</th>
                </tr>
              </thead>
              <tbody>
                {data.scientists?.map((scientist, index) => (
                  <tr key={index}>
                    <td>{scientist.name}</td>
                    <td>{scientist.institution}</td>
                    <td>{scientist.email}</td>
                    <td>{scientist.phone}</td>
                    <td>{scientist.country}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GraphicsPage;
