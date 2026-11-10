import "./Login.css";

function Login() {
  return (
    <section className="login-page">
      <div className="login-container">
        <div className="login-image">
          <img src="/images/logo_image.jpg" alt="Login" />
        </div>
        <div className="login-form">
          <h2>Login</h2>

          <form>
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email"
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter your password"
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
