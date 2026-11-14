import "./Login.css";
import { useState } from "react";

function Login({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function handleLogin(event) {
    event.preventDefault();

    setError("");

    setIsLoading(true);

    try {
      const csrfResponse = await fetch("http://localhost:8080/api/auth/csrf", {
        credentials: "include",
      });
      if (!csrfResponse.ok) {
        console.error("ERROR RESPONSE");
        setError("Unable to initialize secure login. Please try again.");
        return;
      }

      const cookies = document.cookie;
      const xsrfToken = cookies
        .split("; ")
        .find((cookie) => cookie.startsWith("XSRF-TOKEN="))
        .split("=")[1];

      const loginRequest = {
        email: email,
        password: password,
      };

      const body = JSON.stringify(loginRequest);

      const loginResponse = await fetch(
        "http://localhost:8080/api/auth/login",
        {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": xsrfToken,
          },
          body: body,
        },
      );

      const responseBody = await loginResponse.json();

      if (!loginResponse.ok) {
        console.error("ERROR RESPONSE");
        setError(responseBody.message);
        return;
      }

      const authResponse = await loginResponse.json();
      onLogin(authResponse);
    } catch (error) {
      console.error(error);
      setError("Unable to connect to the server. Please try again later.");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <section className="login-page">
      <div className="login-container">
        <div className="login-image">
          <img src="/images/logo_image.jpg" alt="Login" />
        </div>
        <div className="login-form">
          <h2>Login</h2>

          {error && <p className="error-message">{error}</p>}

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label htmlFor="email">Email</label>
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
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter your password"
                value={password}
                onChange={(event) => {
                  setPassword(event.target.value);
                  setError("");
                }}
              />
            </div>

            <button type="submit" className="login-button" disabled={isLoading}>
              {isLoading ? "Logging in..." : "Login"}
            </button>
          </form>
        </div>
      </div>
    </section>
  );
}

export default Login;
