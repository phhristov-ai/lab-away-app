import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { jwtDecode } from 'jwt-decode';

type AdminContextType = {
    isAdmin: boolean;
    logout: () => void;
};

interface JwtPayload {
  username: string;
  role: string;
  exp: number;
}

const AdminContext = createContext<AdminContextType>({ isAdmin: false, logout: () => { } });

export const AdminProvider = ({ children }: { children: React.ReactNode }) => {
    const [isAdmin, setIsAdmin] = useState(false);
    useEffect(() => {
        const token = localStorage.getItem('adminToken');
        if (token) {
            try {
        
                const decoded = jwtDecode<JwtPayload>(token);
                setIsAdmin(decoded?.role === 'ADMIN');
            } catch {
                setIsAdmin(false);
            }
        }
    }, []);

    const logout = () => {
        localStorage.removeItem('adminToken');
        setIsAdmin(false);
    };
    const memoizedValue = useMemo(() => ({ isAdmin, logout }), [isAdmin, logout]);

    return (
        <AdminContext.Provider value={memoizedValue}>
            {children}
        </AdminContext.Provider>
    );
};

export const useAdmin = () => useContext(AdminContext);
