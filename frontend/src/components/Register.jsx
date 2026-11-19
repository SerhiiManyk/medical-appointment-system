import { useState } from "react";
import "./Register.css";

function Register({ language, translations }) {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [gender, setGender] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    const patientData = {
      firstName,
      lastName,
      email,
      phoneNumber: phone,
      gender,
      dateOfBirth,
      password,
    };
    setIsLoading(true);
    try {
      const csrfResponse = await fetch("http://localhost:8080/api/auth/csrf", {
        credentials: "include",
      });
      if (!csrfResponse.ok) {
        console.error("ERROR RESPONSE");
        setError("Unable to initialize secure registration. Please try again.");
        return;
      }

      const cookies = document.cookie;
      const xsrfToken = cookies
        .split("; ")
        .find((cookie) => cookie.startsWith("XSRF-TOKEN="))
        .split("=")[1];

      const response = await fetch("http://localhost:8080/api/patients", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          "X-XSRF-TOKEN": xsrfToken,
        },
        body: JSON.stringify(patientData),
      });
      if (!response.ok) {
        console.error("Registration failed");
        setError("Registration failed. Please try again.");
        return;
      }
      const responseData = await response.json();
      console.log(responseData);
    } catch (error) {
      console.error("REGISTER FETCH ERROR:", error);
      setError("Unable to create a new profile. Please try again later.");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="register-container">
      <div className="register-description">
        <h2>{translations[language].welcome}</h2>
        <p>{translations[language].registrationDescription}</p>
      </div>
      <div className="register-card">
        <div className="register-title">
          <h1>{translations[language].register}</h1>
        </div>

        {error && <p className="error-message">{error}</p>}

        <form className="register-form" onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="First name"
            value={firstName}
            onChange={(event) => {
              setFirstName(event.target.value);
              setError("");
            }}
          />
          <input
            type="text"
            placeholder="Last name"
            value={lastName}
            onChange={(event) => {
              setLastName(event.target.value);
              setError("");
            }}
          />
          <input
            type="email"
            id="email"
            name="email"
            placeholder="Enter your email"
            value={email}
            onChange={(event) => {
              setEmail(event.target.value);
              setError("");
            }}
          />
          <input
            type="tel"
            placeholder="Your phone number"
            value={phone}
            onChange={(event) => {
              setPhone(event.target.value);
              setError("");
            }}
          />
          <select
            value={gender}
            onChange={(event) => {
              setGender(event.target.value);
              setError("");
            }}
          >
            <option value="">Choose your gender</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </select>
          <input
            type="date"
            placeholder="Date of Birth"
            value={dateOfBirth}
            onChange={(event) => {
              setDateOfBirth(event.target.value);
              setError("");
            }}
          />
          <input
            type="password"
            id="password"
            name="password"
            placeholder="Create password"
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
              setError("");
            }}
          />
          <button type="submit" disabled={isLoading}>
            {isLoading ? "Registering..." : "Register"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Register;
