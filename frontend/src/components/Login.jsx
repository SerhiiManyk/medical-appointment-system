import "./Login.css";
import { useState } from "react";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  async function handleLogin(event) {
    event.preventDefault();
    const csrfResponse = await fetch("http://localhost:8080/api/auth/csrf", {
      credentials: "include",
    });
    if (!csrfResponse.ok) {
      console.error("ERROR RESPONSE");
      return;
    }

    const cookies = document.cookie;
    const xsrfToken = cookies
      .split("; ")
      .find((cookie) => cookie.startsWith("XSRF-TOKEN="))
      .split("=")[1];
  }

  return (
    <section className="login-page">
      <div className="login-container">
        <div className="login-image">
          <img src="/images/logo_image.jpg" alt="Login" />
        </div>
        <div className="login-form">
          <h2>Login</h2>

          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
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
                onChange={(event) => setPassword(event.target.value)}
              />
            </div>

            <button type="submit" className="login-button">
              Login
            </button>
          </form>
        </div>
      </div>
    </section>
  );
}

export default Login;
