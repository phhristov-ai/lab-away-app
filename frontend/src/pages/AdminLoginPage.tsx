import './AdminLoginPage.css';
import { useAdminLogin } from '../hooks/useAdminLogin';

const AdminLoginPage = () => {
  const {
    username,
    setUsername,
    password,
    setPassword,
    error,
    handleLogin,
  } = useAdminLogin();

  return (
    <div className="admin-login-wrapper">
      <div className="admin-login-container">
        <h2>Admin Login</h2>
        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Login</button>
          {error && <p className="error-msg">{error}</p>}
        </form>
      </div>
    </div>
  );
};

export default AdminLoginPage;