import { useState } from "react";
import "./App.css";

function App() {
  const [language, setLanguage] = useState("en");

  const translations = {
    en: {
      title: "Medical Appointment System",
      home: "Home",
      doctors: "Doctors",
      appointments: "Appointments",
      login: "Login",
    },
    pl: {
      title: "System Rezerwacji Wizyt Lekarskich",
      home: "Strona główna",
      doctors: "Lekarze",
      appointments: "Wizyty",
      login: "Zaloguj się",
    },
    uk: {
      title: "Система запису до лікаря",
      home: "Головна",
      doctors: "Лікарі",
      appointments: "Записи",
      login: "Увійти",
    },
  };

  return (
    <>
      <header>
        <div className="logo">
          <img src="/images/ikon-header.jpg" alt="Medical logo" />
          <h1>{translations[language].title}</h1>
        </div>

        <nav>
          <a href="#home">{translations[language].home}</a>
          <a href="#doctors">{translations[language].doctors}</a>
          <a href="#appointments">{translations[language].appointments}</a>
          <a href="#login">{translations[language].login}</a>
        </nav>

        <div className="language-switcher">
          <button onClick={() => setLanguage("en")}>🇬🇧</button>
          <button onClick={() => setLanguage("pl")}>🇵🇱</button>
          <button onClick={() => setLanguage("uk")}>🇺🇦</button>
        </div>
      </header>
    </>
  );
}

export default App;
