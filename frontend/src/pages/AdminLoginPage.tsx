import { useState } from 'react';
import './AdminLoginPage.css';
import { useNavigate } from 'react-router-dom';
import { adminLogin } from '../services/adminService';

const AdminLoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const res = await adminLogin({ username, password });
            localStorage.setItem('adminToken', res.token);
            setError('');
            navigate('/');
        } catch (err: any) {
            setError(err.response?.data?.message ?? 'Login failed');
        }
    };
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
