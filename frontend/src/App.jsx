import { useState } from "react";
import "./App.css";
import SpecializationSection from "./components/SpecializationSection";

function App() {
  const [language, setLanguage] = useState("en");

  const translations = {
    en: {
      title: "Medical Appointment System",
      home: "Home",
      doctors: "Doctors",
      appointments: "Appointments",
      login: "Login",
      welcome: "Welcome to our medical platform",
      description: "Book your appointment with a doctor quickly and easily.",
      bookAppointment: "Book an appointment",
      doctorsTitle: "Our Doctors",
      specializationsTitle: "Our Specializations",
    },
    pl: {
      title: "System Rezerwacji Wizyt Lekarskich",
      home: "Strona główna",
      doctors: "Lekarze",
      appointments: "Wizyty",
      login: "Zaloguj się",
      welcome: "Witamy na naszej platformie medycznej",
      description: "Umów wizytę u lekarza szybko i łatwo.",
      bookAppointment: "Umów wizytę",
      doctorsTitle: "Nasi lekarze",
      specializationsTitle: "Nasze specjalizacje",
    },
    uk: {
      title: "Система запису до лікаря",
      home: "Головна",
      doctors: "Лікарі",
      appointments: "Записи",
      login: "Увійти",
      welcome: "Ласкаво просимо до нашої медичної платформи",
      description: "Запишіться на прийом до лікаря швидко та легко.",
      bookAppointment: "Записатися на прийом",
      doctorsTitle: "Наші лікарі",
      specializationsTitle: "Наші спеціалізації",
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

      <main>
        <section className="hero">
          <h2>{translations[language].welcome}</h2>
          <p>{translations[language].description}</p>
          <button>{translations[language].bookAppointment}</button>
        </section>

        <SpecializationSection
          language={language}
          translations={translations}
        />
      </main>
    </>
  );
}

export default App;
