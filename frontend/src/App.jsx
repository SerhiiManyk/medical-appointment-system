import { useState, useEffect } from "react";
import "./App.css";
import SpecializationSection from "./components/SpecializationSection";
import Login from "./components/Login";

function App() {
  const [language, setLanguage] = useState("en");
  const [showLogin, setShowLogin] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    async function checkSession() {
      try {
        const response = await fetch("http://localhost:8080/api/auth/me", {
          method: "GET",
          credentials: "include",
        });
        if (!response.ok) {
          console.error("ERROR RESPONSE");
          setUser(null);
          return;
        } else {
          const authResponse = await response.json();
          setUser(authResponse);
        }
      } catch (error) {
        console.error(error);
        setUser(null);
      }
    }
    checkSession();
  }, []);

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
      logout: "Logout",
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
      logout: "Wyloguj",
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
      logout: "Вийти",
    },
  };

  function handleLoginSuccess(authResponse) {
    setUser(authResponse);
    setShowLogin(false);
  }

  async function handleLogout() {
    try {
      const cookies = document.cookie;
      const xsrfCookie = cookies
        .split("; ")
        .find((cookie) => cookie.startsWith("XSRF-TOKEN="));
      const xsrfToken = xsrfCookie?.split("=")[1];

      const response = await fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include",
        headers: {
          "X-XSRF-TOKEN": xsrfToken,
        },
      });

      if (!response.ok) {
        console.error("Logout failed:", response.status);
        return;
      }

      setUser(null);
      setShowLogin(false);
    } catch (error) {
      console.error("Logout error:", error);
    }
  }

  return (
    <>
      <header>
        <div className="logo">
          <img src="/images/ikon-header.jpg" alt="Medical logo" />
          <h1>{translations[language].title}</h1>
        </div>

        {user && (
          <>
            <div className="user-name">
              {`${user.firstName} ${user.lastName}`}
            </div>
            <div className="user-role">{user.role}</div>
          </>
        )}

        <nav>
          <button onClick={() => setShowLogin(false)}>
            {translations[language].home}
          </button>
          <a href="#doctors">{translations[language].doctors}</a>
          <a href="#appointments">{translations[language].appointments}</a>
          <button onClick={() => (user ? handleLogout() : setShowLogin(true))}>
            {user
              ? translations[language].logout
              : translations[language].login}
          </button>
        </nav>

        <div className="language-switcher">
          <button onClick={() => setLanguage("en")}>🇬🇧</button>
          <button onClick={() => setLanguage("pl")}>🇵🇱</button>
          <button onClick={() => setLanguage("uk")}>🇺🇦</button>
        </div>
      </header>

      <main>
        {showLogin ? (
          <Login onLogin={handleLoginSuccess} />
        ) : (
          <>
            <section className="hero">
              <h2>{translations[language].welcome}</h2>
              <p>{translations[language].description}</p>
              <button>{translations[language].bookAppointment}</button>
            </section>

            <SpecializationSection
              language={language}
              translations={translations}
            />
          </>
        )}
      </main>
    </>
  );
}

export default App;
