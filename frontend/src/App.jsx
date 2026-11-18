import { useState, useEffect } from "react";
import "./App.css";
import SpecializationSection from "./components/SpecializationSection";
import Login from "./components/Login";
import Register from "./components/Register";
import EditProfile from "./components/EditProfile";

function App() {
  const [language, setLanguage] = useState("en");
  const [showLogin, setShowLogin] = useState(false);
  const [user, setUser] = useState(null);
  const [showRegister, setShowRegister] = useState(false);
  const [showEditProfile, setShowEditProfile] = useState(false);

  useEffect(() => {
    async function checkSession() {
      try {
        const response = await fetch("http://localhost:8080/api/auth/me", {
          method: "GET",
          credentials: "include",
        });
        if (response.status === 401 || response.status === 403) {
          setUser(null);
          return;
        } else if (!response.ok) {
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
      register: "Register",
      editProfile: "Edit Profile",
      registrationDescription:
        "Create your patient account in a few simple steps. Fill in your personal details and contact information, then choose a secure password. After registration, you will be able to book medical appointments.",
      welcome: "Welcome to our Medical Appointment System",
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
      register: "Zarejestruj się",
      editProfile: "Edytuj profil",
      registrationDescription:
        "Utwórz konto pacjenta w kilku prostych krokach. Podaj swoje dane osobowe i kontaktowe, a następnie wybierz bezpieczne hasło. Po rejestracji będziesz mógł rezerwować wizyty lekarskie.",
      welcome: "Witamy w naszym systemie rezerwacji wizyt lekarskich",
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
      register: "Реєстрація",
      editProfile: "Редагувати профіль",
      registrationDescription:
        "Створіть обліковий запис пацієнта за кілька простих кроків. Заповніть особисті та контактні дані, а потім придумайте надійний пароль. Після реєстрації ви зможете записуватися на прийом до лікаря.",
      welcome: "Ласкаво просимо до системи медичних записів",
    },
  };

  function handleLoginSuccess(authResponse) {
    setUser(authResponse);
    setShowLogin(false);
  }

  async function handleLogout() {
    try {
      const csrfResponse = await fetch("http://localhost:8080/api/auth/csrf", {
        credentials: "include",
      });

      if (!csrfResponse.ok) {
        console.error("Unable to initialize CSRF");
        return;
      }

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
      setShowRegister(false);
    } catch (error) {
      console.error("Logout error:", error);
    }
  }

  function onBack() {
    setShowEditProfile(false);
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
          <button
            onClick={() => {
              if (!user) {
                setShowRegister(true);
                setShowLogin(false);
                setShowEditProfile(false);
              } else if (user.role === "PATIENT") {
                setShowEditProfile(true);
                setShowRegister(false);
                setShowLogin(false);
              }
            }}
          >
            {!user && translations[language].register}

            {user &&
              user.role === "PATIENT" &&
              translations[language].editProfile}
          </button>
          <a href="#doctors">{translations[language].doctors}</a>
          <a href="#appointments">{translations[language].appointments}</a>
          <button
            onClick={() => {
              if (user) {
                handleLogout();
              } else {
                setShowLogin(true);
                setShowRegister(false);
              }
            }}
          >
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
        ) : showRegister ? (
          <Register language={language} translations={translations} />
        ) : showEditProfile ? (
          <EditProfile
            language={language}
            translations={translations}
            onBack={onBack}
          />
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
